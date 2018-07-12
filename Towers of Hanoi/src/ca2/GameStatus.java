package ca2;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class GameStatus implements Serializable{

	private static final long serialVersionUID = 1L;
	
	ArrayList<String[]> firstTower, secondTower, thirdTower;
	public ArrayList<int[]>moves;
	public int move, numberOfDiscs;
	
	/**
	 * constructor of GameStatus
	 * @param game
	 */
	public GameStatus(TowersOfHanoi game){
		firstTower = new ArrayList<String[]>();
		secondTower = new ArrayList<String[]>();
		thirdTower = new ArrayList<String[]>();
		if (game!=null){
			for(int i=0; i<game.firstTower.getChildren().size(); i++){
				firstTower.add(new String[]{String.valueOf(((Rectangle)game.firstTower.getChildren().get(i)).getWidth()), 
									String.valueOf(((Rectangle)game.firstTower.getChildren().get(i)).getFill()), 
									((Rectangle)game.firstTower.getChildren().get(i)).getId()});
			}
			
			for(int i=0; i<game.secondTower.getChildren().size(); i++){
				secondTower.add(new String[]{String.valueOf(((Rectangle)game.secondTower.getChildren().get(i)).getWidth()), 
									String.valueOf(((Rectangle)game.secondTower.getChildren().get(i)).getFill()), 
									((Rectangle)game.secondTower.getChildren().get(i)).getId()});
			}
			for(int i=0; i<game.thirdTower.getChildren().size(); i++){
				thirdTower.add(new String[]{String.valueOf(((Rectangle)game.thirdTower.getChildren().get(i)).getWidth()), 
								String.valueOf(((Rectangle)game.thirdTower.getChildren().get(i)).getFill()), 
								((Rectangle)game.thirdTower.getChildren().get(i)).getId()});
			}
			moves = game.moves;
			move = game.move;
			numberOfDiscs = Integer.parseInt(game.comboBox.getValue());
		}
		
		
	}
	
		

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * to overload the constructor
	 * @param game
	 */
	public GameStatus(TowersOfHanoiAlternative game){
		firstTower = new ArrayList<String[]>();
		secondTower = new ArrayList<String[]>();
		thirdTower = new ArrayList<String[]>();
		if (game!=null){
			for(int i=0; i<game.firstTower.getChildren().size(); i++){
				firstTower.add(new String[]{String.valueOf(((Rectangle)game.firstTower.getChildren().get(i)).getWidth()), 
									String.valueOf(((Rectangle)game.firstTower.getChildren().get(i)).getFill()), 
									((Rectangle)game.firstTower.getChildren().get(i)).getId()});
			}
			
			for(int i=0; i<game.secondTower.getChildren().size(); i++){
				secondTower.add(new String[]{String.valueOf(((Rectangle)game.secondTower.getChildren().get(i)).getWidth()), 
									String.valueOf(((Rectangle)game.secondTower.getChildren().get(i)).getFill()), 
									((Rectangle)game.secondTower.getChildren().get(i)).getId()});
			}
			for(int i=0; i<game.thirdTower.getChildren().size(); i++){
				thirdTower.add(new String[]{String.valueOf(((Rectangle)game.thirdTower.getChildren().get(i)).getWidth()), 
								String.valueOf(((Rectangle)game.thirdTower.getChildren().get(i)).getFill()), 
								((Rectangle)game.thirdTower.getChildren().get(i)).getId()});
			}
			moves = game.moves;
			move = game.move;
			numberOfDiscs = Integer.parseInt(game.comboBox.getValue());
		}
	}
	
	
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	/**
	 * to retrieve saved TowersOf Hanoi game status  
	 * @param status
	 * @param primaryStage
	 * @return game
	 */
	public static TowersOfHanoi retrieve(GameStatus status, Stage primaryStage){
		TowersOfHanoi game = new TowersOfHanoi(primaryStage);
		game.comboBox.setValue(String.valueOf(status.numberOfDiscs));
		try{game.firstTower.getChildren().clear();}catch(Exception e){} // because when comboBox value is set, it initiates the first tower again 
																		// but we need the tower empty to fill it out with the bricks from the saved game
		for(int i=0; i<status.firstTower.size(); i++){
			double width = Double.parseDouble(status.firstTower.get(i)[0]);
			Color color = Color.valueOf(status.firstTower.get(i)[1]);
			String discId = status.firstTower.get(i)[2];
			Rectangle rect = new Rectangle (width, 25, color);
			rect.setId(discId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			game.firstTower.getChildren().add(rect);
			int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
				(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
				(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
				(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
			rect.widthProperty().bind(game.scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(game.scene.heightProperty().divide(19));
		}
		

		for(int i=0; i<status.secondTower.size(); i++){
			double width = Double.parseDouble(status.secondTower.get(i)[0]);
			Color color = Color.valueOf(status.secondTower.get(i)[1]);
			String discId = status.secondTower.get(i)[2];
			Rectangle rect = new Rectangle (width, 25, color);
			rect.setId(discId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			game.secondTower.getChildren().add(rect);	
			int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
				(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
				(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
				(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
			rect.widthProperty().bind(game.scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(game.scene.heightProperty().divide(19));
		}
		
		

		for(int i=0; i<status.thirdTower.size(); i++){
			double width = Double.parseDouble(status.thirdTower.get(i)[0]);
			Color color = Color.valueOf(status.thirdTower.get(i)[1]);
			String discId = status.thirdTower.get(i)[2];
			Rectangle rect = new Rectangle (width, 25, color);
			rect.setId(discId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			game.thirdTower.getChildren().add(rect);
			int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
				(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
				(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
				(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
			rect.widthProperty().bind(game.scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(game.scene.heightProperty().divide(19));
		}
		
		
		game.move = status.move;
		game.moves = status.moves;
		game.updateNumberOfMoves();
		return game;
	}
	

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
	
	
	/**
	 * to retrieve saved TowersOfHanoiAlternative game status
	 * @param status
	 * @param primaryStage
	 * @return game
	 */
	public static TowersOfHanoiAlternative retrieveAlternative(GameStatus status, Stage primaryStage){
		TowersOfHanoiAlternative game = new TowersOfHanoiAlternative(primaryStage);
		game.comboBox.setValue(String.valueOf(status.numberOfDiscs));
		try{game.firstTower.getChildren().clear();}catch(Exception e){}
		
		for(int i=0; i<status.firstTower.size(); i++){
			double width = Double.parseDouble(status.firstTower.get(i)[0]);
			Color color = Color.valueOf(status.firstTower.get(i)[1]);
			String discId = status.firstTower.get(i)[2];
			Rectangle rect = new Rectangle (width, 25, color);
			rect.setId(discId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			game.firstTower.getChildren().add(rect);
			int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
				(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
				(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
				(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
			rect.widthProperty().bind(game.scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(game.scene.heightProperty().divide(19));
		}
		

		for(int i=0; i<status.secondTower.size(); i++){
			double width = Double.parseDouble(status.secondTower.get(i)[0]);
			Color color = Color.valueOf(status.secondTower.get(i)[1]);
			String discId = status.secondTower.get(i)[2];
			Rectangle rect = new Rectangle (width, 25, color);
			rect.setId(discId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			game.secondTower.getChildren().add(rect);	
			int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
				(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
				(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
				(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
			rect.widthProperty().bind(game.scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(game.scene.heightProperty().divide(19));
		}
		
		

		for(int i=0; i<status.thirdTower.size(); i++){
			double width = Double.parseDouble(status.thirdTower.get(i)[0]);
			Color color = Color.valueOf(status.thirdTower.get(i)[1]);
			String discId = status.thirdTower.get(i)[2];
			Rectangle rect = new Rectangle (width, 25, color);
			rect.setId(discId);
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(3);
			game.thirdTower.getChildren().add(rect);
			int origWidth = (discId.equals("Rect0"))? 30:(discId.equals("Rect1"))? 60:
				(discId.equals("Rect2"))? 90:(discId.equals("Rect3"))? 120:
				(discId.equals("Rect4"))? 150:(discId.equals("Rect5"))? 180:
				(discId.equals("Rect6"))? 210:(discId.equals("Rect7"))? 240:null;
			rect.widthProperty().bind(game.scene.widthProperty().divide(3).subtract(250-origWidth));
			rect.heightProperty().bind(game.scene.heightProperty().divide(19));
		}
		
		
		game.move = status.move;
		game.moves = status.moves;
		game.updateNumberOfMoves();
		return game;
		
		
	}
	
	
	
	
}
