package ca2;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.fxml.FXML;

/**
 * 
 * @author Yahya Almardeny
 * @version 25/2/2017
 */
public class OpeningScreen extends Application{

	
	@FXML
	private Parent  root;
	@FXML
	private Button classicGame, alternativeGame, exit;
	private String gameType;
	private MainMenu mainMenu;

	
	@Override
	public void start(Stage primaryStage){
			
		try {
			 root = FXMLLoader.load(OpeningScreen.class.getResource("/OpeningScreen.fxml"));
		} catch (IOException e) {e.printStackTrace();}
				
		Scene scene = new Scene(root,550,375);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
				
		classicGame = (Button)root.lookup("#classicGame");
		alternativeGame = (Button)root.lookup("#alternativeGame");
		exit = (Button)root.lookup("#exit");
		
		primaryStage.getIcons().add(new Image(OpeningScreen.class.getResourceAsStream("/icon.png")));
		primaryStage.setTitle("Opening Screen");
		primaryStage.show();
				
		/**************************************************************/
		
		classicGame.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				mainMenu = new MainMenu(gameType = "classic");
				primaryStage.close();
		}});
				
				
		alternativeGame.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				mainMenu = new MainMenu(gameType = "alternative");
				primaryStage.close();
		}});
				
				
				
		exit.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
				 primaryStage.close();
		}});
							
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	/**
	 * will be invoked when program close
	 */
	@Override
	public void stop(){
		if (MainMenu.classic!=null || MainMenu.alternative!=null){
			ObjectOutputStream objectOutputStream;
			Alert alert = new Alert(AlertType.CONFIRMATION, "Do You Want to Save This Game?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();
			
			switch(gameType){
				case "classic":
					if (alert.getResult() == ButtonType.YES) {
						try {
							objectOutputStream = new ObjectOutputStream(new FileOutputStream("."+File.separator + "classic.dat"));
							objectOutputStream.writeObject(new GameStatus(mainMenu.getClassic()));
							objectOutputStream.close();
						} catch (Exception e) {
							alert = new Alert(AlertType.ERROR, "failed to save the game", ButtonType.CLOSE);
						}
					}
					break;
					
				case "alternative":
					if (alert.getResult() == ButtonType.YES) {
						try {
							objectOutputStream = new ObjectOutputStream(new FileOutputStream("."+File.separator + "alternative.dat"));
							objectOutputStream.writeObject(new GameStatus(mainMenu.getAlternative()));
							objectOutputStream.close();
						} catch (Exception e) {
							alert = new Alert(AlertType.ERROR, "failed to save the game", ButtonType.CLOSE);
						}
					}
					break;
			}			
		}
	}

	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	
	public static void main (String[] args){
		launch(args);
    }

	
}
