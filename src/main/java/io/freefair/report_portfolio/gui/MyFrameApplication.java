package io.freefair.report_portfolio.gui;/**
 * Created by dennis on 03.12.15
 * (c) by dennis
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MyFrameApplication extends Application {
	@Override
	public void start(Stage primaryStage) throws IOException {
		URL resource = MyFrameApplication.class.getResource("myFrame.fxml");
		FXMLLoader loader = new FXMLLoader(resource);
		Parent root = loader.load();
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(new Scene(root, 700, 700));
		MyFrameController controller = loader.getController();
		controller.mainScene = primaryStage;
		controller.init();
		primaryStage.show();
	}

	public void doLaunch(String[] args) {
		launch(args);
	}
}
