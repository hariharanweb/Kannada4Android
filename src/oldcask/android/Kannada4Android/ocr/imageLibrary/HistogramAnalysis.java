package oldcask.android.Kannada4Android.ocr.imageLibrary;

/**
 * A very basic class to aid in the ananlysis the histogram of a thresholded
 * picture represented as a 2D boolean array
 */
public class HistogramAnalysis {

	/**
	 * Gets the strength of a vertical line of the image
	 * 
	 * @param inputBoolean
	 *            The Boolean representation of the image
	 * 
	 * @param initial_x
	 *            The initial x position
	 * 
	 * @param initial_y
	 *            The intial y position
	 * 
	 * @param heightOfLine
	 *            The height of the line considered
	 * 
	 * @return The strength of the line
	 */
	public static int getStrengthV(boolean inputBoolean[][], int initial_x, int initial_y, int heightOfLine) {
		int str = 0;
		for (int k = 0; k < heightOfLine; k++) {
			if (inputBoolean[initial_x + k][initial_y] == true)
				str++;
		}
		return str;
	}

	/**
	 * Gets the strength of a horizontal line of the image
	 * 
	 * @param inputBoolean
	 *            The Boolean representation of the image
	 * 
	 * @param initial_x
	 *            The inital x position
	 * 
	 * @param initial_y
	 *            The initial y position
	 * 
	 * @param widthOfLine
	 *            The width of the line considered
	 * 
	 * @return The strength of the line
	 */
	public static int getStrengthH(boolean inputBoolean[][], int initial_x, int initial_y, int widthOfLine) {
		int str = 0, k;
		for (k = 0; k < widthOfLine; k++) {
			if (inputBoolean[initial_x][initial_y + k] == true)
				str++;
		}
		return str;
	}
}
