package tests;

import AI.BoardUtil;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;

public class BoardUtilTests {
	@Test
	public void testCountOnBoard(){
		int[][] board = {{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0}};
		int[][] board2 = {{0,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0}};
		int[][] board3 = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}};

		Assert.assertEquals(true, BoardUtil.countOnBoard(0,0,1,board,5));
		Assert.assertEquals(false, BoardUtil.countOnBoard(1,1,1,board2,5));
		Assert.assertEquals(false, BoardUtil.countOnBoard(0,0,1,board2,5));
		Assert.assertEquals(false, BoardUtil.countOnBoard(1,0,1,board2,5));
		Assert.assertEquals(true, BoardUtil.countOnBoard(1,0,1,board2,4));
		Assert.assertEquals(true, BoardUtil.countOnBoard(0,0,1,board3,5));
		Assert.assertEquals(true, BoardUtil.countOnBoard(0,0,1,board3,4));
	}

	@Test
	public void testCountAllOnBoard(){
		int[][] board = {{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0}};
		int[][] board2 = {{0,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0},{1,0,0,0,0}};
		int[][] board3 = {{1,0,0,0,0},{0,1,0,0,0},{0,0,1,0,0},{0,0,0,1,0},{0,0,0,0,1}};

		Assert.assertEquals(1, BoardUtil.countAllOnBoard(1,board,5));
		Assert.assertEquals(0, BoardUtil.countAllOnBoard(1,board2,5));
		Assert.assertEquals(1, BoardUtil.countAllOnBoard(1,board2,4));
		Assert.assertEquals(1, BoardUtil.countAllOnBoard(1,board3,5));
	}

	@Test
	public void testCountAllPatternOnBoard(){
		int[][] board = {{0,1,1,1,1,0,0,0,0,0},{1,0,0,0,0,0,0,0,0,0},{1,0,0,0,0,0,0,0,0,0},{1,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,2,2,0,2,2,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{1,2,2,2,2,2,1,0,0,0}};

		int []pattern1 = {0,1,1,1,1,0};
		int []pattern2 = {0,1,1,1,0};
		int []pattern3 = {2,2,0,2,2};

		Assert.assertEquals(1, BoardUtil.countAllPatternOnBoard(pattern1,board));
		Assert.assertEquals(1, BoardUtil.countAllPatternOnBoard(pattern2,board));
		Assert.assertEquals(1, BoardUtil.countAllPatternOnBoard(pattern3,board));
//		Assert.assertEquals(1, BoardUtil.countAllOnBoard(1,board3,5));
	}

	@Test
	public void testBoardValues(){
		int[][] board = {{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},
				{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0},{0,0,0,0,0,0,0,0,0,0}};

		Assert.assertEquals(0.0, BoardUtil.boardValues(new Point(0,0), board), 0.0);
		Assert.assertEquals(1.0, BoardUtil.boardValues(new Point(5,5), board), 0.0);
		Assert.assertEquals(0.0, BoardUtil.boardValues(new Point(10,10), board), 0.0);
		Assert.assertEquals(0.5, BoardUtil.boardValues(new Point(10,5), board), 0.0);
		Assert.assertEquals(0.4, BoardUtil.boardValues(new Point(8,8), board), 0.0);
	}
}
