package AI;

import Game.FiveGUI;
import Game.FiveLogic;

import java.awt.*;
import java.util.ArrayList;

public class State implements Comparable<State>{
	private boolean maxState;
	private int[][] boardState;
	private Point data;
	private int player;

	public State(boolean maxState, int[][] boardState, Point data, int player) {
		this.maxState = maxState;
		this.boardState = boardState;
		this.data = data;
		this.player = player;
	}

	public ArrayList<State> generateChildren(){
		// Stupid way to do it. Find a better algorithm!
		ArrayList<State> children = new ArrayList<>();
		FiveLogic fiveLogic = new FiveLogic(boardState.length, boardState[0].length, FiveGUI.AI_MOVE_FIRST);
		fiveLogic.setBoardState(boardState);

		int nextPlayer = (maxState) ? player : BoardUtil.reversePlayer(player);

		ArrayList<Point> childrenPoints = BoardUtil.getPossibleChildren(nextPlayer, boardState, FiveGUI.MAX_CHILDREN);
		if(childrenPoints.size()>0) {
//			System.out.println("Pruning children...");
			for (Point point : childrenPoints) {
				int[][] newBoardState = new int[boardState.length][boardState[0].length];
				for (int k = 0; k < boardState.length; k++) {
					System.arraycopy(boardState[k], 0, newBoardState[k], 0, boardState[k].length);
				}
				// Create the change in the newBoardState
				newBoardState[point.y][point.x] = nextPlayer;
				State child = new State(!maxState, newBoardState, point, player);
				children.add(0, child);
			}
		}else {
//			System.out.println("Checks all positions...");
			for (int i = 0; i < boardState.length; i++) {
				for (int j = 0; j < boardState[i].length; j++) {
					if (boardState[i][j] == 0 && BoardUtil.hasNeighbor(i, j, boardState)) {
						// Add new children
						// Make deep copy of boardState
						int[][] newBoardState = new int[boardState.length][boardState[0].length];
						for (int k = 0; k < boardState.length; k++) {
							System.arraycopy(boardState[k], 0, newBoardState[k], 0, boardState[k].length);
						}

						// Create the change in the newBoardState
						newBoardState[i][j] = nextPlayer;
						State child = new State(!maxState, newBoardState, new Point(j, i), player);
						children.add(child);
					}
				}
			}
		}

		return children;
	}

	public int getPlayer() {
		return player;
	}

	public int[][] getBoardState() {
		return boardState;
	}

	public Point getData() {
		return data;
	}

	public boolean isMaxState() {
		return maxState;
	}

	/**
	 * Checks the state of the board for wins or ties
	 * @return true if win or tie, false if still moves to go
	 */
	public boolean isTerminal() {
		return (BoardUtil.countAllOnBoard(player, boardState, 5)>0 ||
				BoardUtil.countAllOnBoard(BoardUtil.reversePlayer(player), boardState, 5)>0);
	}

	public double heuristicEvaluation(){
		return BoardUtil.countAllPatternsOnBoard(player, boardState);
	}

	@Override
	public int compareTo(State o) {
		double otherValue = o.heuristicEvaluation();
		return (int) (heuristicEvaluation() - otherValue);
	}
}
