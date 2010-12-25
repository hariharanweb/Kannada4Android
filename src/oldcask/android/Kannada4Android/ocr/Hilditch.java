package oldcask.android.Kannada4Android.ocr;

import jjil.core.RgbImage;


public class Hilditch {

	/**
	 * Empty constructor for the Hilditch Class
	 * 
	 */
	Hilditch() {

	}

	/**
	 * 
	 * Adjust method pads the source boolean array with 2 rows of 'false' values
	 * 
	 * @param t1
	 *            The source array
	 * @param t
	 *            The bigger resulting array
	 * @param h
	 *            Height of the array (t1.length)
	 * @param w
	 *            Width of the array (t1[0].length)
	 * 
	 * @return The padded array
	 */
	boolean[][] adjust(boolean t1[][], boolean t[][], int h, int w) {

		for (int i = 0; i < w + 4; i++) {
			t[0][i] = false;
			t[h + 1][i] = false;
		}

		for (int j = 0; j < h + 4; j++) {
			t[j][0] = false;
			t[j][w + 1] = false;
		}

		for (int k = 0, ki = 2; k < h; k++, ki++)
			for (int l = 0, li = 2; l < w; l++, li++)
				t[ki][li] = t1[k][l];
		return t;
	}

	/**
	 * After Thinning the dimensions of the image may have varied considerably
	 * This method finds the bounds of the resulting image, and reduces the size
	 * of the image accordingly
	 * 
	 * @param t
	 *            Source image's boolean representation
	 * @param w
	 *            Width of the image t[0].length
	 * @param h
	 *            Height of the image t.length
	 * 
	 * @return Perfectly bounded image
	 */
	boolean[][] findBounds(boolean t[][], int w, int h) {
		boolean bound[][];

		int top = findUBound(t, w, h);
		int bot = findBBound(t, w, h);
		int rgt = findRBound(t, w, h);
		int lft = findLBound(t, w, h);

		if (bot - top + 1 < 0 || rgt - lft + 1 < 0)
			return null;
		else
			bound = new boolean[bot - top + 1][rgt - lft + 1];
		for (int i1 = 0, i = top; i <= bot; i++, i1++)
			for (int j1 = 0, j = lft; j <= rgt; j++, j1++)
				bound[i1][j1] = t[i][j];

		return bound;
	}

	/**
	 * Finds Upper bound of the image
	 * 
	 * @param t
	 *            Source image
	 * @param w
	 *            Width of the image t[0].length
	 * @param h
	 *            Height of the image t.length
	 * 
	 * @return Upper bound of the image
	 */
	int findUBound(boolean t[][], int w, int h) {
		int top = 0;
		while (top < h
				&& (HistogramAnalysis.getStrengthH(t, top, 0, w) == 0 || HistogramAnalysis
						.getStrengthH(t, top, 0, w) == w)) {
			top++;
		}
		return top;
	}

	/**
	 * Finds Lower bound of the image
	 * 
	 * @param t
	 *            Source image
	 * @param w
	 *            Width of the image t[0].length
	 * @param h
	 *            Height of the image t.length
	 * 
	 * @return Lower bound of the image
	 */
	int findBBound(boolean t[][], int w, int h) {
		int bot = h - 1;
		while (bot > 0
				&& (HistogramAnalysis.getStrengthH(t, bot, 0, w) == 0 || HistogramAnalysis
						.getStrengthH(t, bot, 0, w) == w)) {
			bot--;
		}
		return bot;
	}

	/**
	 * Finds Left bound of the image
	 * 
	 * @param t
	 *            Source image
	 * @param w
	 *            Width of the image t[0].length
	 * @param h
	 *            Height of the image t.length
	 * 
	 * @return Left bound of the image
	 */
	int findLBound(boolean t[][], int w, int h) {
		int lft = 0;
		while (lft < w && HistogramAnalysis.getStrengthV(t, 0, lft, h) == 0) {
			lft++;
		}
		return lft;
	}

	/**
	 * Finds Right bound of the image
	 * 
	 * @param t
	 *            Source image
	 * @param w
	 *            Width of the image t[0].length
	 * @param h
	 *            Height of the image t.length
	 * 
	 * @return Right bound of the image
	 */
	int findRBound(boolean t[][], int w, int h) {
		int rgt = w - 1;
		while (rgt > 0 && HistogramAnalysis.getStrengthV(t, 0, rgt, h) == 0) {
			rgt--;
		}
		return rgt;
	}

	/**
	 * A direct implementation of the Hilditch algorithm for thinning.
	 * Iteratively thins the image one layer at a time
	 * 
	 * @param t
	 *            Source image
	 * @param w
	 *            Width of the image t[0].length
	 * @param h
	 *            Height of the image t.length
	 * @param n
	 *            Number of layers to remove
	 * 
	 * @return Thinned BufferedImage
	 */
	RgbImage dothin(boolean t[][], int w, int h, int n) {
		int a, b;
		int c = 1, count = 1;
		boolean temp[][] = new boolean[h - 4][w - 4];

		while (count < n) {
			count++;
			for (int i = 2, i1 = 0; i1 < h - 4; i++, i1++) {
				for (int j = 2, j1 = 0; j1 < w - 4; j++, j1++) {
					c = 1;

					if (t[i][j] == true) {
						temp[i1][j1] = true;
						a = checka(t, i, j);
						b = checkb(t, i, j);
						if (b >= 2 && b <= 6)
							c *= 1;
						else
							c *= 0; // System.out.print("1 "); //Condition 1

						if (a <= 1)
							c *= 1;
						else
							c *= 0; // System.out.print("2 ");//Condition 2

						if (Threshold.val(t[i - 1][j]) * Threshold.val(t[i][j + 1])
								* Threshold.val(t[i][j - 1]) == 0
								|| checka(t, i - 1, j) != 1)
							c *= 1; // } //Condition 3
						else
							c *= 0; // System.out.print("3 ");

						if (Threshold.val(t[i - 1][j]) * Threshold.val(t[i][j + 1])
								* Threshold.val(t[i + 1][j]) == 0
								|| checka(t, i, j + 1) != 1)
							c *= 1; // } //Condition 4
						else
							c *= 0; // System.out.print("4 ");

					}
					if (c == 1) {
						temp[i1][j1] = false;
					}
				}
			}

			for (int i = 2, i1 = 0; i1 < h - 4; i++, i1++)
				for (int j = 2, j1 = 0; j1 < w - 4; j++, j1++) {
					t[i][j] = temp[i1][j1];
				}
		}
		checknCorrect(temp);
		return Threshold.makeImage(findBounds(temp, temp[0].length, temp.length));
	}

	/**
	 * Correcting some errors in the Thinning
	 * 
	 * @param temp
	 *            boolean representation of the thinned image
	 * 
	 */
	void checknCorrect(boolean temp[][]) {
		int s1 = 0, s2 = 0, sh = 0;
		for (int i = 0; i < temp[0].length; i++) {
			s2 += Threshold.val(temp[1][i]);
			sh += Threshold.val(temp[temp.length - 2][i]);
			s1 += Threshold.val(temp[2][i]);
		}
		if (s1 == 0) {
			for (int i = 0; i < temp[0].length; i++) {
				temp[0][i] = false;
			}
		}
		if (s2 == 0) {
			for (int i = 0; i < temp[0].length; i++) {
				temp[0][i] = false;
				temp[1][i] = false;
			}
		}
		if (sh == 0) {
			for (int i = 0; i < temp[0].length; i++) {
				temp[temp.length - 1][i] = false;
			}
		}
	}

	/**
	 * Method that checks the second condition of Hilditch's 4 proposed
	 * conditions to change a black pixel to white
	 * 
	 * @param t
	 *            Original image's source boolean representation
	 * @param i
	 *            vertical position of the pixel under consideration
	 * @param j
	 *            horizontal position of the pixel under consideration
	 * 
	 * @return Whether the comdition is satisfied
	 */
	int checkb(boolean t[][], int i, int j) {
		int count = 0;
		if (t[i + 1][j - 1] == false)
			count++;
		if (t[i + 1][j] == false)
			count++;
		if (t[i + 1][j + 1] == false)
			count++;
		if (t[i][j + 1] == false)
			count++;
		if (t[i][j - 1] == false)
			count++;
		if (t[i - 1][j + 1] == false)
			count++;
		if (t[i - 1][j] == false)
			count++;
		return count;
	}

	/**
	 * Method that checks the first condition of Hilditch's 4 proposed
	 * conditions to change a black pixel to white
	 * 
	 * @param t
	 *            Original image's source boolean representation
	 * @param i
	 *            vertical position of the pixel under consideration
	 * @param j
	 *            horizontal position of the pixel under consideration
	 * 
	 * @return Whether the comdition is satisfied
	 */
	int checka(boolean t[][], int i, int j) {
		int count = 0;
		// if(i-1<0 || i+1>=h || j-1<0 || j+1>=w) return 1;
		if (t[i - 1][j] == false && t[i - 1][j + 1] == true)
			count++; // p2 p3
		if (t[i - 1][j + 1] == false && t[i][j + 1] == true)
			count++; // p3 p4
		if (t[i][j + 1] == false && t[i + 1][j + 1] == true)
			count++; // p4 p5
		if (t[i + 1][j + 1] == false && t[i + 1][j] == true)
			count++; // p5 p6
		if (t[i + 1][j] == false && t[i + 1][j - 1] == true)
			count++; // p6 p7
		if (t[i + 1][j - 1] == false && t[i][j - 1] == true)
			count++; // p7 p8
		if (t[i][j - 1] == false && t[i - 1][j - 1] == true)
			count++; // p8 p9
		if (t[i - 1][j - 1] == false && t[i - 1][j] == true)
			count++; // p9 p2
		return count;
	}

}