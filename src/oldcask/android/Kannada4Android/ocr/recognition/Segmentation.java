package oldcask.android.Kannada4Android.ocr.recognition;

import oldcask.android.Kannada4Android.ocr.imageLibrary.HistogramAnalysis;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Parameters;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Threshold;
import jjil.algorithm.RgbCrop;
import jjil.core.Error;
import jjil.core.RgbImage;
import android.util.Log;

public class Segmentation {
	private RgbImage inputImage;
	boolean inputBoolean[][];

	int horizontalStrength[];
	int verticalStrength[];
	int height, width;

	public Segmentation(RgbImage inputImage, boolean inputBoolean[][]) {
		this.inputImage = inputImage;
		this.inputBoolean = inputBoolean;
		height = inputImage.getHeight();
		width = inputImage.getWidth();
		horizontalStrength = new int[height];
		verticalStrength = new int[width];
		for (int i = 0; i < height; i++) {
			horizontalStrength[i] = HistogramAnalysis.getHorizontalStrength(inputBoolean, i, 0,width);
		}
		for (int j = 0; j < width; j++) {
			verticalStrength[j] = HistogramAnalysis.getVerticalStrength(inputBoolean, 0,j, height);
		}
	}

	public void segment(SegmentedImageProcessor segmentedImageProcessor) {
		int Lines[] = new int[width];
		int from[] = new int[Parameters.MAX_CHARACTERS_RECOGNISABLE];
		int to[] = new int[Parameters.MAX_CHARACTERS_RECOGNISABLE];
		int lineWidth[] = new int[Parameters.MAX_CHARACTERS_RECOGNISABLE];
		int count = 0, index = 0;
		Lines[++count] = 0;

		for (int j = 0; j < width; j++) {
			if (verticalStrength[j] < Parameters.MIN_PIXEL_REQUIRED_FOR_A_LETTER_TO_EXIST)
				Lines[count++] = j;
		}
		Lines[count] = width;

		for (int j = 0; j < count; j++) {
			if (Lines[j + 1] - Lines[j] != 1) {
				from[index] = Lines[j];
				to[index] = Lines[j + 1];
				lineWidth[index] = to[index] - from[index];

				try {
					if(lineWidth[index] < Parameters.MIN_CHARACTER_INTER_SPACING)
						continue;
					
					RgbImage segment = inputImage;
					RgbCrop croppedImage = new RgbCrop(from[index], 0, lineWidth[index],
							height);
					croppedImage.push(inputImage);
					if (!croppedImage.isEmpty())
						segment = (RgbImage) croppedImage.getFront();
					 
					if (Threshold.threshold(segment) > Parameters.MAX_SEGMENT_THRESHOLD)
						continue;
					
					boolean subArray[][] = getSubArray(inputBoolean,from[index], to[index], 0, height);
					segmentedImageProcessor.process(segment, subArray);
					index++;
				} catch (Error e) {
					Log.e(Parameters.TAG_SEGMENTATION,"Error in height in Rgbcrop in Segmentation");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Two Dimensional version of System.arraycopy() Copies a particular part of
	 * the two dimensional boolean array into another
	 * 
	 * @param inputBoolean
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
	public boolean[][] getSubArray(boolean inputBoolean[][], int x1, int x2, int y1, int y2) {
		boolean temp[][] = new boolean[y2 - y1][x2 - x1];
		for (int i = y1, a = 0; i < y2; i++, a++) {
			for (int j = x1, b = 0; j < x2; j++, b++) {
				temp[a][b] = inputBoolean[i][j];
			}
		}
		return temp;
	}
}
