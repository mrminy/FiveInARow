package AI;

public class PointScore{
	int x, y;
	double score;

	public PointScore(int x, int y, double score) {
		this.x = x;
		this.y = y;
		this.score = score;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public double getScore() {
		return score;
	}
}
