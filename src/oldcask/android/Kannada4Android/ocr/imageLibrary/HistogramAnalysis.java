package oldcask.android.Kannada4Android.ocr.imageLibrary;

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
	 *            The initial y position
	 * 
	 * @param heightOfLine
	 *            The height of the line considered
	 * 
	 * @return The strength of the line
	 */
	public static int getVerticalStrength(boolean inputBoolean[][], int initial_x, int initial_y, int heightOfLine) {
		int strength = 0;
		for (int k = 0; k < heightOfLine; k++) {
			if (inputBoolean[initial_x + k][initial_y] == true)
				strength++;
		}
		return strength;
	}

	/**
	 * Gets the strength of a horizontal line of the image
	 * 
	 * @param inputBoolean
	 *            The Boolean representation of the image
	 * 
	 * @param initial_x
	 *            The initial x position
	 * 
	 * @param initial_y
	 *            The initial y position
	 * 
	 * @param widthOfLine
	 *            The width of the line considered
	 * 
	 * @return The strength of the line
	 */
	public static int getHorizontalStrength(boolean inputBoolean[][], int initial_x, int initial_y, int widthOfLine) {
		int strength = 0;
		for (int k = 0; k < widthOfLine; k++) {
			if (inputBoolean[initial_x][initial_y + k] == true)
				strength++;
		}
		return strength;
	}
}
