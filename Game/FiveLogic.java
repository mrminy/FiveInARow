package Game;// Game.FiveLogic.java - Game to get 5 pieces in a row.
///////////////////////////////////////////////// class Game.FiveLogic

import java.util.ArrayList;

/** This class implements the logic (model) for the game of
 Game.Five-In-A-Row.
 <br/>Exercise: The undo function doesn't do anything.  Fix it.
 @author Fred Swartz
 @version 2004-05-02
 */
public class FiveLogic {
	/** Number of board rows. */
	private int maxRows_;
	/** Number of board columns. */
	private int maxCols_;
	/** The board. */
	private int[][] board_;
	/** The player who moves next. */
	private int     nextPlayer_;
	/** Number of moves in the game. */
	private int     moves_ = 0;

	private ArrayList<int[][]> boardHistory;
	private int showingMove = 0;

	//-- Constants
	public  static final int EMPTY   = 0;  // The cell is empty.
	private static final int PLAYER1 = 1;
	public  static final int TIE     = -1; // Game is a tie (draw).

	//================================================== constructor
	public FiveLogic(int rows, int cols, boolean aiToMoveFirst) {
		boardHistory = new ArrayList<>();
		maxRows_ = rows;
		maxCols_ = cols;
		board_ = new int[maxRows_][maxCols_];
		reset(aiToMoveFirst);
	}//end constructor

	public void setBoardState(int[][] boardState){
		this.board_ = boardState;
	}

	public int[][] getBoard_() {
		return board_;
	}

	//================================================= getNextPlayer
	/** Returns the next player. */
	public int getNextPlayer() {
		int oneCounter = 0;
		int twoCounter = 0;
		for (int i = 0; i < board_.length; i++) {
			for (int j = 0; j < board_[i].length; j++) {
				if(board_[i][j] == 1){
					oneCounter++;
				}
				if(board_[i][j] == 2){
					twoCounter++;
				}
			}
		}

		this.nextPlayer_ = (oneCounter<=twoCounter) ? 1 : 2;

		return nextPlayer_;
	}//end getFace

	//=================================================== getPlayerAt
	/** Returns player who has played at particular row and column. */
	public int getPlayerAt(int r, int c) {
		return board_[r][c];
	}//end getPlayerAt

	//========================================================== reset
	/** Clears board to initial state. Makes first move in center. */
	public void reset(boolean aiFirstToMove) {
		for (int r=0; r<maxRows_; r++) {
			for (int c=0; c<maxCols_; c++) {
				board_[r][c] = EMPTY;
			}
		}
		moves_ = 0;  // No moves so far.
		showingMove = 0;
		boardHistory = new ArrayList<>();
		nextPlayer_ = PLAYER1;
		//-- Make first move in center.

		if(aiFirstToMove){
			move(maxCols_/2, maxRows_/2);
		}
	}//end reset

	//=========================================================== move
	/** Play a marker on the board, record it, flip players. */
	public void move(int r, int c) {
		assert board_[r][c] == EMPTY;
		board_[r][c] = nextPlayer_;  // Record this move.
		nextPlayer_ = 3-nextPlayer_; // Flip players
		int[][] newBoardState = new int[board_.length][board_[0].length];
		for (int k = 0; k < board_.length; k++) {
			System.arraycopy(board_[k], 0, newBoardState[k], 0, board_[k].length);
		}
		boardHistory.add(newBoardState);
		moves_++;                    // Increment number of moves.
		showingMove++;
	}//end move

	//=========================================================== undo
	/** Undo the last move made.  Don't go beyond beginning. */
	public void undo() {
		// UNIMPLEMENTED.
		if(showingMove>1) {
			showingMove--;
			board_ = boardHistory.get(showingMove-1);
		}
	}//end undo

	public void redo() {
		// UNIMPLEMENTED.
		if(boardHistory.size()>showingMove) {
			showingMove++;
			board_ = boardHistory.get(showingMove-1);
		}
	}//end undo

	//========================================== utility method count5_
	/** The count5_ utility function returns true if there are five in
	 a row starting at the specified r,c position and
	 continuing in the dr direcection (+1, -1) and
	 similarly for the column c.
	 */
	private boolean count5_(int r, int dr, int c, int dc) {
		int player = board_[r][c];  // remember the player.
		for (int i=1; i<5; i++) {
			if (board_[r+dr*i][c+dc*i] != player) return false;
		}
		return true;  // There were 5 in a row!
	} // count5_

	//=================================================== getGameStatus
	/** -1 = game is tie, 0 = more to play,
	 1 = player1 wins, 2 = player2 wins */
	public int getGameStatus() {
		int row;
		int col;
		int n_up, n_right, n_up_right, n_up_left;

		boolean at_least_one_move;   // true if game isn't a tie

		for (row = 0; row < maxRows_; row++) {
			for (col = 0; col < maxCols_; col++) {
				int p = board_[row][col];
				if (p != EMPTY) {
					// look at 4 kinds of rows of 5
					//  1. a column going up
					//  2. a row going to the right
					//  3. a diagonal up and to the right
					//  4. a diagonal up and to the left

					if (row < maxRows_-4) // Look up
						if (count5_(row, 1, col, 0)) return p;

					if (col < maxCols_-4) { // row to right
						if (count5_(row, 0, col, 1))  return p;

						if (row < maxRows_-4) { // diagonal up to right
							if (count5_(row, 1, col, 1)) return p;
						}
					}

					if (col > 3 && row < maxRows_-4) { // diagonal up left
						if (count5_(row, 1, col, -1)) return p;
					}
				}//endif position wasn't empty
			}//endfor row
		}//endfor col

		// Neither player has won, it's tie if there are empty positions.
		// Game is finished if total moves equals number of positions.
		if (moves_ == maxRows_*maxCols_) {
			return TIE; // Game tied.  No more possible moves.
		} else {
			return 0;  // More to play.
		}
	}//end getGameStatus

}//end class Game.FiveLogic