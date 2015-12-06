package AI;

import Game.FiveGUI;
import com.sun.deploy.util.ArrayUtil;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class BoardUtil {
	public static int emptyTiles(int[][] boardState) {
		int emptyTiles = 0;
		for (int[] aBoardState : boardState) {
			for (int anABoardState : aBoardState) {
				if (anABoardState == 0) {
					emptyTiles++;
				}
			}
		}
		return emptyTiles;
	}

	public static double boardValues(Point point, int[][] boardState) {
		double maxValue = boardState.length / 2;
		double d_x = Math.abs(maxValue - point.getX());
		double d_y = Math.abs(maxValue - point.getY());
//		return (maxValue * 2 - (d_x + d_y));
		return (maxValue - d_x + maxValue - d_y) / (maxValue * 2.0);
	}

	public static double boardValues(int x, int y, int[][] boardState) {
		double maxValue = boardState.length / 2;
		double d_x = Math.abs(maxValue - x);
		double d_y = Math.abs(maxValue - y);
//		return (maxValue * 2 - (d_x + d_y));
		return (maxValue - d_x + maxValue - d_y) / (maxValue * 2.0);
	}

	public static int boardValuesSum(int player, int[][] boardState) {
		double sum = 0.0;
		double sumOtherPlayer = 0.0;
		for (int i = 0; i < boardState.length; i++) {
			for (int j = 0; j < boardState[i].length; j++) {
				if (boardState[i][j] == player)
					sum += boardValues(new Point(j, i), boardState);
				if (boardState[i][j] == reversePlayer(player))
					sumOtherPlayer -= boardValues(new Point(j, i), boardState);
			}
		}
		return (int) (sum + sumOtherPlayer);
	}

	public static boolean hasNeighbor(int i, int j, int[][] boardState) {
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				int row = i + k;
				int col = l + j;
				if (row >= 0 && row < boardState.length && col >= 0 && col < boardState.length && boardState[row][col] != 0) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean hasNeighbor(int i, int j, int[][] boardState, int player) {
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				int row = i + k;
				int col = l + j;
				if (row >= 0 && row < boardState.length && col >= 0 && col < boardState.length && boardState[row][col] == player) {
					return true;
				}
			}
		}
		return false;
	}

	public static String printBoard(int[][] boardState) {
		String out = "";
		for (int[] aBoardState : boardState) {
			for (int j = 0; j < aBoardState.length; j++) {
				out += aBoardState[j] + ", ";
			}
			out += "\n";
		}
		return out;
	}

	public static int reversePlayer(int player) {
		return (player == 1) ? 2 : 1;
	}

	public static int countAllOnBoard(int player, int[][] boardState, int countTo) {
		int counter = 0;
		for (int i = 0; i < boardState.length; i++) {
			for (int j = 0; j < boardState[i].length; j++) {
				if (boardState[i][j] == player && countOnBoard(i, j, player, boardState, countTo)) {
					counter++;
				}
			}
		}
		return counter;
	}

	public static int countAllPatternOnBoard(int[] pattern, int[][] boardState) {
		int counter = 0;
		for (int i = 0; i < boardState.length; i++) {
			for (int j = 0; j < boardState[i].length; j++) {
				if (boardState[i][j] == pattern[0]) {
					counter += countOnBoard(i, j, pattern, boardState);
				}
			}
		}
		return counter;
	}

	public static boolean countOnBoard(int r, int c, int checkPlayer, int[][] boardState, int countTo) {
		if (boardState[r][c] != checkPlayer)
			return false;
		int[][] dr_dc = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
		for (int[] aDr_dc : dr_dc) {
			int dr = aDr_dc[0];
			int dc = aDr_dc[1];
			int maxR = r + dr * (countTo - 1);
			int maxC = c + dc * (countTo - 1);
			int counter = 0;
			if (maxR >= 0 && maxC >= 0 && maxR < boardState.length && maxC < boardState[0].length) {
				for (int i = 1; i < countTo; i++) {
					if (boardState[r + dr * i][c + dc * i] != checkPlayer) {
						break;
					} else {
						counter++;
					}
				}
			}
			if (counter == countTo - 1) {
				return true;
			}
		}
		return false;
	}

	public static int countOnBoard(int r, int c, int[] pattern, int[][] boardState) {
		if (boardState[r][c] != pattern[0])
			return 0;
		int[][] dr_dc = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
		int patternCounter = 0;
		for (int[] aDr_dc : dr_dc) {
			int dr = aDr_dc[0];
			int dc = aDr_dc[1];
			int maxR = r + dr * (pattern.length - 1);
			int maxC = c + dc * (pattern.length - 1);
			int counter = 0;
			if (maxR >= 0 && maxC >= 0 && maxR < boardState.length && maxC < boardState[0].length) {
				for (int i = 1; i < pattern.length; i++) {
					if (boardState[r + dr * i][c + dc * i] != pattern[i]) {
						break;
					} else {
						counter++;
					}
				}
				if (counter == pattern.length - 1) {
					patternCounter++;
				}
			}
		}
		return patternCounter;
	}

	public static int patternMatchForPoint(int i, int j, int patternPosition, int[] pattern, int[][] boardState) {
		if(boardState[i][j] != pattern[patternPosition]) return 0;

		int patternLength = pattern.length;
		int[] patternAfter = Arrays.copyOfRange(pattern, patternPosition, patternLength);
		int[] patternBefore = Arrays.copyOfRange(pattern, 0, patternPosition+1);
		List<Integer> list = new ArrayList<>();
		for (int k = patternBefore.length-1; k >= 0; k--) {
			list.add(patternBefore[k]);
		}
		for (int k = 0; k < patternBefore.length; k++) {
			patternBefore[k] = list.get(k);
		}


		int[][] dr_dcAfter = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
		int totalCounter = 0;

		for (int[] aDr_dcAfter : dr_dcAfter) {
			int drAfter = aDr_dcAfter[0];
			int dcAfter = aDr_dcAfter[1];
			int drBefore = drAfter * -1;
			int dcBefore = dcAfter * -1;
			int maxR = i + drAfter * (patternAfter.length - 1);
			int maxC = j + dcAfter * (patternAfter.length - 1);
			int minR = i + drBefore * (patternBefore.length - 1);
			int minC = j + dcBefore * (patternBefore.length - 1);

			int localCounter = 0;

			if (maxR >= 0 && maxC >= 0 && maxR < boardState.length && maxC < boardState[0].length &&
					minR >= 0 && minC >= 0 && minR < boardState.length && minC < boardState[0].length) {
				if(patternAfter.length>0) {
					for (int a = 1; a < patternAfter.length; a++) {
						int r = i + drAfter * a;
						int c = j + dcAfter * a;
						if (boardState[i + drAfter * a][j + dcAfter * a] != patternAfter[a]) {
							break;
						} else {
							localCounter++;
						}
					}
				}
				if(patternBefore.length>0) {
					for (int b = 1; b < patternBefore.length; b++) {
						int r = i + drBefore * b;
						int c = j + dcBefore * b;
						if (boardState[i + drBefore * b][j + dcBefore * b] != patternBefore[b]) {
							break;
						} else {
							localCounter++;
						}
					}
				}

				if (localCounter == pattern.length-1) {
					totalCounter++;
				}
			}
		}
		return totalCounter;
	}

	public static double checkAllDirectionsForStones(int i, int j, int player, int[][] boardState){
		// Check for five/max in each direction for blocks of player stones
		// This position should be 0
		if(boardState[i][j] != 0) return 0.0;

		int[][] dr_dcAfter = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {-1, 1}, {1, -1}};
		double[] scoreBoard = {0.0, 0.001, 0.01, 0.01, 0.01};
		ArrayList<Integer> inRowCounter = new ArrayList<>();

		for (int[] aDr_dcAfter : dr_dcAfter) {
			int drAfter = aDr_dcAfter[0];
			int dcAfter = aDr_dcAfter[1];
			int maxR = i + drAfter * 4;
			int maxC = j + dcAfter * 4;

			int currentInRow = 0;

			for (int a = 1; a < 5; a++) {
				int r =  i + drAfter*a;
				int c =  i + dcAfter*a;
				if(r >= 0 && c >= 0 && r < boardState.length && c < boardState[0].length){
					if(boardState[r][c] == player){
						currentInRow++;
					}else if(boardState[r][c] == 0){
						inRowCounter.add(currentInRow);
						currentInRow=0;
					}else{
						// If opponent stone is found, break out.
						break;
					}
				}
			}
			if(currentInRow != 0) {
				inRowCounter.add(currentInRow);
			}
		}

		double totalScore = 0;
		for (Integer inRow : inRowCounter){
			totalScore += scoreBoard[inRow];
		}

		return totalScore;
	}

	// TODO make it return a list with points (not empty list) every time!
	public static ArrayList<Point> getPossibleChildren(int p, int[][] boardState, int n) {
		// Iterate trough each square in the board and give it a value.
		// Rate the top n children and return them in the favour for player p.
		int r = BoardUtil.reversePlayer(p);
		int[][] patterns = {{p, p, 0, p, p}, {0, p, p, p, p}, {0, p, p, p, p, 0}, {p, 0, p, p, p}, {0, p, p, p, 0},
				{r, r, 0, r, r}, {0, r, r, r, r}, {0, r, r, r, r, 0}, {r, 0, r, r, r}, {0, r, r, r, 0}};
		int[] patternPosition = {2, 0, 0, 1, 0, 2, 0, 0, 1, 0};
		int finalPatternScore = 10; // For winning positions

		ArrayList<PointScore> pointScores = new ArrayList<>();
		for (int i = 0; i < boardState.length; i++) {
			for (int j = 0; j < boardState[i].length; j++) {
				if (boardState[i][j] == 0) {
					double pointScore = 0.0;
					for (int k = 0; k < patterns.length; k++) {
						pointScore += finalPatternScore*patternMatchForPoint(i, j, patternPosition[k], patterns[k], boardState);
					}

					// TODO function for all cells
					if(hasNeighbor(i, j, boardState, p)) {
						// Number of blocking stones + number of created stones in row. NB: the stones blocked/created has to have an open side
						pointScore += checkAllDirectionsForStones(i, j, p, boardState);
					}
					if(hasNeighbor(i, j, boardState, r)) {
						pointScore += checkAllDirectionsForStones(i, j, r, boardState);
					}


					if(pointScore>0.0) {
						pointScores.add(new PointScore(j, i, pointScore));
					}
				}
			}
		}

		if(pointScores.size()>n) {
			// Sorts and selects top n points
			Collections.sort(pointScores, new Comparator<PointScore>() {
				@Override
				public int compare(PointScore o1, PointScore o2) {
					return Double.compare(o2.getScore(), o1.getScore());
				}
			});
			if (pointScores.size() < n) n = pointScores.size() - 1;
			pointScores = new ArrayList<>(pointScores.subList(0, n));
		}

		ArrayList<Point> topPoints = new ArrayList<>();
		for (PointScore topPointScore : pointScores) {
			topPoints.add(new Point(topPointScore.x, topPointScore.y));
		}
		return topPoints;
	}

	public static double countAllPatternsOnBoard(int p, int[][] boardState) {
		int r = BoardUtil.reversePlayer(p);
		int[][] patterns = {{p, p, 0, p, p}, {p, p, p, p, 0}, {0, p, p, p, 0}, {0, p, p, p, p, 0}, {p, p, p, p, p},
				{r, r, 0, r, r}, {r, r, r, r, 0}, {0, r, r, r, 0}, {0, r, r, r, r, 0}, {r, r, r, r, r}};
		double[] scores = {0.00005, 0.00005, 0.001, 0.01, 1, -0.00005, -0.00005, -0.001, -0.01, -1};
		double positionScoreEvaluation = 0.005;
		double score = 0.0;

		for (int i = 0; i < boardState.length; i++) {
			for (int j = 0; j < boardState[i].length; j++) {
				if (hasNeighbor(i, j, boardState)) {
					if (boardState[i][j] != 0) {
						double positionScore = BoardUtil.boardValues(j, i, boardState) * positionScoreEvaluation;
						if (boardState[i][j] == p) score += positionScore;
						else if (boardState[i][j] == r) score -= positionScore;
					}
					for (int k = 0; k < patterns.length; k++) {
						score += scores[k] * countOnBoard(i, j, patterns[k], boardState);
					}
				}
			}
		}
		score = (score <= -1.0) ? -1.0 : score;
		score = (score >= 1.0) ? 1.0 : score;

		if (FiveGUI.recordData && Math.random() < 0.005 && p == 1) {
			FiveGUI.boardsEvaluated.add(boardState);
			FiveGUI.evaluations.add(score);
		}
		return score;
	}

}
