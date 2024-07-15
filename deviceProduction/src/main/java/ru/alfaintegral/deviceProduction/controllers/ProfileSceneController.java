package ru.alfaintegral.deviceProduction.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.api.responses.UserMe.UserDtoReq;
import ru.alfaintegral.deviceProduction.services.ApiService;
import ru.alfaintegral.deviceProduction.services.SceneService;

@Component
@RequiredArgsConstructor
public class ProfileSceneController {
    private final ApiService apiService;
    private final SceneService sceneService;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label nameLabel;

    @FXML
    private Label surnameLabel;

    @FXML
    private Label middleNameLabel;

    @FXML
    private Label mailLabel;


    @FXML
    private void initialize() {
        apiService.get(
                "http://localhost:8081/api/main/user/me",
                UserDtoReq.class,
                res1 -> {
                    usernameLabel.setText(res1.getUsername());
                    nameLabel.setText(res1.getName());
                    surnameLabel.setText(res1.getSurname());
                    middleNameLabel.setText(res1.getMiddleName());
                    mailLabel.setText(res1.getMail());
                    mailLabel.setText(res1.getMail());
                    apiService.get(
                            "http://localhost:8081/api/main/user/avatar/" + res1.getAvatarId(),
                            String.class,
                            res2 -> {
                                avatarImageView.setImage(new Image(res2));
                            },
                            Throwable::printStackTrace
                    );
                },
                Throwable::printStackTrace
        );
    }
}
