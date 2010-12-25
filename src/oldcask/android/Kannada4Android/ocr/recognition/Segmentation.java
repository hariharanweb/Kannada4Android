package oldcask.android.Kannada4Android.ocr.recognition;

import oldcask.android.Kannada4Android.ocr.imageLibrary.HistogramAnalysis;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Threshold;
import jjil.algorithm.RgbCrop;
import jjil.core.Error;
import jjil.core.RgbImage;
import android.util.Log;

public class Segmentation {
	private static final double VERTICAL_STRENGTH_THRESHOLD = 0.05;
	private static final int MAX_CHARACTERS = 20;
	private static final String TAG_SEGMENTATION = "Segmentation";
	private RgbImage inputImage;

	int horizontalStrength[];
	int verticalStrength[];
	
	boolean inputBoolean[][];

	int height, width;

	static int pcount = 1;

	/**
	 * 
	 * @param inputImage
	 *            The input RgbImage on which the operations will be done
	 * @param inputBoolean
	 *            Boolean representation of the thresholded version of the input
	 *            image
	 */
	public Segmentation(RgbImage inputImage, boolean inputBoolean[][]) {
		this.inputImage = inputImage;
		this.inputBoolean = inputBoolean;
		height = inputImage.getHeight();
		width = inputImage.getWidth();
		horizontalStrength = new int[height];
		verticalStrength = new int[width];
		for (int i = 0; i < height; i++) {
			horizontalStrength[i] = HistogramAnalysis.getStrengthH(inputBoolean, i, 0,width);
		}
		for (int j = 0; j < width; j++) {
			verticalStrength[j] = HistogramAnalysis.getStrengthV(inputBoolean, 0,j, height);
		}
	}

	public void segment(BIQueue PicQueue) {
		int Lines[] = new int[width];
		int from[] = new int[MAX_CHARACTERS];
		int to[] = new int[MAX_CHARACTERS];
		int lineWidth[] = new int[MAX_CHARACTERS];
		int count = 0, index = 0;
		Lines[++count] = 0;

		for (int j = 0; j < width; j++) {
			if (verticalStrength[j] < VERTICAL_STRENGTH_THRESHOLD * inputBoolean.length)
				Lines[count++] = j;
		}
		Lines[count] = width;

		for (int j = 0; j < count; j++) {
			if (Lines[j + 1] - Lines[j] != 1) {
				from[index] = Lines[j];
				to[index] = Lines[j + 1];
				lineWidth[index] = to[index] - from[index];
				System.out.println("Printing Hsplit " + from[index] + " "
						+ to[index] + " " + lineWidth[index] + " " + index);

				try {
					RgbImage segment = inputImage;
					RgbCrop croppedImage = new RgbCrop(from[index], 0, lineWidth[index],
							height);
					croppedImage.push(inputImage);
					if (!croppedImage.isEmpty())
						segment = (RgbImage) croppedImage.getFront();

					if (lineWidth[index] < 4 || Threshold.threshold(segment) > 0.75f)
						continue;
					boolean subArray[][] = getSubArray(inputBoolean,from[index], to[index], 0, height);
					PicQueue.insert(segment, subArray);
					index++;
				} catch (Error e) {
					System.out.println(" Error in height in Rgbcrop in Segmentation");
					Log.e(TAG_SEGMENTATION,
							"Error in height in Rgbcrop in Segmentation");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Two Dimensional version of System.arraycopy() Copies a particular part of
	 * the two dimensional boolean array into another
	 * 
	 * @param t
	 *            The source array, of the form t[y][x]
	 * @param x1
	 *            The starting x position.
	 * @param x2
	 *            The ending y position
	 * @param y1
	 *            The starting y position
	 * @param y2
	 *            The ending y position
	 * @return The copied array
	 */
	public boolean[][] getSubArray(boolean t[][], int x1, int x2, int y1, int y2) {
		boolean temp[][] = new boolean[y2 - y1][x2 - x1];
		for (int i = y1, a = 0; i < y2; i++, a++) {
			for (int j = x1, b = 0; j < x2; j++, b++) {
				temp[a][b] = t[i][j];
			}
		}
		return temp;
	}

	/**
	 * Finds a split point on the image. For Images having 2 lines/Some random
	 * stuff with the text.
	 * 
	 * @return the SplitPoint.
	 */
	public int shouldSplit() {
		if (horizontalStrength[height / 2] < 5)
			return height / 2;

		boolean Split = false;
		int j = height / 2;
		while (!Split && j > height / 3) {
			j--;
			if (horizontalStrength[j] < 5)
				Split = true;
		}
		if (Split == true)
			return j;

		Split = false;
		j = height / 2;
		while (!Split && j <= 2 * height / 3) {
			j++;
			if (horizontalStrength[j] < 5)
				Split = true;
		}
		if (Split == true)
			return j;

		return (height - 1);
	}

}
