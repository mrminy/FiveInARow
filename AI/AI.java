package AI;

import Game.FiveGUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class AI {
	private int maxDepth;

	public AI() {
	}

	public Point getBestMove(int[][] initialBoardState, int aiPlayerValue, int maxDepth, double maxTime) {
		long startTime = System.nanoTime();
		this.maxDepth = maxDepth;
		Point bestMove = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		double timeUsed = 0.0;
		State initialState = new State(true, initialBoardState, null, aiPlayerValue);
		ArrayList<State> children = initialState.generateChildren();
		Collections.sort(children);

		for (State child : children) {
			// Check for win or loose in next move
			if (BoardUtil.countAllOnBoard(child.getPlayer(), child.getBoardState(), 5) > 0) {
				bestMove = child.getData();
				bestScore = child.heuristicEvaluation();
				System.out.println("break1");
				System.out.println("Move score: " + bestScore + ", time used (s): " + timeUsed / 1000.0);
				return bestMove;
			}
		}

//		Map<Point, Integer> serverData = new ConcurrentHashMap<>();
//		ArrayList<Integer> currentValues = new ArrayList<>();
//		children.parallelStream().forEach((child) -> {
//			ArrayList<Integer> copyList = new ArrayList<>();
//			copyList.addAll(currentValues);
//			Collections.sort(copyList);
//			int maxValue = (copyList.size()>0) ? copyList.get(copyList.size()-1) : (int) Double.NEGATIVE_INFINITY;
//			int alpha = (int) miniMax(child, maxDepth-1, maxValue, Double.POSITIVE_INFINITY);
//			currentValues.add(alpha);
//			serverData.put(child.getData(), alpha);
//		});

		for (State child : children) {
			double alpha = miniMax(child, maxDepth - 1, bestScore, Double.POSITIVE_INFINITY);
//			double alpha = negamax(child, maxDepth - 1, bestScore, Double.POSITIVE_INFINITY, 1);
			if (alpha > bestScore || bestMove == null) {
				bestMove = child.getData();
				bestScore = alpha;
			}

			if (timeUsed > maxTime) {
				System.out.println("breakTime");
				break;
			}
		}

//		for (Point point : serverData.keySet()){
//			Integer nextValue = serverData.get(point);
//			if(bestMove == null || nextValue>bestScore){
//				bestScore = nextValue;
//				bestMove = point;
//			}
//		}

		long endTime = System.nanoTime();
		timeUsed += (endTime - startTime) / 1000000;

		System.out.println("Move score: " + bestScore + ", time used (s): " + timeUsed / 1000.0);
		FiveGUI.saveData();
		FiveGUI.resetData();
		return bestMove;
	}

	/**
	 * Does not work yet...
	 */
	private double negamax(State currentNode, int depth, double alpha, double beta, double color) {
		if (depth==0 || currentNode.isTerminal()) {
			return currentNode.heuristicEvaluation();
		}
		double bestValue = Double.NEGATIVE_INFINITY;
		ArrayList<State> childNodes = currentNode.generateChildren();
//		OrderMoves(childNodes) // TODO

		for (State child : childNodes){
			double val = -negamax(child, depth-1, -beta, alpha, -color);
			bestValue = Math.max(bestValue, val);
			alpha = Math.max(alpha, val);
			if(alpha>beta){
				break;
			}
		}
		return bestValue;
}


	private double miniMax(State currentNode, int depth, double alpha, double beta) {
		if (depth <= 0 || currentNode.isTerminal()) {
			return currentNode.heuristicEvaluation();
		}
		if (currentNode.isMaxState()) {
			double currentAlpha = Double.NEGATIVE_INFINITY;
			for (State child : currentNode.generateChildren()) {
				currentAlpha = Math.max(currentAlpha, miniMax(child, depth - 1, alpha, beta));
				alpha = Math.max(alpha, currentAlpha);
				if (alpha >= beta) {
					return alpha;
				}
			}
			return currentAlpha;
		}
		double currentBeta = Double.POSITIVE_INFINITY;
		ArrayList<State> children = currentNode.generateChildren();
		if(depth==maxDepth-1){
			Collections.sort(children);
		}
		for (State child : children) {
			currentBeta = Math.min(currentBeta, miniMax(child, depth - 1, alpha, beta));
			beta = Math.min(beta, currentBeta);
			if (beta <= alpha) {
				return beta;
			}
		}
		return currentBeta;
	}
}
