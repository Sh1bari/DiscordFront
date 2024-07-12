package ru.alfaintegral.deviceProduction.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.*;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.UserDto;
import ru.alfaintegral.deviceProduction.models.api.responses.UserDtoRes;
import ru.alfaintegral.deviceProduction.services.ApiService;

@Component
@RequiredArgsConstructor
public class HelloController {
    private final ApiService apiService;

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
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String mail = mailField.getText();
        String password = passwordField.getText();

        if (mail.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Username and password cannot be empty");
        }else {
            apiService.post(
                    "http://localhost:8080/api/auth/login",
                    UserDto.builder()
                            .mail(mailField.getText())
                            .password(passwordField.getText())
                            .build(),
                    UserDtoRes.class,
                    response -> {
                        errorMessage.setText(response.toString());
                        System.out.println(response);
                    },
                    e -> {
                        System.out.println(e.getMessage());
                        errorMessage.setText(e.getMessage());
                        e.printStackTrace();
                    }
            );
        }
    }
}
