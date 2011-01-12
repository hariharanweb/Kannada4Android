package org.oldcask.kannada4android.ocr.recognition;

import org.oldcask.kannada4android.ocr.imagelibrary.HistogramAnalysis;
import org.oldcask.kannada4android.ocr.imagelibrary.Parameters;

import jjil.core.RgbImage;


public class DownSample {

	private boolean downSample[][];

	public DownSample() {
		downSample = new boolean[Parameters.DOWNSAMPLE_HEIGHT][Parameters.DOWNSAMPLE_WIDTH];
	}

	/**
	 * Method that actually downsamples the input image
	 * 
	 * @param inputSegment
	 *            The input Segment of the Image
	 * 
	 * @param inputBoolean
	 *            The boolean representation of the thresholded segemnt
	 * 
	 */
	public void downSample(RgbImage inputSegment, boolean inputBoolean[][]) {
		double ratioY = (double) inputBoolean.length / Parameters.DOWNSAMPLE_HEIGHT;
		double ratioX = (double) inputBoolean[0].length / Parameters.DOWNSAMPLE_WIDTH;

		for (int i = 0; i < (int) Parameters.DOWNSAMPLE_HEIGHT; i++) {
			System.out.print(i + "  ");
			for (int j = 0; j < (int) Parameters.DOWNSAMPLE_WIDTH; j++) {
				downSample[i][j] = isBlack(i, j, ratioY, ratioX, inputBoolean);
				if (downSample[i][j] == true)
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
	 * @param box_x
	 *            specifies the box's starting x position
	 * @param box_y
	 *            specifies the box's starting y position
	 * @param box_height
	 *            specifies the box's height
	 * @param box_width
	 *            specifies the box's width
	 * @param inputBoolean
	 *            boolean representation of the thresholded original image
	 * @return returns true if the box can be reduced to a black dot. Else
	 *         returns false
	 */
	private boolean isBlack(int box_x, int box_y, double box_height, double box_width, boolean inputBoolean[][]) {
		int str = 0;
		for (int j = 0; j <= Math.ceil(box_width); j++)
			if ((j + box_y * box_width) < inputBoolean[0].length)
				str += HistogramAnalysis.getVerticalStrength(inputBoolean, ((int) Math.floor(box_x * box_height)),
						((int) Math.floor(j + box_y * box_width)), ((int) Math.ceil(box_height)));
		if (str >= Parameters.DOWNSAMPLE_BOX_STRENGTH_NEEDED)
			return true;
		else
			return false;
	}

	public boolean[][] getDownSampled() {
		return downSample;
	}

}