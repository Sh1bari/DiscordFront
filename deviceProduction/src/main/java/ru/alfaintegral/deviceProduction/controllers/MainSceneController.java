package ru.alfaintegral.deviceProduction.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.api.responses.Login.JwtTokenDtoRes;
import ru.alfaintegral.deviceProduction.models.api.responses.UserMe.UserDtoReq;
import ru.alfaintegral.deviceProduction.models.entities.Token;
import ru.alfaintegral.deviceProduction.services.ApiService;
import ru.alfaintegral.deviceProduction.services.SceneService;
import ru.alfaintegral.deviceProduction.services.TokenService;

@Component
@RequiredArgsConstructor
public class MainSceneController {
    private final TokenService tokenService;
    private final ApiService apiService;
    private final SceneService sceneService;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label usernameLabel;

    @FXML
    private ProgressIndicator avatarLoadingIndicator;

    @FXML
    private Button logoutButton;

    @FXML
    private VBox sidePanel;

    @FXML
    private void initialize() {
        initProfile();
        sidePanel.setOnMouseClicked(event -> toggleSidePanel());
        avatarImageView.setOnMouseClicked(event -> onAvatarClicked());
        usernameLabel.setOnMouseClicked(event -> onUsernameClicked());
        logoutButton.setOnMouseClicked(event -> onLogoutClicked());
    }

    private void initProfile() {
        avatarLoadingIndicator.setVisible(true);
        apiService.get(
                "http://localhost:8081/api/main/user/me",
                UserDtoReq.class,
                res1 -> {
                    usernameLabel.setText(res1.getUsername());
                    if(res1.getAvatarId()!=null){
                        apiService.get(
                                "http://localhost:8081/api/main/user/avatar/" + res1.getAvatarId(),
                                String.class,
                                res2 -> {
                                    avatarImageView.setImage(new Image(res2));
                                },
                                Throwable::printStackTrace
                        );
                    }else {
                        //TODO вставить дефолтную аватарку
                    }
                },
                Throwable::printStackTrace
        );
    }

    private void toggleSidePanel() {
        if (sidePanel.getPrefWidth() == 200.0) {
            sidePanel.setPrefWidth(50.0);
        } else {
            sidePanel.setPrefWidth(200.0);
        }
    }

    @FXML
    private void onAvatarClicked() {
        sceneService.switchScene(avatarImageView, "/ProfileScene.fxml");
    }

    @FXML
    private void onUsernameClicked() {
        sceneService.switchScene(usernameLabel, "/ProfileScene.fxml");
    }

    @FXML
    private void onLogoutClicked() {
        tokenService.getToken().setAccessToken(null);
        tokenService.getToken().setRefreshToken(null);
        tokenService.saveToken(tokenService.getToken());
        sceneService.switchScene(logoutButton, "/hello-view.fxml");
    }
}
