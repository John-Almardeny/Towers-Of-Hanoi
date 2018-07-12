package ca2;


import java.util.ArrayList;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
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
import javafx.stage.Stage;
import javafx.util.Duration;



/**
 * Towers of Hanoi Game
 * @author Yahya Almardeny
 * @version 25/02/2017
 * All Rights Reserved
 */
public class TowersOfHanoi{

	@FXML
	Parent root; 
	@FXML
	VBox firstTower, secondTower, thirdTower;
	@FXML
	Button tower1Btn, tower2Btn, tower3Btn, restartGame, undoMove;
	@FXML
	ComboBox<String> comboBox; // to change the number of discs
	@FXML
	TextField numberOfMoves; // to show the number of current moves done
	@FXML
	StackPane tower1BtnContainer, tower2BtnContainer,tower3BtnContainer; // the parents of the towers' buttons
	Scene scene;
	int  source, move, numberOfDiscs =0; // the source tower of the disc and the number of the moves
	ArrayList<int[]>moves; // keeps records & information about the discs and their towers for every move
	private boolean end; // will be true at the end of the game to show the congrats message
	final int defaultNumberOfDiscs = 5, topDiscWidth = 30;
	private final Color []discsColors = {Color.rgb(251,235,251), Color.rgb(242,195,243), Color.rgb(231,143,233), Color.rgb(219,88,222), 
										 Color.rgb(173,34,176), Color.rgb(124,24,126), Color.rgb(82,16,84), Color.rgb(57,12,58)};
	
	
	
	
	/**
	 * Constructor of TowersOfHanoi
	 * @param primaryStage
	 */
	public TowersOfHanoi(Stage primaryStage){
		startGame(primaryStage);	
	}
	
	 

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * the start point of the game
	 * @param primaryStage
	 */
	public void startGame(Stage primaryStage) {
		
		primaryStage = new Stage();
		ImageView imageView = new ImageView(new Image(TowersOfHanoi.this.getClass().getResourceAsStream("/bg.jpg")));
		
		try {
			root = FXMLLoader.load(TowersOfHanoi.class.getResource("/Towers of Hanoi.fxml")); // load the fxml file
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		((StackPane)root.lookup("#towersRegion")).getChildren().add(0, imageView); // add the image view as a background (index 0) to the root stackPane (middle area)  
		
		scene = new Scene(root,800,600);
		scene.getStylesheets().add(TowersOfHanoi.class.getResource("/Towers of Hanoi.css").toExternalForm()); // add the cascade styling sheet
		
		imageView.fitWidthProperty().bind(scene.widthProperty()); // bind the width & height properties to the scene (resizable)
	    imageView.fitHeightProperty().bind(scene.heightProperty().divide(4).multiply(3)); // the height is 75% of the overall scene's height      
			
		primaryStage.setScene(scene);
		primaryStage.setTitle("Towers of Hanoi");
		primaryStage.setMinWidth(750);
		primaryStage.setMinHeight(450);
		primaryStage.getIcons().add(new Image(TowersOfHanoi.class.getResourceAsStream("/icon.png")));
		primaryStage.show();
		
		//the handlers
		initiateGameComponents();
		initiateTowers(defaultNumberOfDiscs);
		numberOfDiscsHandler();
		restartGameHandler();
		dragAndDropHandler(firstTower);
		dragAndDropHandler(secondTower);
		dragAndDropHandler(thirdTower);
		towersButtonClickHandler();
		towersRegionClickHandler();
		undoMoveHandler();  
		congrats();
		
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * initiates towers (clear 2 and 3) and 
	 * fill tower 1 with discs
	 * it will be invoked at the beginning of the game
	 * when restarted and if number of discs changed
	 * @param discs
	 */
	public void initiateTowers(int discs){
		// clears all towers of any has discs
		firstTower.getChildren().clear();
		secondTower.getChildren().clear();
		thirdTower.getChildren().clear();
		
		int discWidth = topDiscWidth;
		for (int i=0; i<discs; i++, discWidth+=30){ // increase width by 30
			Rectangle rect = new Rectangle(discWidth,25, discsColors[i]);// create the discs (using Rectangle Shape)
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			rect.setId("Rect"+i);	
			firstTower.getChildren().add(i,rect);// add the each disc
			rect.widthProperty().bind(scene.widthProperty().divide(3).subtract(250-discWidth));// bind the width and the height of discs
			rect.heightProperty().bind(scene.heightProperty().divide(19));						// to the scene's to make them resizable
		}
		move=source=0;
		moves = new ArrayList<int[]>(); // erase the records
		updateNumberOfMoves();
	}
	
	


	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * Initiate the game components and variables
	 * for one time only once it launches 
	 */
	@SuppressWarnings("unchecked")
	public void initiateGameComponents(){
		firstTower = (VBox) root.lookup("#firstTower"); // look up the id of first tower from the FXML file
		secondTower = (VBox) root.lookup("#secondTower");
		thirdTower = (VBox) root.lookup("#thirdTower");
		restartGame = (Button) root.lookup("#restartGame");
		tower1Btn = (Button) root.lookup("#tower1Btn");
		tower2Btn = (Button) root.lookup("#tower2Btn");
		tower3Btn = (Button) root.lookup("#tower3Btn");
		undoMove = (Button) root.lookup("#undoMove");
		tower1BtnContainer = (StackPane) root.lookup("#tower1BtnContainer");
		tower2BtnContainer = (StackPane) root.lookup("#tower2BtnContainer");
		tower3BtnContainer = (StackPane) root.lookup("#tower3BtnContainer");
		numberOfMoves = (TextField) root.lookup("#numberOfMoves");
		comboBox = (ComboBox<String>) root.lookup("#comboBox");
		comboBox.getItems().addAll("3","4","5","6","7","8"); // number of discs between 3 - 8
		comboBox.setValue("5");
		comboBox.setEditable(true);
		moves = new ArrayList<int[]>();
		move=source=0;
		numberOfDiscs = 5;
		end = false;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * To handle the number of discs when it's changed
	 * in the comboBox
	 */
	public void numberOfDiscsHandler(){
		
		comboBox.valueProperty().addListener(new ChangeListener<String>(){ // listen to change of comboBox value
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				boolean validInput = true;
				int choice = 0;
				if (!arg2.isEmpty()){ // if there is an input
					for(int i = 0; i < arg2.length(); i++){ // check for any non-digit
						char c = arg2.charAt(i);
				        if((c<48 || c>57)){ // if any non-digit found (ASCII chars values for [0-9] are [48-57]
				            validInput=false;
				        }
				    }
					
				    if (validInput){// if the input consists of integers only
				    	choice = Integer.parseInt(arg2); // it's safe, take the input
				    	if(2<choice && choice<9){ // if it's in the accepted range 
				    		initiateTowers(choice);
				    		numberOfDiscs = choice;
				    	}
					}
				}
			}});
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * To restart the game by clicking
	 * on the Restart Game button
	 * it will keep to the current number of discs 
	 */
	public void restartGameHandler(){
		restartGame.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e) {
				// set to the current number of discs (not the default)
				initiateTowers(numberOfDiscs);
				comboBox.setValue(String.valueOf(numberOfDiscs));
		}});
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * to handle the drag and drop events of the discs
	 * @param tower
	 */
	public void dragAndDropHandler(Node tower){
		tower.setOnDragDetected(new EventHandler<MouseEvent>(){ // if drag event is detected
			@Override
			public void handle(MouseEvent event) {
				final Object source = event.getSource(); // take the event source into object 
				if(source instanceof VBox){ // if it's an instance of VBox
					ObservableList<Node> discs = ((VBox) source).getChildrenUnmodifiable(); // create list of observable nodes (represents the Rectangles/discs) 
					if (discs.size() != 0){ // if there is any disc in the source
						Node topDisc = discs.get(0); // take the top disc
						if(topDisc instanceof Rectangle){ // if it's an instance of Rectangle (this is a double check but not necessary)
							Dragboard db = ((Node) source).startDragAndDrop(TransferMode.MOVE); //create a dragboard and choose the transfer mode as MOVE 
							ClipboardContent content = new ClipboardContent();
							content.putString(String.valueOf(((Rectangle) topDisc).getWidth())); // put the width of the top disc in the content
							content.putHtml(String.valueOf(((Rectangle) topDisc).getFill())); // put the color of the top disc in the content (I'm using the HTML string)
							content.putUrl(String.valueOf(((Rectangle) topDisc).getId())); // put the id of the top disc in the content (I'm using the URL string)
							db.setContent(content); //set the clip board content in the dragboard object
							resolveRegionNameToColorButton(tower, Color.CYAN); // color the container of the source button to indicate the occurrence 
							event.consume();
			}}}}});
	
		tower.setOnDragOver(new EventHandler<DragEvent>() { // set rule for the potential destination
			 public void handle(DragEvent event) {
				double sourceTopDisc = Double.parseDouble(event.getDragboard().getString());// get the top disc width of the source that is put on the drag board
				double destinationTopDisc = 999999; // big number in case the destination has no discs yet
				ObservableList<Node> discs = ((VBox)tower).getChildrenUnmodifiable(); // get list of the discs in the destination tower
				if (discs.size()!=0){ // if there is any
					destinationTopDisc = ((Rectangle)discs.get(0)).getWidth(); // assign the width of the top disc for validation
				}
				if (event.getGestureSource() != tower && sourceTopDisc<destinationTopDisc) { // if the destination is not the source and the top disc of source smaller of the destination top disc
			            event.acceptTransferModes(TransferMode.MOVE); // accept transfer 
			            resolveRegionNameToColorButton(tower, Color.rgb(10,241,90)); // indicate acceptance by coloring the button container   
			    }
				else if (sourceTopDisc>destinationTopDisc){resolveRegionNameToColorButton(tower, Color.RED);} // if the destination top disc is bigger, indicate illegal move
				
			    event.consume();
		   }});

		
		tower.setOnDragExited(new EventHandler<DragEvent>() { // when drag exited, remove color indication
			 public void handle(DragEvent event) {
				 resolveRegionNameToColorButton(tower, Color.BLACK);
			 }
		});
		
		
		tower.setOnDragDropped(new EventHandler<DragEvent>() { // if drag has dropped on the destination tower
		    public void handle(DragEvent event) {
		        Dragboard db = event.getDragboard();
		        boolean success = false; // to inform successful drop
		        if (db.hasString() && event.getGestureSource() != tower) {
		        	createDisc(tower, db.getUrl(), db.getString(), db.getHtml()); //create disc in the destination with the same attributes of the one coming from source
		        	moves.add(new int[]{resolveGestureName(event.getGestureSource()),resolveGestureName(tower)}); // record this move
		            success = true;
		        }
		        event.setDropCompleted(success); // inform successful drop
		        event.consume();
		}});
		
		tower.setOnDragDone(new EventHandler<DragEvent>() { // when the drop is done remove the top disc from the source tower
		    public void handle(DragEvent event) {
		        if (event.getTransferMode() == TransferMode.MOVE) {
		            ((VBox)tower).getChildren().remove(0);
		        }
		        event.consume();
		 }});
	}
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * to handle the clicks on the towers' buttons
	 */
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
	
	
	
	/**
	 * to handle the clicks on the direct towers regions(areas)
	 */
	public void towersRegionClickHandler(){
		//note that click mouse means press and release 
		//(setOnMousePressed will interrupt with drag and drop events in regards of the color indication)
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
	
	
	
	/**
	 * to handle and validate the click events
	 * of the first tower
	 */
	public void firstTowerGeneralClick(){
		if (source==0){// means it's the source because it's the first click
			source=1; // now the source is the first tower (i.e. 1)
			flashRegion(tower1BtnContainer, Color.CYAN); // flash the region to indicate occurrence of selection
		} 
		else if (source!=1){ // if the first tower meant to be the destination
			switch(source){
				case 2: // source is second tower
					if(secondTower.getChildren().size() !=0){ // if the source already has discs
						if (firstTower.getChildren().size()!=0){ // if the destination tower (first tower) contains disc(s)
							if ((((Rectangle)secondTower.getChildren().get(0)).getWidth())<(((Rectangle)firstTower.getChildren().get(0)).getWidth())){ // check if the top disc from source is smaller
								createDisc(firstTower, ((Rectangle)secondTower.getChildren().get(0)).getId(), //create the disc in the destination(tower 1)
										   	String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getWidth()),
											  String.valueOf(((Rectangle)secondTower.getChildren().get(0)).getFill()));
								secondTower.getChildren().remove(0); // remove the top disc from the source tower (tower 2)
								flashRegion(tower1BtnContainer, Color.rgb(10,241,90)); // indicate acceptance
								moves.add(new int[]{2,1}); // record the move
								
							}
							else{flashRegion(tower1BtnContainer, Color.RED);} // if the top disc in the source is bigger than the top disc in the destination
						}
						else { // if the destination tower (first tower) has no discs yet, do the move
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
							if ((((Rectangle)thirdTower.getChildren().get(0)).getWidth())<(((Rectangle)firstTower.getChildren().get(0)).getWidth())){
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
			source=0; // reset the source to zero for the future rounds	
		}
	}

	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	* to handle and validate the click events
	* of the second tower
	*/
	public void secondTowerGeneralClick(){
		if (source==0){
			source=2; 
			flashRegion(tower2BtnContainer, Color.CYAN);
		}
		else if (source!=2){
			switch(source){
				case 1:
					if(firstTower.getChildren().size() !=0){
						if (secondTower.getChildren().size()!=0){
							if ((((Rectangle)firstTower.getChildren().get(0)).getWidth())<(((Rectangle)secondTower.getChildren().get(0)).getWidth())){
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
							if ((((Rectangle)thirdTower.getChildren().get(0)).getWidth())<(((Rectangle)secondTower.getChildren().get(0)).getWidth())){
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
	
	
	
	/**
	* to handle and validate the click events
	* of the third tower
	*/
	public void thirdTowerGeneralClick(){
	
		if (source==0){
			source=3; 
			flashRegion(tower3BtnContainer, Color.CYAN);
		}
		else if (source!=3){
			switch(source){
				case 1:
					if(firstTower.getChildren().size() !=0){
						if (thirdTower.getChildren().size()!=0){
							if ((((Rectangle)firstTower.getChildren().get(0)).getWidth())<(((Rectangle)thirdTower.getChildren().get(0)).getWidth())){
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
							if ((((Rectangle)secondTower.getChildren().get(0)).getWidth())<(((Rectangle)thirdTower.getChildren().get(0)).getWidth())){
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


	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * to handle the click on the Undo Move Button
	 */
	public void undoMoveHandler(){
		 undoMove.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent arg0) {
			   if (moves.size()!=0){ // if there are moves
				   Rectangle rect = (Rectangle)resolveVBoxNumber(moves.get(moves.size()-1)[1]).getChildren().get(0); // get the last disc moved from the last tower involved that is recored in the ArrayList 
				   createDisc(moves.get(moves.size()-1)[0], rect.getId(), String.valueOf(rect.getWidth()), rect.getFill().toString()); // create the disc in the source (the opposite operation)
				   resolveVBoxNumber(moves.get(moves.size()-1)[1]).getChildren().remove(0); // remove the disc from the destination (the opposite operation)
				   moves.remove(moves.size()-1); // remove the record of last move from the ArrayList
				   move-=2; // decrement the move by 2 (because when creating the disc above the method incremnts it by 1)
				   updateNumberOfMoves();
			   }
			}});	 
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	/**
	 * important method that will be invoked every time
	 * the disc moves, it creates the disc in the tower specified
	 * in the first parameter which is the node itself 
	 * @param tower
	 * @param discId
	 * @param width
	 * @param color
	 */
	public void createDisc(Node tower, String discId, String width, String color){
		// create te disc according to the attributes coming via the parameters
		Rectangle rect = new Rectangle(Double.parseDouble(width),20); // the height is fixed 20
		rect.setFill(Color.valueOf(color));
		rect.setId(discId);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(3);
		((VBox) tower).getChildren().add(0,rect);
		
		// binding to the scene changes the original width of the discs
		int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
						(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
						(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
						(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
		
		rect.widthProperty().bind(scene.widthProperty().divide(3).subtract(250-origWidth)); // now bind again similar to ones when towers initiated at the begining 
		rect.heightProperty().bind(scene.heightProperty().divide(19));
		move++;
		updateNumberOfMoves();
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * overload the createDisc method to create disc in the 
	 * tower specified by the first parameter but this time 
	 * the parameter contains the number of the tower
	 * 1 , 2 or 3
	 * @param towerNumber
	 * @param discId
	 * @param width
	 * @param color
	 */
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
	
	
	
	/**
	 * to color the tower button region temporary (flash)
	 * @param region
	 * @param color
	 */
	public void flashRegion(Region region, Color color){
		region.setBackground(new Background( new BackgroundFill(color, null, null )));
		PauseTransition delay = new PauseTransition(Duration.seconds(0.3)); //PauseTransition is for a one off pause, it will delay 0.3 second before back to the original color again
		delay.setOnFinished(event -> {region.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));}); // event written in lambda style
		delay.play();					
	}
	
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	/**
	 * convert the gesture name of the event into number
	 * to resolve the towers names
	 * @param obj
	 * @return result
	 */
	public int resolveGestureName(Object obj){
		int result= (((VBox)obj).getId().equals("firstTower"))? 1: // Ternary operator style 
			  			(((VBox)obj).getId().equals("secondTower"))? 2:
			  				(((VBox)obj).getId().equals("thirdTower"))? 3:0;							
		return result;
	}


	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * this method color the tower button container
	 * specified by the parameters 
	 * @param obj
	 * @param color
	 */
	public void resolveRegionNameToColorButton(Object obj, Color color){
		int region = resolveGestureName(obj); // resolve the gesture name
		switch(region){ // start coloring according to the tower passed in the parameter
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
	
	
	/**
	 * resolves the tower represented by its number
	 * to the tower name
	 * @param number
	 * @return result
	 */
	private VBox resolveVBoxNumber(int number){
		VBox result= (number==1)? firstTower:
						(number==2)? secondTower:
							(number==3)? thirdTower:null;
		return result;
	}


	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * update the number of moves and set
	 * the number in the TextField numberOfMoves
	 */
	public void updateNumberOfMoves(){
		numberOfMoves.setText(String.valueOf(move));
	}
	

	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		
	/**
	 * it will show at the end of the game to
	 * inform success
	 */
	public void congrats(){
		scene.addEventHandler(Event.ANY, new EventHandler<Event>(){
			@Override
			public void handle(Event event) {
			if(!end){ // if it's not the end
			    if(secondTower.getChildren().size()==numberOfDiscs){ // check if the second towers contain all discs
		        	  Alert alert = new Alert (AlertType.INFORMATION, "Congratulation You Win!", ButtonType.OK); // show congrats message box
		        	  end = true; // restart the end boolean
		        	  alert.showAndWait();
		}}}});
	}

		
}
