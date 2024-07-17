package ru.alfaintegral.deviceProduction.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.api.responses.Login.JwtTokenDtoRes;
import ru.alfaintegral.deviceProduction.models.api.responses.UserMe.UserDtoReq;
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
    private ImageView profileAvatarImageView;

    @FXML
    private ProgressIndicator avatarLoadingIndicator;
    @FXML
    private ProgressIndicator avatarProfileLoadingIndicator;

    @FXML
    private Button logoutButton;

    @FXML
    private Label mailLabel;

    @FXML
    private AnchorPane mainPageAnchorPane;

    @FXML
    private Label middleNameLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private AnchorPane profileAnchorPane;

    @FXML
    private AnchorPane sideBarAnchorPane;

    @FXML
    private AnchorPane mainButton;

    @FXML
    private AnchorPane headerAnchorPane;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private void initialize() {
        initProfile();
        logoutButton.setOnMouseClicked(event -> onLogoutClicked());
    }

    private void initProfile() {
        avatarLoadingIndicator.setVisible(true);
        apiService.get(
                "http://localhost:8081/api/main/user/me",
                UserDtoReq.class,
                res1 -> {
                    usernameLabel.setText(res1.getUsername());
                    if (res1.getAvatarId() != null) {
                        apiService.get(
                                "http://localhost:8081/api/main/user/avatar/" + res1.getAvatarId(),
                                String.class,
                                res2 -> {
                                    avatarImageView.setImage(new Image(res2));
                                    avatarLoadingIndicator.setVisible(false);
                                },
                                Throwable::printStackTrace
                        );
                    } else {
                        //TODO вставить дефолтную аватарку
                    }
                },
                Throwable::printStackTrace
        );
    }

    @FXML
    private void handleMainButtonClick() {
        openMainPageAnchorPane();
    }

    @FXML
    private void handleProfileButtonClick() {
        openProfilePage();
    }

    private void openProfilePage(){
        openProfileAnchorPane();
        avatarProfileLoadingIndicator.setVisible(true);
        profileAvatarImageView.setImage(null);
        apiService.get(
                "http://localhost:8081/api/main/user/me",
                UserDtoReq.class,
                res1 -> {
                    usernameLabel.setText(res1.getUsername());
                    nameLabel.setText(res1.getName());
                    surnameLabel.setText(res1.getSurname());
                    middleNameLabel.setText(res1.getMiddleName());
                    mailLabel.setText(res1.getMail());
                    if (res1.getAvatarId() != null) {
                        apiService.get(
                                "http://localhost:8081/api/main/user/avatar/" + res1.getAvatarId(),
                                String.class,
                                res2 -> {
                                    profileAvatarImageView.setImage(new Image(res2));
                                    avatarProfileLoadingIndicator.setVisible(false);
                                },
                                Throwable::printStackTrace
                        );
                    } else {
                        //TODO вставить дефолтную аватарку
                    }
                },
                Throwable::printStackTrace
        );
    }

    @FXML
    private void onLogoutClicked() {
        tokenService.saveToken(new JwtTokenDtoRes(null, null));

        sceneService.switchScene(logoutButton, "/AuthorizationScene.fxml");
    }

    private void openMainPageAnchorPane(){
        hideAnchorPanes();
        mainPageAnchorPane.setVisible(true);
    }

    private void openProfileAnchorPane(){
        hideAnchorPanes();
        profileAnchorPane.setVisible(true);
    }

    private void hideAnchorPanes(){
        mainPageAnchorPane.setVisible(false);
        profileAnchorPane.setVisible(false);
    }
}
