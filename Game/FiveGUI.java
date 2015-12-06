package Game;// Game.FiveGUI.java

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;

import AI.AI;

/////////////////////////////////////////////////// class Game.FiveGUI

/**
 * A Graphical User Interface for a Game.Five-In-A-Row game.
 * This implements the user interface (view and controller),
 * but the logic (model) is implemented in a separate class that
 * knows nothing about the user interface.
 * <p>This subclasses JPanel and puts the some buttons in the north,
 * a graphical display of the board in the center, and
 * a status field in the south.
 * </p>
 * <p>Exercise: This game probably originated on a Go board where
 * the pieces are placed on the intersections, not in the
 * empty spaces.  Change the program to put all pieces on the
 * intersections.
 * </p>
 * <p>Exercise: The Undo button doesn't do anything.  Fix it here
 * in the GUI and in the logic.
 * </p>
 * <p>Exercise: Create a machine player.</p>
 *
 * @author Fred Swartz
 * @version 2004-05-02 Rodenbach
 */
public class FiveGUI extends JPanel implements ActionListener{
	public static DecimalFormat df = new DecimalFormat("#.######");

	//=============================================== instance variables
	private GraphicsPanel boardDisplay_;
	private JTextField statusField_ = new JTextField();
	private FiveLogic gameLogic_;
	private boolean gameOver_ = false;

	private static final Color[] PLAYER_COLOR = {null, Color.BLACK, Color.WHITE};
	private static final String[] PLAYER_NAME = {null, "BLACK", "WHITE"};

	public static final boolean recordData = false;
	public static final int SIZE = 20;
	public static final int MAX_AI_SEARCH = 6;
	public static final int MAX_AI_TIME = 300000;
	public static final boolean AI_MOVE_FIRST = true;
	public static final boolean AI_VS_AI = true;
	public static final int AI_VS_AI_DELAY = 10;
	public static final int MAX_CHILDREN = 10;

	public static ArrayList<int[][]> boardsEvaluated = new ArrayList<>();
	public static ArrayList<Double> evaluations = new ArrayList<>();

	private Timer timer;
	private int moveCounter;

	//====================================================== constructor
	public FiveGUI() {
		this.gameLogic_ = new FiveLogic(SIZE, SIZE, AI_MOVE_FIRST);
		moveCounter = 0;

		//--- Create some buttons
		JButton newGameButton = new JButton("New Game");
		JButton undoButton = new JButton("Undo");
		JButton redoButton = new JButton("Redo");

			timer = new Timer(AI_VS_AI_DELAY, this);
		if(AI_VS_AI){
			timer.start();
		}

		//--- Create control panel
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());
		controlPanel.add(newGameButton);
		controlPanel.add(undoButton);
		controlPanel.add(redoButton);

		//--- Create graphics panel
		boardDisplay_ = new GraphicsPanel();

		//--- Set the layout and add the components
		this.setLayout(new BorderLayout());
		this.add(controlPanel, BorderLayout.NORTH);
		this.add(boardDisplay_, BorderLayout.CENTER);
		this.add(statusField_, BorderLayout.SOUTH);

		if(AI_MOVE_FIRST){
			moveCounter++;
			System.out.println(moveCounter + " - Player 1 made move: " + SIZE/2 + ", " + SIZE/2);
		}

		//-- Add action listeners
		newGameButton.addActionListener(new NewGameAction());
		undoButton.addActionListener(new UndoAction());
		redoButton.addActionListener(new RedoAction());
	}//end constructor

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameLogic_.getGameStatus()==0){
			// make move
			timer.stop();
			boardDisplay_.makeAIMove();
			if(AI_VS_AI) {
				timer.start();
			}
		}else{
			timer.stop();
		}
	}

	//////////////////////////////////////////////// class GraphicsPanel
	// This is defined inside the outer class so that
	// it can use the game logic variable.
	class GraphicsPanel extends JPanel implements MouseListener {
		private static final int ROWS = FiveGUI.SIZE;
		private static final int COLS = FiveGUI.SIZE;
		private static final int CELL_SIZE = 30; // Pixels
		private static final int OUTER_PADDING = 20;
		private static final int WIDTH = COLS * CELL_SIZE + OUTER_PADDING;
		private static final int HEIGHT = ROWS * CELL_SIZE + OUTER_PADDING;

		//================================================== constructor
		public GraphicsPanel() {
			this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
			this.setBackground(Color.GRAY);
			this.addMouseListener(this);  // Listen own mouse events.
		}//end constructor

		//============================================== paintComponent
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			//-- Paint grid (could be done once and saved).
			for (int r = 0; r < ROWS; r++) {  // Horizontal lines
				if(r != 0) {
					g.drawLine(0, r * CELL_SIZE, WIDTH, r * CELL_SIZE);
				}
				g.drawString(r+"", HEIGHT-OUTER_PADDING/2, r*CELL_SIZE+OUTER_PADDING);
			}
			for (int c = 0; c < COLS; c++) {
				if(c != 0) {
					g.drawLine(c * CELL_SIZE, 0, c * CELL_SIZE, HEIGHT);
				}
				g.drawString(c+"", c*CELL_SIZE+10, WIDTH-OUTER_PADDING/2);
			}

			//-- Draw players pieces.
			for (int r = 0; r < ROWS; r++) {
				for (int c = 0; c < COLS; c++) {
					int x = c * CELL_SIZE;
					int y = r * CELL_SIZE;
					int who = gameLogic_.getPlayerAt(r, c);
					if (who != gameLogic_.EMPTY) {
						g.setColor(PLAYER_COLOR[who]);
						g.fillOval(x + 2, y + 2, CELL_SIZE - 4, CELL_SIZE - 4);
					}
				}
			}
		}//end paintComponent

		//======================================== listener mousePressed
		public void mousePressed(MouseEvent e) {
			if(!AI_VS_AI) {
				//--- map x,y coordinates into a row and col.
				int col = e.getX() / CELL_SIZE;
				int row = e.getY() / CELL_SIZE;
				boolean validMove = makeMove(col, row);

				if(validMove){
					timer.start();
				}
			}
		}//end mousePressed

		public boolean makeAIMove() {
			int thisPlayer = gameLogic_.getNextPlayer();
			AI ai = new AI();
			Point bestMove = ai.getBestMove(gameLogic_.getBoard_(), thisPlayer, MAX_AI_SEARCH, MAX_AI_TIME);
			if (bestMove != null) {
				boolean aiValidMove = makeMove(bestMove.x, bestMove.y);
				if (!aiValidMove) {
					System.out.println("Made invalid move: " + bestMove.x + ", " + bestMove.y);
					return false;
				}
			} else {
				System.out.println("BAD MOVE!");
				return false;
			}
			return true;
		}

		public boolean makeMove(int col, int row) {
			int currentOccupant = gameLogic_.getPlayerAt(row, col);
			if (!gameOver_ && currentOccupant == gameLogic_.EMPTY) {
				int player = gameLogic_.getNextPlayer();
				gameLogic_.move(row, col);
				switch (gameLogic_.getGameStatus()) {
					case 1: // Player one wins.  Game over.
						gameOver_ = true;
						statusField_.setText("BLACK WINS");
						break;
					case 2: // Player two wins.  Game over.
						gameOver_ = true;
						statusField_.setText("WHITE WINS");
						break;

					case FiveLogic.TIE:  // Tie game.  Game over.
						gameOver_ = true;
						statusField_.setText("TIE GAME");
						break;

					default:
						showNextPlayer();
				}
				this.repaint();  // Show any updates to game.
				moveCounter++;
				System.out.println(moveCounter + " - Player " + player + " made move: " + col + ", " + row);

				if(gameOver_){
					// Save moves
					if(recordData) {
						System.out.println("SAVED DATA");
						saveData();
					}
				}

				return true;
			} else {  // Not legal
				Toolkit.getDefaultToolkit().beep();
				return false;
			}
		}

		//========================================== ignore these events
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}//end inner class GraphicsPanel

	//======================================= untility method showNextPlayer
	private void showNextPlayer() {
		statusField_.setText(PLAYER_NAME[gameLogic_.getNextPlayer()] + " to play");
	}//end showNextPlayer


	///////////////////////////////////////// inner class NewGameAction
	private class NewGameAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			saveData();
			resetData();
			gameLogic_.reset(FiveGUI.AI_MOVE_FIRST);
			gameOver_ = false;
			showNextPlayer();
			boardDisplay_.repaint();
		}
	}//end inner class NewGameAction

	private class UndoAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gameLogic_.undo();
			showNextPlayer();
			boardDisplay_.repaint();
		}
	}//end inner class NewGameAction

	private class RedoAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			gameLogic_.redo();
			showNextPlayer();
			boardDisplay_.repaint();
		}
	}//end inner class NewGameAction

	private static int[] convertToOneDimBoard(int[][] board){
		ArrayList<Integer> flatlist = new ArrayList<>();
		for (int[] aBoard : board) {
			for (int anABoard : aBoard) {
				int value = (anABoard==2) ? -1 : anABoard;
				flatlist.add(value);
			}
		}

		int[] flatArr = new int[flatlist.size()];
		for (int i = 0; i < flatlist.size(); i++) {
			flatArr[i] = flatlist.get(i);
		}
		return flatArr;
	}

	public static void saveData(){
		if(recordData) {
			try {
				PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter("filetestansw.txt", true)));
				for (double evaluation : evaluations) {
					pr.println(df.format(evaluation));
				}
				pr.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				PrintWriter pr = new PrintWriter(new BufferedWriter(new FileWriter("filetest.txt", true)));
				for (int[][] board : boardsEvaluated) {
					int[] oneDimBoard = convertToOneDimBoard(board);
					String line = "";
					for (int value : oneDimBoard) {
						line += value + " ";
					}
					line = line.substring(0, line.length() - 1);
					pr.println(line);
				}
				pr.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void resetData(){
		evaluations = new ArrayList<>();
		boardsEvaluated = new ArrayList<>();
	}

}//end class Game.FiveGUI