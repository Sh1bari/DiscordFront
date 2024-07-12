package ru.alfaintegral.deviceProduction.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import lombok.*;
import org.springframework.stereotype.Service;

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

    private final ObjectMapper objectMapper;
    private final Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    private final HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    return objectMapper.readValue(response.body(), responseType);
                } else {
                    throw new RuntimeException("Failed to fetch data: " + response.statusCode());
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    public <T> void executeRequest(HttpRequest request, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        sendRequest(request, responseType)
                .thenAccept(response -> Platform.runLater(() -> onSuccess.accept(response)))
                .exceptionally(e -> {
                    Platform.runLater(() -> onError.accept((Exception) e));
                    return null;
                });
    }

    public <T> void get(String url, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        executeRequest(request, responseType, onSuccess, onError);
    }

    public <T> void post(String url, String jsonBody, Class<T> responseType, Consumer<T> onSuccess, Consumer<Exception> onError) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        executeRequest(request, responseType, onSuccess, onError);
    }
}
