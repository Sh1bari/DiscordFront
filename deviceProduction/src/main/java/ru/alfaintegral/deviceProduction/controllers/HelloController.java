package ru.alfaintegral.deviceProduction.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.*;
import org.springframework.stereotype.Component;
import ru.alfaintegral.deviceProduction.models.UserDto;
import ru.alfaintegral.deviceProduction.services.ApiService;

@Component
@RequiredArgsConstructor
public class HelloController {
    private final ApiService apiService;

    @FXML
    private Label myLabel;

    @FXML
    protected void handleButtonClick() {
        apiService.get(
                "http://localhost:8081/api/main/service/test",
                UserDto.class,
                response -> myLabel.setText(response.toString()),
                e -> {
                    myLabel.setText("Error fetching data");
                    e.printStackTrace();
                }
        );
    }
}
