package ca2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Towers Of Hanoi Alternative Game
 * @author Yahya Almardeny
 * @version 25/02/2017
 */
public class TowersOfHanoiAlternative {
	
	@FXML
	Parent root;
	@FXML 
	Scene scene;
	@FXML
	VBox firstTower, secondTower, thirdTower;
	@FXML
	Button tower1Btn, tower2Btn, tower3Btn, restartGame, undoMove;
	@FXML
	ComboBox<String> comboBox;
	@FXML
	TextField numberOfMoves;
	@FXML
	StackPane tower1BtnContainer,tower2BtnContainer,tower3BtnContainer;
	@FXML
	StackPane towersRegion;
	int  source, move, countDown, numberOfDiscs; 
	ArrayList<int[]>moves;
	String specialBrickId, specialBrickTower; // to record the specialBrick information
	boolean end, specialBrickEffect, countDownRunning, isUndoMove; // booleans to control the work flow of the special brick
	final int defaultNumberOfDiscs = 5, topDiscWidth = 30, STARTTIME = 5; 
	Timeline timeline = null ;
	TextField timerLabel = new TextField();
	Timer timer;
	final Color []discsColors = {Color.rgb(251,235,251), Color.rgb(242,195,243), Color.rgb(231,143,233), Color.rgb(219,88,222), 
									 Color.rgb(173,34,176), Color.rgb(124,24,126), Color.rgb(82,16,84), Color.rgb(57,12,58)};
		
		
		
		
	/**
	* Constructor of TowersOfHanoiAlternative
	* @param primaryStage
	*/
	public TowersOfHanoiAlternative(Stage primaryStage){
			startGame(primaryStage);	
	}
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
	public void startGame(Stage primaryStage) {
			
		primaryStage = new Stage();
		ImageView imageView = new ImageView(new Image(TowersOfHanoiAlternative.this.getClass().getResourceAsStream("/bg.jpg")));
			
		try {
			root = FXMLLoader.load(TowersOfHanoiAlternative.class.getResource("/Towers of Hanoi.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		towersRegion = ((StackPane)root.lookup("#towersRegion"));
		towersRegion.getChildren().add(0, imageView);
			
		scene = new Scene(root,800,600);
		scene.getStylesheets().add(TowersOfHanoiAlternative.class.getResource("/Towers of Hanoi.css").toExternalForm());
		imageView.fitWidthProperty().bind(scene.widthProperty());
		imageView.fitHeightProperty().bind(scene.heightProperty().divide(4).multiply(3));      
				
		primaryStage.setScene(scene);
		primaryStage.setTitle("Towers of Hanoi");
		primaryStage.setMinWidth(750);
		primaryStage.setMinHeight(450);
		primaryStage.getIcons().add(new Image(TowersOfHanoiAlternative.class.getResourceAsStream("/icon.png")));			
		primaryStage.show();
		
		initiateGameComponents();
		initiateTowers(defaultNumberOfDiscs);
		undoMoveHandler(); 
		numberOfDiscsHandler();
		restartGameHandler();
		specialBrickHandler();
		dragAndDropHandler(firstTower);
		dragAndDropHandler(secondTower);
		dragAndDropHandler(thirdTower);
		towersButtonClickHandler();
		towersRegionClickHandler();		 
		congrats();	
	}
		
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		

	public void initiateTowers(int discs){
		firstTower.getChildren().clear();
		secondTower.getChildren().clear();
		thirdTower.getChildren().clear();		
		int discWidth = topDiscWidth;
		for (int i=0; i<discs; i++, discWidth+=30){
			Rectangle rect = new Rectangle(discWidth,25, discsColors[i]);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			rect.setId("Rect"+i);	
			firstTower.getChildren().add(i,rect);
			rect.widthProperty().bind(scene.widthProperty().divide(3).subtract(250-discWidth));
			rect.heightProperty().bind(scene.heightProperty().divide(19));
		}
		move=source=0;
		moves = new ArrayList<int[]>();
		updateNumberOfMoves();
		end=specialBrickEffect= false;
		specialBrickId= specialBrickTower = null;
		if(countDownRunning){timer.cancel(); towersRegion.getChildren().remove(2); countDownRunning=false;} // when restart or change the number of brick
																											// if it's displaying the time left count down -> cancel it and remove it
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	@SuppressWarnings("unchecked")
	public void initiateGameComponents(){
		firstTower = (VBox) root.lookup("#firstTower");
		secondTower = (VBox) root.lookup("#secondTower");
		thirdTower = (VBox) root.lookup("#thirdTower");
		tower1BtnContainer = (StackPane) root.lookup("#tower1BtnContainer");
		tower2BtnContainer = (StackPane) root.lookup("#tower2BtnContainer");
		tower3BtnContainer = (StackPane) root.lookup("#tower3BtnContainer");
		numberOfMoves = (TextField) root.lookup("#numberOfMoves");
		restartGame = (Button) root.lookup("#restartGame");
		tower1Btn = (Button) root.lookup("#tower1Btn");
		tower2Btn = (Button) root.lookup("#tower2Btn");
		tower3Btn = (Button) root.lookup("#tower3Btn");
		undoMove = (Button) root.lookup("#undoMove");
		comboBox = (ComboBox<String>) root.lookup("#comboBox");
		comboBox.getItems().addAll("3","4","5","6","7","8"); 
		comboBox.setValue("5");
		comboBox.setEditable(true);
		numberOfDiscs = Integer.parseInt(comboBox.getValue());
		moves = new ArrayList<int[]>();
		end=specialBrickEffect=countDownRunning=false;
		specialBrickId=specialBrickTower=null; // at start there is no special brick
		move=source=0;
	}
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void numberOfDiscsHandler(){
		comboBox.valueProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				boolean validInput = true;
				int choice = 0;					
				if (!arg2.isEmpty()){ // if there is entry
					for(int i = 0; i < arg2.length(); i++){ // check for any non-digit
						char c = arg2.charAt(i);
				        if((c<47 || c>58)){
				           validInput=false;
				        }
					}
				if (validInput){// if the input consists of only integers 
					 choice = Integer.parseInt(arg2);
				   	if(2<choice && choice<9){ // if it's in the accepted range 
					    initiateTowers(numberOfDiscs = choice);
					    		
					}
				}
		}}});
			
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * handle and control the special brick even
	 * according to the number of the moves done
	 * every 10 moves the special brick activate
	 */
	public void specialBrickHandler(){
		numberOfMoves.textProperty().addListener(new ChangeListener<String>(){
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				// every 10 moves show the special brick
				if (Integer.parseInt(arg2)%10==0 && Integer.parseInt(arg2)!=0 && !isUndoMove){ // executes only if it's not "undo move" (should show no effects when go backwards)
					specialBrickEffect=true; // reference to activate the special brick event 
					countDown = STARTTIME; 
					startCountDown(); // start to count down for 5 seconds
				}
			}});
	}
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * count down watch for 5 seconds
	 * appear in a textField on the top right
	 */
	public void startCountDown() {
		timerLabel = new TextField();
		if(timer!=null){timer.cancel();}
		timer = new Timer();
		timerLabel.setStyle("-fx-text-fill:white");
		timerLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
		timerLabel.setBackground(Background.EMPTY);
		timerLabel.setAlignment(Pos.TOP_RIGHT);
		timerLabel.setEditable(false);
		towersRegion.getChildren().add(2, timerLabel);
		countDownRunning = true; // indicate that counting down started
			
		timer.schedule(new TimerTask() { // timer task to update the seconds
			@Override
		    public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
		        	   timerLabel.setText("Time left: " + countDown);
		               countDown--;
		               if (countDown < 0){timer.cancel();}
			}});}}, 1000, 1000); //Every 1 second
		    
		 timeline = new Timeline(new KeyFrame( // create a time line to remove the special brick effect after 7 seconds
				 		Duration.seconds(7), ae -> { removeEffectFromSpecialBrick();})); // lambda style
		 timeline.play();
	}
		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
	public void restartGameHandler(){		
		restartGame.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e) {
				initiateTowers(numberOfDiscs);
				comboBox.setValue(String.valueOf(numberOfDiscs));
		}});
	}
		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void dragAndDropHandler(Node tower){
		tower.setOnDragDetected(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				final Object source = event.getSource();
				if(source instanceof VBox){
					ObservableList<Node> discs = ((VBox) source).getChildrenUnmodifiable();
					if (discs.size() != 0){
						Node topDisc = discs.get(0);
						if(topDisc instanceof Rectangle){
							Dragboard db = ((Node) source).startDragAndDrop(TransferMode.MOVE);
							ClipboardContent content = new ClipboardContent();
							content.putString(String.valueOf(((Rectangle) topDisc).getWidth()));
							content.putHtml(String.valueOf(((Rectangle) topDisc).getFill()));
							content.putUrl(String.valueOf(((Rectangle) topDisc).getId()));
							db.setContent(content);
							resolveRegionNameToColorButton(tower, Color.CYAN);
							event.consume();
				}}}}});
		
		tower.setOnDragOver(new EventHandler<DragEvent>() {
			 public void handle(DragEvent event) {				 
				double sourceTopDisc = Double.parseDouble(event.getDragboard().getString());
				double destinationTopDisc = 999999;
				ObservableList<Node> discs = ((VBox)tower).getChildrenUnmodifiable();
				if (discs.size()!=0){
					destinationTopDisc = ((Rectangle)discs.get(0)).getWidth();
				}
				// the boolean indicates the trigger of the special brick event, while the method isSpecialBrick to maintain the event	
				if (specialBrickEffect || isSpecialBrick(((VBox)event.getGestureSource()).getChildren().get(0).getId())){ // make exemption of the rule when it's special brick 
					event.acceptTransferModes(TransferMode.MOVE);
					resolveRegionNameToColorButton(tower, Color.rgb(10,241,90)); 
				}
				
				else {
					if (event.getGestureSource() != tower && event.getDragboard().hasString() && sourceTopDisc<destinationTopDisc) {
		            event.acceptTransferModes(TransferMode.MOVE);
		            resolveRegionNameToColorButton(tower, Color.rgb(10,241,90));   
			    }
					else if (sourceTopDisc>destinationTopDisc){resolveRegionNameToColorButton(tower, Color.RED);}
				}
				event.consume();
		}});

		tower.setOnDragExited(new EventHandler<DragEvent>() {
			 public void handle(DragEvent event) {
				 resolveRegionNameToColorButton(tower, Color.BLACK);
		}});
			
		tower.setOnDragDropped(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        Dragboard db = event.getDragboard();
		        boolean success = false;  
			    if (db.hasString() && event.getGestureSource() != tower) {
			      	createDisc(tower, db.getUrl(), db.getString(), db.getHtml());
			       	moves.add(new int[]{resolveGestureName(event.getGestureSource()),resolveGestureName(tower)});
			       	success = true;
			    }
			    event.setDropCompleted(success);
		        event.consume();
	     }});
			
		tower.setOnDragDone(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        if (event.getTransferMode() == TransferMode.MOVE) {
		        	((VBox)tower).getChildren().remove(0);
			    }
			    event.consume();
		 }});
		
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void towersButtonClickHandler(){		
		tower1Btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				firstTowerGeneralClick();
		}});
			
		tower2Btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				secondTowerGeneralClick();
		}});
			
		tower3Btn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				thirdTowerGeneralClick();
		}});
		
	 }

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void towersRegionClickHandler(){		
		firstTower.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				firstTowerGeneralClick();	
		}});
			
		secondTower.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				secondTowerGeneralClick();
		 }});
			
		thirdTower.setOnMouseClicked(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				thirdTowerGeneralClick();
		}});
		
	 }
		
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	public void firstTowerGeneralClick(){
		if (source==0){source=1; flashRegion(tower1BtnContainer, Color.CYAN);} // means it's the source
		else if (source!=1){
			switch(source){
				case 2:
					if(secondTower.getChildren().size() !=0){
						if (firstTower.getChildren().size()!=0){
							if (specialBrickEffect || isSpecialBrick(secondTower.getChildren().get(0).getId())){ // the same way in handling the special brick occurrence in the drag and drop 
								createDisc(firstTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), 
												String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
													String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
								secondTower.getChildren().remove(0);
								flashRegion(tower1BtnContainer, Color.rgb(10,241,90));
								moves.add(new int[]{2,1});
							}
							else if ((((Rectangle)secondTower.getChildren().get(0)).getWidth())<(((Rectangle)firstTower.getChildren().get(0)).getWidth())){
										createDisc(firstTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), 
													String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
														String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
										secondTower.getChildren().remove(0);
										flashRegion(tower1BtnContainer, Color.rgb(10,241,90));
										moves.add(new int[]{2,1});	
							}
							else{flashRegion(tower1BtnContainer, Color.RED);}
						}
						else {
							createDisc(firstTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), 
										String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
											String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
							secondTower.getChildren().remove(0);
							moves.add(new int[]{2,1});
							flashRegion(tower1BtnContainer,  Color.rgb(10,241,90));						
						}
					}	
				break;
				
				case 3:
					if(thirdTower.getChildren().size() !=0){
						if (firstTower.getChildren().size()!=0){
							if (specialBrickEffect || isSpecialBrick(thirdTower.getChildren().get(0).getId())){
								createDisc(firstTower, ((Rectangle)thirdTower.getChildren().get(0)).getId(), 
											 String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getWidth()),
												String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getFill()));
								thirdTower.getChildren().remove(0);
								flashRegion(tower1BtnContainer,  Color.rgb(10,241,90));
								moves.add(new int[]{3,1});
							}
							else if ((((Rectangle)thirdTower.getChildren().get(0)).getWidth())<(((Rectangle)firstTower.getChildren().get(0)).getWidth())){
										createDisc(firstTower, ((Rectangle)thirdTower.getChildren().get(0)).getId(), 
												String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getWidth()),
													String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getFill()));
										thirdTower.getChildren().remove(0);
										flashRegion(tower1BtnContainer,  Color.rgb(10,241,90));
										moves.add(new int[]{3,1});
							}
							else{flashRegion(tower1BtnContainer, Color.RED);}
						}
						else {
								createDisc(firstTower, ((Rectangle)thirdTower.getChildren().get(0)).getId(), 
											String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getWidth()),
												String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getFill()));
								thirdTower.getChildren().remove(0);
								flashRegion(tower1BtnContainer,  Color.rgb(10,241,90));
								moves.add(new int[]{3,1});
						}
					}
				break;
			}
			source=0;
		}
	}

	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
	public void secondTowerGeneralClick(){
		if (source==0){source=2; flashRegion(tower2BtnContainer, Color.CYAN);}
		else if (source!=2){
				switch(source){
					case 1:
						if(firstTower.getChildren().size() !=0){
							if (secondTower.getChildren().size()!=0){
								if (specialBrickEffect || isSpecialBrick(firstTower.getChildren().get(0).getId())){
									createDisc(secondTower, ((Rectangle)firstTower.getChildren().get(0)).getId(), 
											String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getWidth()),
												String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getFill()));
									firstTower.getChildren().remove(0);
									flashRegion(tower2BtnContainer,  Color.rgb(10,241,90));
									moves.add(new int[]{1,2});
								}
								else if ((((Rectangle)firstTower.getChildren().get(0)).getWidth())<(((Rectangle)secondTower.getChildren().get(0)).getWidth())){
										createDisc(secondTower, ((Rectangle)firstTower.getChildren().get(0)).getId(), 
												String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getWidth()),
													String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getFill()));
										firstTower.getChildren().remove(0);
										flashRegion(tower2BtnContainer,  Color.rgb(10,241,90));
										moves.add(new int[]{1,2});	
								}
								else{flashRegion(tower2BtnContainer, Color.RED);}
							}
							else{
								createDisc(secondTower, ((Rectangle)firstTower.getChildren().get(0)).getId(), 
										String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getWidth()),
											String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getFill()));
								firstTower.getChildren().remove(0);
								moves.add(new int[]{1,2});
								flashRegion(tower2BtnContainer,  Color.rgb(10,241,90));
							}	
						}
				break;
						
				case 3:
					if(thirdTower.getChildren().size() !=0){
						if (secondTower.getChildren().size()!=0){
							if (specialBrickEffect || isSpecialBrick(thirdTower.getChildren().get(0).getId())){
								createDisc(secondTower, ((Rectangle)thirdTower.getChildren().get(0)).getId(), 
											String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getWidth()),
												String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getFill()));
								thirdTower.getChildren().remove(0);
								flashRegion(tower2BtnContainer,  Color.rgb(10,241,90));
								moves.add(new int[]{3,2});
							}
							else if ((((Rectangle)thirdTower.getChildren().get(0)).getWidth())<(((Rectangle)secondTower.getChildren().get(0)).getWidth())){
										createDisc(secondTower, ((Rectangle)thirdTower.getChildren().get(0)).getId(), 
												String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getWidth()),
													String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getFill()));
										thirdTower.getChildren().remove(0);
										flashRegion(tower2BtnContainer,  Color.rgb(10,241,90));
										moves.add(new int[]{3,2});
							}
							else{flashRegion(tower2BtnContainer, Color.RED);}
							}
						else{
							createDisc(secondTower, ((Rectangle)thirdTower.getChildren().get(0)).getId(), 
										String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getWidth()),
											String.valueOf(((Rectangle)thirdTower.getChildren().get(0)).getFill()));
							thirdTower.getChildren().remove(0);
							moves.add(new int[]{3,2});
							flashRegion(tower2BtnContainer,  Color.rgb(10,241,90));
						}					
					}
				break;
			}
			source=0;
		}			
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void thirdTowerGeneralClick(){	
		if (source==0){source=3; flashRegion(tower3BtnContainer, Color.CYAN);}
		else if (source!=3){
			switch(source){
				case 1:
					if(firstTower.getChildren().size() !=0){
						if (thirdTower.getChildren().size()!=0){
							if (specialBrickEffect || isSpecialBrick(firstTower.getChildren().get(0).getId())){
								createDisc(thirdTower, ((Rectangle)firstTower.getChildren().get(0)).getId(), 
											String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getWidth()),
												String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getFill()));
								firstTower.getChildren().remove(0);
								flashRegion(tower3BtnContainer, Color.rgb(10,241,90));
								moves.add(new int[]{1,3});
							}
							else if ((((Rectangle)firstTower.getChildren().get(0)).getWidth())<(((Rectangle)thirdTower.getChildren().get(0)).getWidth())){
										createDisc(thirdTower, ((Rectangle)firstTower.getChildren().get(0)).getId(), 
											String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getWidth()),
												String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getFill()));
										firstTower.getChildren().remove(0);
										flashRegion(tower3BtnContainer, Color.rgb(10,241,90));
										moves.add(new int[]{1,3});
							}
							else{flashRegion(tower3BtnContainer, Color.RED);}
							}
						else{
							createDisc(thirdTower, ((Rectangle)firstTower.getChildren().get(0)).getId(), 
										String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getWidth()),
											String.valueOf(((Rectangle)firstTower.getChildren().get(0)).getFill()));
							firstTower.getChildren().remove(0);
							flashRegion(tower3BtnContainer, Color.rgb(10,241,90));
							moves.add(new int[]{1,3});
						}
					}
				break;
						
				case 2:
					if(secondTower.getChildren().size() !=0){
						if (thirdTower.getChildren().size()!=0){
							if (specialBrickEffect || isSpecialBrick(secondTower.getChildren().get(0).getId())){
								createDisc(thirdTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), 
										String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
											String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
								secondTower.getChildren().remove(0);
								flashRegion(tower3BtnContainer, Color.rgb(10,241,90));
								moves.add(new int[]{2,3});
							}
							else if ((((Rectangle)secondTower.getChildren().get(0)).getWidth())<(((Rectangle)thirdTower.getChildren().get(0)).getWidth())){
									createDisc(thirdTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), 
										String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
											String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
									secondTower.getChildren().remove(0);
									flashRegion(tower3BtnContainer, Color.rgb(10,241,90));
									moves.add(new int[]{2,3});
							}
							else{flashRegion(tower3BtnContainer, Color.RED);}
							}
						else{
							createDisc(thirdTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), 
									String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
										String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
							secondTower.getChildren().remove(0);
							moves.add(new int[]{2,3});
							flashRegion(tower3BtnContainer, Color.rgb(10,241,90));
						}
					}
					break;
			}
			source=0;
		}	
	}

		

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		

	public void undoMoveHandler(){		 
		undoMove.setOnAction(new EventHandler<ActionEvent>(){	 
			@Override
			public void handle(ActionEvent arg0) {
				if (moves.size()!=0){
					if(!countDownRunning){ // undo moves shall not execute if the count down is running
						isUndoMove = true; // indicate that it's "undo move" in order not to create a special brick in the createDisc() method 
						Rectangle rect = (Rectangle) resolveVBoxNumber(moves.get(moves.size()-1)[1]).getChildren().get(0);
						createDisc(moves.get(moves.size()-1)[0], rect.getId(), String.valueOf(rect.getWidth()), rect.getFill().toString());
						resolveVBoxNumber(moves.get(moves.size()-1)[1]).getChildren().remove(0);
						moves.remove(moves.size()-1);
						move--;
						updateNumberOfMoves();
						isUndoMove = false;
		}}}});
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
		
	public void createDisc(Node tower, String discId, String width, String color){
		Rectangle rect = new Rectangle(Double.parseDouble(width),20);
		rect.setFill(Color.valueOf(color));
		rect.setId(discId);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(3);
		if (!isUndoMove){
			move++;
			updateNumberOfMoves();
		}
		if (isUndoMove){ // if it's an undo move -> don't create a special brick if it's original special
			rect.setEffect(null);
			Color origColor = (discId.equals("Rect0"))? discsColors[0]:(discId.equals("Rect1"))? discsColors[1]:
							  (discId.equals("Rect2"))? discsColors[2]:(discId.equals("Rect3"))? discsColors[3]:
							  (discId.equals("Rect4"))? discsColors[4]:(discId.equals("Rect5"))? discsColors[5]:
							  (discId.equals("Rect6"))? discsColors[6]:(discId.equals("Rect7"))? discsColors[7]:null;
			rect.setFill(origColor);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			specialBrickEffect= false;
			specialBrickId=null;
		}
		else if (specialBrickEffect || isSpecialBrick(discId)){ // create a special brick
				DropShadow borderGlow = new DropShadow();
				borderGlow.setColor(Color.LIME);
				borderGlow.setOffsetX(0f);
				borderGlow.setOffsetY(0f);
				borderGlow.setWidth(100);
				borderGlow.setHeight(100);
				rect.setFill(Color.WHITE);
				rect.setEffect(borderGlow);
				rect.setStroke(Color.GREEN);
				rect.setStrokeWidth(3);
				specialBrickId = rect.getId();
				specialBrickTower = tower.getId();
				specialBrickEffect=false;
		}
			
		((VBox) tower).getChildren().add(0,rect);
		int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
						(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
						(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
						(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
		rect.widthProperty().bind(scene.widthProperty().divide(3).subtract(250-origWidth));
		rect.heightProperty().bind(scene.heightProperty().divide(19));		
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void createDisc(int towerNumber, String discId, String width, String color){
		switch(towerNumber){
		case 1:
			 createDisc(firstTower, discId, width, color);
			 break;
		case 2:
			createDisc(secondTower, discId, width, color);
			break;
		case 3:
			createDisc(thirdTower, discId, width, color);
			break;
		}
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void flashRegion(Region region, Color color){
		region.setBackground(new Background( new BackgroundFill(color, null, null )));
		PauseTransition delay = new PauseTransition(Duration.seconds(0.3));	
		delay.setOnFinished(event -> {region.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));});
		delay.play();					
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
		
	public int resolveGestureName(Object obj){
		int result= (((VBox)obj).getId().equals("firstTower"))? 1:
						(((VBox)obj).getId().equals("secondTower"))? 2:
				  			(((VBox)obj).getId().equals("thirdTower"))? 3:0;									
		return result;
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void resolveRegionNameToColorButton(Object obj, Color color){
		int region = resolveGestureName(obj);
		switch(region){
			case 1:
				tower1BtnContainer.setBackground(new Background( new BackgroundFill(color, null, null )));
				break;
			case 2:
				tower2BtnContainer.setBackground(new Background( new BackgroundFill(color, null, null )));
				break;
			case 3:
				tower3BtnContainer.setBackground(new Background( new BackgroundFill(color, null, null )));
				break;
		}	
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	private VBox resolveVBoxNumber(int number){
		VBox result= (number==1)? firstTower:
						(number==2)? secondTower:
							(number==3)? thirdTower:null;
		return result;
	}
		
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public void updateNumberOfMoves(){
		numberOfMoves.setText(String.valueOf(move));
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * check if the disc id equals the special brick id
	 * which indicates keeping the special effect while moving
	 * @param discId
	 * @return boolean
	 */
	public boolean isSpecialBrick(String discId){
		if (specialBrickId!=null){
			if (specialBrickId.equals(discId)){
				return true;
			}
		}
		return false;
	}
		
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	/**
	 * to remove the effect from the special brick
	 * at the end of the 5 seconds (count down)	
	 */
	public void removeEffectFromSpecialBrick(){
			// return everything to the original
			Color origColor = (specialBrickId.equals("Rect0"))? discsColors[0]:(specialBrickId.equals("Rect1"))? discsColors[1]:
						  (specialBrickId.equals("Rect2"))? discsColors[2]:(specialBrickId.equals("Rect3"))? discsColors[3]:
						  (specialBrickId.equals("Rect4"))? discsColors[4]:(specialBrickId.equals("Rect5"))? discsColors[5]:
						  (specialBrickId.equals("Rect6"))? discsColors[6]:(specialBrickId.equals("Rect7"))? discsColors[7]:null; 
			int origWidth = (specialBrickId.equals("Rect0"))? 30:(specialBrickId.equals("Rect1"))? 60:
							(specialBrickId.equals("Rect2"))? 90:(specialBrickId.equals("Rect3"))? 120:
							(specialBrickId.equals("Rect4"))? 150:(specialBrickId.equals("Rect5"))? 180:
							(specialBrickId.equals("Rect6"))? 210:(specialBrickId.equals("Rect7"))? 240:null;
			Rectangle rect = new Rectangle(origWidth,20, origColor);
			rect.setId(specialBrickId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
						
			switch(specialBrickTower){
				case "firstTower":
					for(int i=0; i<firstTower.getChildren().size(); i++){ // cycle through the tower to check which one is the special brick
						if (firstTower.getChildren().get(i).getId().equals(specialBrickId)){
							firstTower.getChildren().set(i, rect); // then reset it
					}}
				break;
				case "secondTower":
					for(int i=0; i<secondTower.getChildren().size(); i++){
						if (secondTower.getChildren().get(i).getId().equals(specialBrickId)){
							secondTower.getChildren().set(i, rect);
					}}
				break;
				case "thirdTower":
					for(int i=0; i<thirdTower.getChildren().size(); i++){
						if (thirdTower.getChildren().get(i).getId().equals(specialBrickId)){
							thirdTower.getChildren().set(i, rect);
					}}
				break;
			}
				
			rect.widthProperty().bind(scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(scene.heightProperty().divide(19));
		
		if(countDownRunning){towersRegion.getChildren().remove(2);countDownRunning=false;} // remove the count down watch from the screen		
		specialBrickId=null; // reset the special brick id
	}
		
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
	public void congrats(){
		scene.addEventHandler(Event.ANY, new EventHandler<Event>(){
			@Override
			public void handle(Event event) {
				if(!end){
				    if(secondTower.getChildren().size()==numberOfDiscs){
			       	  Alert alert = new Alert (AlertType.INFORMATION, "Congratulation You Win!", ButtonType.OK);
			       	  end = true;      	  
			        alert.showAndWait();
		}}}});
	}

			
	

}
