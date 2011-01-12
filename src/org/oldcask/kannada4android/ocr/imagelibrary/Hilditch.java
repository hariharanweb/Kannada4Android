package org.oldcask.kannada4android.ocr.imagelibrary;

import jjil.core.RgbImage;

public class Hilditch {

	/**
	 * After Thinning the dimensions of the image may have varied considerably
	 * This method finds the bounds of the resulting image, and reduces the size
	 * of the image accordingly
	 * 
	 * @param inputBoolean
	 *            Source image's boolean representation
	 * @param width
	 *            Width of the image t[0].length
	 * @param height
	 *            Height of the image t.length
	 * 
	 * @return Perfectly bounded image
	 */
	private boolean[][] findBounds(boolean inputBoolean[][], int width,
			int height) {
		boolean bound[][];

		int upperBound = findUpperBound(inputBoolean, width, height);
		int lowerBound = findLowerBound(inputBoolean, width, height);
		int rightBound = findRightBound(inputBoolean, width, height);
		int leftBound = findLeftBound(inputBoolean, width, height);

		if (lowerBound - upperBound + 1 < 0 || rightBound - leftBound + 1 < 0)
			return null;
		else
			bound = new boolean[lowerBound - upperBound + 1][rightBound
					- leftBound + 1];
		for (int i1 = 0, i = upperBound; i <= lowerBound; i++, i1++)
			for (int j1 = 0, j = leftBound; j <= rightBound; j++, j1++)
				bound[i1][j1] = inputBoolean[i][j];

		return bound;
	}

	private int findUpperBound(boolean inputBoolean[][], int width, int height) {
		int top = 0;
		while (top < height
				&& (HistogramAnalysis.getHorizontalStrength(inputBoolean, top,
						0, width) == 0 || HistogramAnalysis
						.getHorizontalStrength(inputBoolean, top, 0, width) == width)) {
			top++;
		}
		return top;
	}

	private int findLowerBound(boolean inputBoolean[][], int width, int height) {
		int bot = height - 1;
		while (bot > 0
				&& (HistogramAnalysis.getHorizontalStrength(inputBoolean, bot,
						0, width) == 0 || HistogramAnalysis
						.getHorizontalStrength(inputBoolean, bot, 0, width) == width)) {
			bot--;
		}
		return bot;
	}

	private int findLeftBound(boolean inputBoolean[][], int width, int height) {
		int lft = 0;
		while (lft < width
				&& HistogramAnalysis.getVerticalStrength(inputBoolean, 0, lft,
						height) == 0) {
			lft++;
		}
		return lft;
	}

	private int findRightBound(boolean inputBoolean[][], int width, int height) {
		int rgt = width - 1;
		while (rgt > 0
				&& HistogramAnalysis.getVerticalStrength(inputBoolean, 0, rgt,
						height) == 0) {
			rgt--;
		}
		return rgt;
	}

	/**
	 * A direct implementation of the Hilditch algorithm for thinning.
	 * Iteratively thins the image one layer at a time
	 * 
	 * @param inputBoolean
	 *            Source image boolean
	 * @param width
	 *            Width of the image t[0].length
	 * @param height
	 *            Height of the image t.length
	 * @param noOfLayers
	 *            Number of layers to remove
	 * 
	 * @return Thinned RgbImage
	 */
	public RgbImage thinningHilditch(boolean inputBoolean[][], int width,
			int height, int noOfLayers) {
		int count = 1;
		boolean temp[][] = new boolean[height - 4][width - 4];

		while (count < noOfLayers) {
			count++;
			applyhilditchAlgorithm(inputBoolean, width, height, temp);

			for (int i = 2, i1 = 0; i1 < height - 4; i++, i1++)
				for (int j = 2, j1 = 0; j1 < width - 4; j++, j1++) {
					inputBoolean[i][j] = temp[i1][j1];
				}
		}
		checkAndCorrect(temp);
		return Threshold.makeImage(findBounds(temp, temp[0].length, temp.length));
	}

	private void applyhilditchAlgorithm(boolean[][] inputBoolean, int width,
			int height, boolean[][] temp) {
		int a,b;
		for (int i = 2, i1 = 0; i1 < height - 4; i++, i1++) {
			for (int j = 2, j1 = 0; j1 < width - 4; j++, j1++) {
				if (inputBoolean[i][j] == true) {
					temp[i1][j1] = true;
					a = calculateA(inputBoolean, i, j);
					b = calculateB(inputBoolean, i, j);

					if(!hilditchConditionOne(b))continue;

					if(!hilditchConditionTwo(a))continue;

					if(!hilditchConditionThree(inputBoolean,i, j))continue;

					if(!hilditchConditionFour(inputBoolean,i, j))continue; 
					
					temp[i1][j1] = false;
				}
			}
		}
	}

	private boolean hilditchConditionFour(boolean[][] inputBoolean, int i, int j) {
		/*
		 * Condition 4 : p2.p4.p6 = 0 or A(p4)!=1 This condition
		 * ensures that 2-pixel wide horizontal lines do not get
		 * completely eroded by the algorithm.
		 */
		if (val(inputBoolean[i - 1][j])
				* val(inputBoolean[i][j + 1])
				* val(inputBoolean[i + 1][j]) == 0
				|| calculateA(inputBoolean, i, j + 1) != 1)
			return true;
		else
			return false;
	}

	private boolean hilditchConditionThree(boolean[][] inputBoolean, int i,	int j) {
		/*
		 * Condition 3 : p2.p4.p8 = 0 or A(p2)!=1 This condition ensures that
		 * 2-pixel wide vertical lines do not get completely eroded by the
		 * algorithm
		 */
		if (val(inputBoolean[i - 1][j]) * val(inputBoolean[i][j + 1])
				* val(inputBoolean[i][j - 1]) == 0
				|| calculateA(inputBoolean, i - 1, j) != 1)
			return true;
		else
			return false;
	}

	private boolean hilditchConditionTwo(int a) {
		/*
		 * Condition 2: A(p1)=1 This is a connectivity test.
		 */
		if (a <= 1)
			return true;
		else
			return false;
	}

	private boolean hilditchConditionOne(int b) {
		/*
		 * Condition 1: 2 < = B(p1) < = 6 The first condition ensures that no
		 * end-point pixel and no isolated one be deleted (any pixel with 1
		 * black neighbour is an end-point pixel), The second condition ensures
		 * that the pixel is a boundary pixel.
		 */
		if (b >= 2 && b <= 6)
			return true;
		else
			return false;
	}

	private void checkAndCorrect(boolean thinnedBoolean[][]) {
		int checkSecondRow = 0, checkFirstRow = 0, checkSecondLastRow = 0;
		int width = thinnedBoolean[0].length;
		int height = thinnedBoolean.length;

		for (int i = 0; i < width; i++) {
			checkFirstRow += val(thinnedBoolean[1][i]);
			checkSecondLastRow += val(thinnedBoolean[height - 2][i]);
			checkSecondRow += val(thinnedBoolean[2][i]);
		}
		if (checkSecondRow == 0) {
			for (int i = 0; i < width; i++) {
				thinnedBoolean[0][i] = false;
			}
		}
		if (checkFirstRow == 0) {
			for (int i = 0; i < width; i++) {
				thinnedBoolean[0][i] = false;
				thinnedBoolean[1][i] = false;
			}
		}
		if (checkSecondLastRow == 0) {
			for (int i = 0; i < width; i++) {
				thinnedBoolean[height - 1][i] = false;
			}
		}
	}

	/**
	 * Method that Calculates B(P1) of Hilditch's 4 proposed conditions to
	 * change a black pixel to white
	 * 
	 * B(p1) = number of non-zero neighbours of p1
	 * 
	 * p9 p2 p3 p8 p1 p4 p7 p6 p5
	 * 
	 * @param inputBoolean
	 *            Original image's input boolean representation
	 * @param i
	 *            vertical position of the pixel under consideration
	 * @param j
	 *            horizontal position of the pixel under consideration
	 * 
	 * @return Whether the condition is satisfied
	 */
	private int calculateB(boolean inputBoolean[][], int i, int j) {
		int count = 0;
		if (inputBoolean[i + 1][j - 1] == false)
			count++;
		if (inputBoolean[i + 1][j] == false)
			count++;
		if (inputBoolean[i + 1][j + 1] == false)
			count++;
		if (inputBoolean[i][j + 1] == false)
			count++;
		if (inputBoolean[i][j - 1] == false)
			count++;
		if (inputBoolean[i - 1][j + 1] == false)
			count++;
		if (inputBoolean[i - 1][j] == false)
			count++;
		return count;
	}

	/**
	 * Method that Calculates A(P1) of Hilditch's 4 proposed conditions to
	 * change a black pixel to white
	 * 
	 * A(p1) = number of 0,1 patterns in the sequence p2,p3,p4,p5,p6,p7,p8,p9,p2
	 * 
	 * p9 p2 p3 p8 p1 p4 p7 p6 p5
	 * 
	 * @param inputBoolean
	 *            Original image's input boolean representation
	 * @param i
	 *            vertical position of the pixel under consideration
	 * @param j
	 *            horizontal position of the pixel under consideration
	 * 
	 * @return Whether the condition is satisfied
	 */
	private int calculateA(boolean inputBoolean[][], int i, int j) {
		int count = 0;
		// if(i-1<0 || i+1>=h || j-1<0 || j+1>=w) return 1;
		if (inputBoolean[i - 1][j] == false
				&& inputBoolean[i - 1][j + 1] == true)
			count++; // p2 p3
		if (inputBoolean[i - 1][j + 1] == false
				&& inputBoolean[i][j + 1] == true)
			count++; // p3 p4
		if (inputBoolean[i][j + 1] == false
				&& inputBoolean[i + 1][j + 1] == true)
			count++; // p4 p5
		if (inputBoolean[i + 1][j + 1] == false
				&& inputBoolean[i + 1][j] == true)
			count++; // p5 p6
		if (inputBoolean[i + 1][j] == false
				&& inputBoolean[i + 1][j - 1] == true)
			count++; // p6 p7
		if (inputBoolean[i + 1][j - 1] == false
				&& inputBoolean[i][j - 1] == true)
			count++; // p7 p8
		if (inputBoolean[i][j - 1] == false
				&& inputBoolean[i - 1][j - 1] == true)
			count++; // p8 p9
		if (inputBoolean[i - 1][j - 1] == false
				&& inputBoolean[i - 1][j] == true)
			count++; // p9 p2
		return count;
	}

	public int val(boolean input) {
		return (input == true) ? 1 : 0;
	}

}