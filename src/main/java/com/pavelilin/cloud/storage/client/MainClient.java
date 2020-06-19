package com.pavelilin.cloud.storage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.BasicConfigurator;

public class MainClient extends Application {

  static {
    BasicConfigurator.configure(); // enabling logging
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/Client.fxml"));
    primaryStage.setTitle("CS_Alpha_C");
    primaryStage.setScene(new Scene(root, 1024, 550));
    primaryStage.show();
  }
}
