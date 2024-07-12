package ru.alfaintegral.deviceProduction.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.api.responses.UserMe.UserDtoReq;
import ru.alfaintegral.deviceProduction.services.ApiService;
import ru.alfaintegral.deviceProduction.services.TokenService;

@Component
@RequiredArgsConstructor
public class MainSceneController {
    private final TokenService tokenService;
    private final ApiService apiService;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private Label usernameLabel;

    @FXML
    private VBox sidePanel;

    @FXML
    private void initialize() {
        initProfile();
        sidePanel.setOnMouseClicked(event -> toggleSidePanel());
    }

    private void initProfile(){
        apiService.get(
                "http://localhost:8081/api/main/user/me",
                UserDtoReq.class,
                res1 -> {
                    usernameLabel.setText(res1.getUsername());
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

    private void toggleSidePanel() {
        if (sidePanel.getPrefWidth() == 200.0) {
            sidePanel.setPrefWidth(50.0);
        } else {
            sidePanel.setPrefWidth(200.0);
        }
    }
}
