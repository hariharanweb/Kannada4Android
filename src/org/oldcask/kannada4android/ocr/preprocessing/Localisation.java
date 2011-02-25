package org.oldcask.kannada4android.ocr.preprocessing;

import org.oldcask.kannada4android.ocr.imagelibrary.HistogramAnalysis;
import org.oldcask.kannada4android.ocr.imagelibrary.Parameters;
import org.oldcask.kannada4android.ocr.imagelibrary.Threshold;

import jjil.algorithm.RgbCrop;
import jjil.core.Error;
import jjil.core.RgbImage;
import android.util.Log;

public class Localisation {
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
			horizontalStrength[i] = HistogramAnalysis.getHorizontalStrength(inputBoolean, i, 0,
					width);
		}
		for (int i = 0; i < width; i++)
			verticalStrength[i] = HistogramAnalysis.getVerticalStrength(
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
			int leftEnd = findLeftEnd();
			int rightEnd = findRightEnd();

			RgbCrop croppedImage = new RgbCrop(leftEnd, 0,(rightEnd - leftEnd + 1), height);
			croppedImage.push(inputImage);
			if (!croppedImage.isEmpty())
				inputImage = (RgbImage) croppedImage.getFront();
			
		} catch (Exception e) {
			Log.e(Parameters.TAG_LOCALISATION, "Error in Width Localisation " + e);
			e.printStackTrace();
		} catch (Error e) {
			Log.e(Parameters.TAG_LOCALISATION, "Error in Pipeline of RgbCrop in Localisation By width " + e);
			e.printStackTrace();
		}

		refreshData(inputImage, Threshold.thresholdIterative(inputImage));
		
		return inputImage;
	}

	private int findRightEnd() {
		int rightEnd = width-1;
		int j = width - 1;
		boolean found = false;
		while (!found && j >= 0) {
			rightEnd = j--;
			if (verticalStrength[rightEnd] >= Parameters.WIDTH_MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			rightEnd = width - 1;
		return rightEnd;
	}

	private int findLeftEnd() {
		int leftEnd = 0;
		int j = 0;
		boolean found = false;
		while (!found && j < width) {
			leftEnd = j++;
			if (verticalStrength[leftEnd] >= Parameters.WIDTH_MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			leftEnd = 0;
		return leftEnd;
	}

	private void refreshData(RgbImage inputImage, boolean inputBoolean[][]) {
		this.inputImage = inputImage;
		this.inputBoolean = inputBoolean;
		height = inputImage.getHeight();
		width = inputImage.getWidth();
		for (int i = 0; i < height; i++) {
			horizontalStrength[i] = HistogramAnalysis.getHorizontalStrength(inputBoolean, i, 0, width);
		}
		for (int i = 0; i < width; i++)
			verticalStrength[i] = HistogramAnalysis.getVerticalStrength(inputBoolean, 0, i, height);
	}

	/**
	 * The localiseImageByHeight further localises the Image
	 * Vertical dimensions may be changed
	 * @return the localised image
	 */

	public RgbImage localiseImageByHeight() {
		try {
			int topEnd = findTopEnd();
			int bottomEnd = findBottomEnd();

			RgbCrop croppedImage = new RgbCrop(0, topEnd, width, (bottomEnd	- topEnd + 1));
			croppedImage.push(inputImage);
			if (!croppedImage.isEmpty())
				inputImage = (RgbImage) croppedImage.getFront();
			
		} catch (Exception e) {
			Log.e(Parameters.TAG_LOCALISATION, "Error in Height Localisation" + e);
			e.printStackTrace();
		} catch (Error e) {
			Log.e(Parameters.TAG_LOCALISATION, "Error in Pipeline of RgbCrop in Localisation By Height " + e);
			e.printStackTrace();
		}
		
		refreshData(inputImage, Threshold.thresholdIterative(inputImage));
		
		return inputImage;
	}

	private int findBottomEnd() {
		int bottomEnd = height-1;
		int j =  height/2;
		boolean found = false;
		while (!found & j < height) {
			bottomEnd = j++;
			if (horizontalStrength[bottomEnd] <= Parameters.HEIGHT_MIN_NUMBER_OF_PIXELS_THRESHOLD)
				found = true;
		}
		if (found == false)
			bottomEnd = height - 1;
		return bottomEnd;
	}

	private int findTopEnd() {
		int topEnd = 0;
		boolean found = false;
		int j = height/2;
		while (!found & j > 0) {
			topEnd = j--;
			if (horizontalStrength[topEnd] <= Parameters.HEIGHT_MIN_NUMBER_OF_PIXELS_THRESHOLD)
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
		for (int i = 0; i < inputBoolean.length/* h */; i++) {
			for (int j = 0; j < inputBoolean[0].length/* w */; j++) {
				if (inputBoolean[i][j] == true)
					System.out.print("@");
				else
					System.out.print(" ");
			}
			System.out.println("|" + (i));
		}
	}
}
