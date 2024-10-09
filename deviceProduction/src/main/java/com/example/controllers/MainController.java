package com.example.controllers;

import com.example.configs.VoiceWebSocketClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.*;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainController {

    @FXML
    private Button connectButton;

    @FXML
    private Button currentRoomLabel;

    private VoiceWebSocketClient client;
    private String currentRoom;

    @FXML
    private void handleConnect() {
        if (client == null || !client.isOpen()) {
            if (currentRoom != null) {
                client = new VoiceWebSocketClient(currentRoom);
                client.connect();
                connectButton.setText("Отключиться");
                currentRoomLabel.setText("Подключён к комнате: " + currentRoom);
            } else {
                currentRoomLabel.setText("Выберите комнату");
            }
        } else {
            client.close();
            connectButton.setText("Подключиться");
            currentRoomLabel.setText("Вы не подключены к комнате");
        }
    }

    @FXML
    private void handleConnectToMainRoom() {
        currentRoom = "main";
        currentRoomLabel.setText("Выбрана комната: Основная");
    }

    @FXML
    private void handleConnectToJunkRoom() {
        currentRoom = "junk";
        currentRoomLabel.setText("Выбрана комната: Шлак");
    }

    @FXML
    private void handleConnectToBetterRoom() {
        currentRoom = "better";
        currentRoomLabel.setText("Выбрана комната: Стану лучше");
    }
}
