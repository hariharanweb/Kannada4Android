package oldcask.android.Kannada4Android.ocr;

import jjil.core.RgbImage;


public class DownSample {

	static final int HGT = OpticalCharacterRecognizer.DOWNSAMPLE_HEIGHT;

	static final int WDT = OpticalCharacterRecognizer.DOWNSAMPLE_WIDTH;
	

	/**
	 * An array of 5x7 booleans. Each 2D array represents the downsampled
	 * version of the characters on the Number Plate
	 */
	boolean DS[][];

	/**
	 * Empty constructor of the class
	 * 
	 */
	public DownSample() {
		DS = new boolean[HGT][WDT];
	}

	/**
	 * Method that actually downsamples the input image
	 * 
	 * @param inp
	 *            The input Segment of the Number Plate
	 * 
	 * @param t
	 *            The boolean representation of the thresholded segemnt
	 * 
	 */
	public void DoDownSample(RgbImage inp, boolean t[][]) {
		double ratioY = (double) t.length / HGT;
		double ratioX = (double) t[0].length / WDT;

		// System.out.println(t.length+" "+t[0].length+" "+ratioY+" "+ratioX+"
		// "+(int)HGT+" "+(int)WDT);

		for (int i = 0; i < (int) HGT; i++) {
			System.out.print(i + "  ");
			for (int j = 0; j < (int) WDT; j++) {
				DS[i][j] = isBlack(i, j, ratioY, ratioX, t);
				if (DS[i][j] == true)
					System.out.print("#");
				else
					System.out.print(" ");
			}
			System.out.println();
		}
	}

	/**
	 * 
	 * Method determines whether a box can be downsampled to a black dot
	 * 
	 * @param x
	 *            specifies the box's starting x position
	 * @param y
	 *            specifies the box's starting y position
	 * @param hi
	 *            specifies the box's height
	 * @param wi
	 *            specifies the box's width
	 * @param t
	 *            boolean representation of the thresholded original image
	 * @return returns true if the box can be reduced to a blaack dot. Else
	 *         returns false
	 */
	private boolean isBlack(int x, int y, double hi, double wi, boolean t[][]) {
		int str = 0;
		for (int j = 0; j <= Math.ceil(wi); j++)
			if ((j + y * wi) < t[0].length)
				str += HistAnl.getStrengthV(t, ((int) Math.floor(x * hi)),
						((int) Math.floor(j + y * wi)), ((int) Math.ceil(hi)));
		if (str >= 2)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @return Downsampled boolean representation
	 */
	public boolean[][] getDownSampled() {
		return DS;
	}

}