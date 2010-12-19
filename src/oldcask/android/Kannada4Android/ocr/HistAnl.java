package oldcask.android.Kannada4Android.ocr;

/**
 * A very basic class to aid in the ananlysis the histogram of a thresholded
 * picture represented as a 2D boolean array
 * 
 * HistAnl stands for Histogram Analysis
 * 
 * @author Source Code
 * @version 1.0
 * 
 */
public class HistAnl {

	/**
	 * Gets the strength of a vertical line of the image
	 * 
	 * @param t
	 *            The Boolean representation of the image
	 * 
	 * @param x
	 *            The initial x position
	 * 
	 * @param y
	 *            The intial y position
	 * 
	 * @param hi
	 *            The height of the line considered
	 * 
	 * @return The strength of the line
	 */
	public static int getStrengthV(boolean t[][], int x, int y, int hi) {
		int str = 0;
		for (int k = 0; k < hi; k++) {
			if (t[x + k][y] == true)
				str++;
		}
		return str;
	}

	/**
	 * Gets the strength of a horizontal line of the image
	 * 
	 * @param t
	 *            The Boolean representation of the image
	 * 
	 * @param x
	 *            The inital x position
	 * 
	 * @param y
	 *            The initial y position
	 * 
	 * @param wi
	 *            The width of the line considered
	 * 
	 * @return The strength of the line
	 */
	public static int getStrengthH(boolean t[][], int x, int y, int wi) {
		int str = 0, k;
		for (k = 0; k < wi; k++) {
			if (t[x][y + k] == true)
				str++;
		}
		return str;
	}
}
