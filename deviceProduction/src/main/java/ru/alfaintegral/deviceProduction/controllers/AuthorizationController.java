package ru.alfaintegral.deviceProduction.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.api.requests.Login.UserDtoReq;
import ru.alfaintegral.deviceProduction.models.api.responses.Login.RegisterUserDtoRes;
import ru.alfaintegral.deviceProduction.services.ApiService;
import ru.alfaintegral.deviceProduction.services.SceneService;
import ru.alfaintegral.deviceProduction.services.TokenService;

@Component
@RequiredArgsConstructor
public class AuthorizationController {
    private final ApiService apiService;
    private final TokenService tokenService;
    private final SceneService sceneService;

    @FXML
    private TextField mailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorMessage;

    @FXML
private void initialize() {
    if (tokenService.getToken() != null && tokenService.getToken().getAccessToken() != null) {
        apiService.get("http://localhost:8081/api/main/user/me", UserDtoReq.class, res -> {
            sceneService.switchScene((Stage) loginButton.getScene().getWindow(), "/MainScene.fxml", "Main Scene");
        }, e -> {
            errorMessage.setText("Session expired, please login again.");
        });
    }
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String mail = mailField.getText();
        String password = passwordField.getText();

        if (mail.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Username and password cannot be empty");
        } else {
            apiService.post(
                    "http://localhost:8080/api/auth/login",
                    UserDtoReq.builder()
                            .mail(mailField.getText())
                            .password(passwordField.getText())
                            .build(),
                    RegisterUserDtoRes.class,
                    response -> {
                        tokenService.saveToken(response.getJwtTokens());
                        errorMessage.setText(tokenService.getToken().toString());
                        sceneService.switchScene((Stage) loginButton.getScene().getWindow(), "/MainScene.fxml", "MainScene");
                    },
                    e -> {
                        errorMessage.setText(e.getMessage());
                        e.printStackTrace();
                    }
            );
        }
    }
}

