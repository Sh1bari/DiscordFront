package ru.alfaintegral.deviceProduction.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import lombok.*;
import org.springframework.stereotype.Service;
import ru.alfaintegral.deviceProduction.exceptions.AppError;
import ru.alfaintegral.deviceProduction.exceptions.GeneralException;
import ru.alfaintegral.deviceProduction.models.api.responses.Login.JwtTokenDtoRes;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final TokenService tokenService;

    private final ObjectMapper objectMapper;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    if (responseType == String.class) {
                        return responseType.cast(response.body());
                    }
                    return objectMapper.readValue(response.body(), responseType);
                } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                    post("http://localhost:8080/api/auth/refresh",new JwtTokenDtoRes(null, tokenService.getToken().getRefreshToken()), JwtTokenDtoRes.class, res -> {
                        tokenService.saveToken(new JwtTokenDtoRes());
                        sendRequest(request, responseType);
                    }, e -> {
                        throw new GeneralException(401, "Unauthorized");
                    });
                } else {
                    AppError err = objectMapper.readValue(response.body(), AppError.class);
                    throw new GeneralException(err.getStatus(), err.getMessage());
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        }, executor);
    }

    public <T> void executeRequest(HttpRequest request, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        sendRequest(request, responseType)
                .thenAccept(response -> Platform.runLater(() -> onSuccess.accept(response)))
                .exceptionally(e -> {
                    Platform.runLater(() -> onError.accept((Exception) e.getCause()));
                    return null;
                });
    }

    public <T> void get(String url, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();
        if(tokenService.getToken().getAccessToken()!=null){
            requestBuilder.header("Authorization", "Bearer " + tokenService.getToken().getAccessToken());
        }
        executeRequest(requestBuilder.build(), responseType, onSuccess, onError);
    }

    private  <T> void post(String url, String jsonBody, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
        if(tokenService.getToken().getAccessToken()!=null){
            requestBuilder.header("Authorization", "Bearer " + tokenService.getToken().getAccessToken());
        }
        executeRequest(requestBuilder.build(), responseType, onSuccess, onError);
    }

    public <T, B> void post(String url, B body, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            post(url, jsonBody, responseType, onSuccess, onError);
        } catch (IOException e) {
            Platform.runLater(() -> onError.accept(e));
        }
    }
}
