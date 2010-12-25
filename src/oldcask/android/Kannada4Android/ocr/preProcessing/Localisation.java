package oldcask.android.Kannada4Android.ocr.preProcessing;

import java.io.File;
import java.io.PrintStream;

import oldcask.android.Kannada4Android.ocr.imageLibrary.HistogramAnalysis;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Threshold;

import android.util.Log;

import jjil.algorithm.RgbCrop;
import jjil.core.Error;
import jjil.core.RgbImage;

public class Localisation {
	private static final int MIN_NUMBER_OF_PIXELS_THRESHOLD = 5;
	int horizontalStrength[];
	int verticalStrength[];
	RgbImage inputImage;
	boolean[][] inputBoolean;
	int height, width;

	/**
	 * 
	 * @param inputImage
	 *            The input RgbImage on which the operations will be done
	 * @param inputBoolean
	 *            Boolean representation of the thresholded version of the input
	 *            image
	 */
	public Localisation(RgbImage inputImage, boolean inputBoolean[][]) {
		this.inputImage = inputImage;
		this.inputBoolean = inputBoolean;
		height = inputImage.getHeight();
		width = inputImage.getWidth();
		horizontalStrength = new int[height];
		verticalStrength = new int[width];
		for (int i = 0; i < height; i++) {
			horizontalStrength[i] = HistogramAnalysis.getStrengthH(inputBoolean, i, 0,
					width);
		}
		for (int i = 0; i < width; i++)
			verticalStrength[i] = HistogramAnalysis.getStrengthV(
					inputBoolean, 0, i, height);
	}

	/**
	 * Localisation of the Image
	 * 
	 * @return A better localization of the Image. The returned image is reduced
	 *         in Width
	 */

	public RgbImage localiseImageByWidth() {

		try {
			int leftEnd = 0, j = 1, rightEnd = width - 1;

			leftEnd = findLeftEnd(leftEnd, j);

			rightEnd = findRightEnd(rightEnd);

			System.out.print("\n LEFT END" + leftEnd + " RIGHTEND" + rightEnd
					+ "HEIGHT" + inputImage.getWidth());

			RgbCrop croppedImage = new RgbCrop(leftEnd, 0,
					(rightEnd - leftEnd), height - 1);
			croppedImage.push(inputImage);
			if (!croppedImage.isEmpty())
				inputImage = (RgbImage) croppedImage.getFront();
		} catch (Exception e) {
			Log.e("Localisation", "Error in Localisation " + e);
			System.out.print(e);
		} catch (Error e) {
			Log.e("Localisation", "Error in Pipeline of RgbCrop " + e);
			e.printStackTrace();
		}

		refreshData(inputImage, Threshold.threshold(inputImage, 0.75f, 0.15f));
		return inputImage;
	}

	private int findRightEnd(int rightEnd) {
		int j;
		boolean found = false;
		j = width - 1;
		while (!found && j >= 0) {
			rightEnd = j--;
			if (verticalStrength[rightEnd] >= MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			rightEnd = width - 1;
		return rightEnd;
	}

	private int findLeftEnd(int leftEnd, int j) {
		boolean found = false;
		while (!found && j < width) {
			leftEnd = j++;
			if (verticalStrength[leftEnd] >= MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			leftEnd = 0;
		return leftEnd;
	}

	/**
	 * Once the localiseImageByWidth method is completed, the Image may be
	 * altered The refresh method refreshes the constructor initiated values It
	 * alters the parameters width,height & Strength
	 * 
	 * @param t
	 *            the boolean representation of the thresholded version of the
	 *            image
	 */
	private void refreshData(RgbImage inputImage, boolean t[][]) {
		height = inputImage.getHeight();
		width = inputImage.getWidth();
		for (int i = 0; i < height; i++) {
			horizontalStrength[i] = HistogramAnalysis.getStrengthH(t, i, 0, width);
		}
		for (int i = 0; i < width; i++)
			verticalStrength[i] = HistogramAnalysis.getStrengthV(t, 0, i, height);
	}

	/**
	 * The localiseImageByHeight further localises the Image on the candidate
	 * Vertical dimensions may be changed
	 * 
	 * @param imageToBeLocalised
	 *            the input to be localised
	 * @param inputBoolean
	 *            the boolean representation thresholded input image
	 * @return the localised image
	 */

	public RgbImage localiseImageByHeight(RgbImage imageToBeLocalised,
			boolean inputBoolean[][]) {
		int topEnd = 0, j = 1, bottomEnd = imageToBeLocalised.getHeight() - 1;
		refreshData(imageToBeLocalised, inputBoolean);
		try {
			topEnd = findTopEnd(topEnd, j);

			bottomEnd = findBottomEnd(bottomEnd);

			System.out.print("\n TOPEND" + topEnd + " BOTTOMEND" + bottomEnd
					+ "HEIGHT" + imageToBeLocalised.getHeight() + "Width = "
					+ imageToBeLocalised.getWidth());

			RgbCrop croppedImage = new RgbCrop(0, topEnd, width - 1, (bottomEnd	- topEnd));
			croppedImage.push(imageToBeLocalised);
			if (!croppedImage.isEmpty())
				imageToBeLocalised = (RgbImage) croppedImage.getFront();
		} catch (Exception e) {
			Log.e("Localisation", "Error in Localisation" + e);
			System.out.print(e);
		} catch (Error e) {
			Log.e("Localisation", "Error in Pipeline of RgbCrop " + e);
			e.printStackTrace();
		}
		// Print(Threshold.threshold(imageToBeLocalised, 0.75f, 0.15f));
		refreshData(inputImage, Threshold.threshold(inputImage, 0.75f, 0.15f));
		return imageToBeLocalised;
	}

	private int findBottomEnd(int bottomEnd) {
		int j;
		boolean found;
		found = false;
		j = height - 1;
		while (!found & j >= (3 * height / 4)) {
			bottomEnd = j--;
			if (horizontalStrength[bottomEnd] <= MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			bottomEnd = height - 1;
		return bottomEnd;
	}

	private int findTopEnd(int topEnd, int j) {
		boolean found = false;
		while (!found & j < height) {
			topEnd = j++;
			if (horizontalStrength[topEnd] >= MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			topEnd = 0;
		return topEnd;
	}

	/**
	 * Prints the image on the console
	 * 
	 * @param inputBoolean
	 *            the boolean representation of the image to be printed
	 */
	public static void Print(boolean inputBoolean[][]) {
		File f = new File("data/fullimage.txt");
		try {
			PrintStream ps = new PrintStream(f);

			for (int i = 0; i < inputBoolean.length/* h */; i++) {
				for (int j = 0; j < inputBoolean[0].length/* w */; j++) {
					if (inputBoolean[i][j] == true)
						ps.print("@");
					else
						ps.print(" ");
				}
				ps.println("|" + (i));
			}
			ps.close();

			for (int i = 0; i < inputBoolean.length/* h */; i++) {
				for (int j = 0; j < inputBoolean[0].length/* w */; j++) {
					if (inputBoolean[i][j] == true)
						System.out.print("@");
					else
						System.out.print(" ");
				}
				System.out.println("|" + (i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
