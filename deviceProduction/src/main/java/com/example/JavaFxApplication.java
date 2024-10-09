package com.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;

    public static void main(String[] args) {
        System.out.println("Приложение запущено");
        launch(args);
        System.out.println("Приложение завершено");
    }

    @Override
    public void init() throws Exception {
        springContext = SpringApplication.run(JavaFxApplication.class);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(springContext::getBean);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("Запуск приложения");

        // Проверка загрузки FXML-файла
        if (getClass().getResource("/AuthorizationScene.fxml") == null) {
            System.out.println("FXML-файл не найден!");
            return;
        } else {
            System.out.println("FXML-файл найден, загружается...");
        }

        fxmlLoader.setLocation(getClass().getResource("/AuthorizationScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Device production");
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        System.out.println("Приложение успешно запущено");
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}