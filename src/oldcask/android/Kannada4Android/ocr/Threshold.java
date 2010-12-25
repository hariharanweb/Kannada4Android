package oldcask.android.Kannada4Android.ocr;

import jjil.core.RgbImage;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * 
 * This class is meant to support the various interconversions
 * 
 * <br>
 * JPEG -> BufferedImage <br>
 * BufferedImage -> JPEG <br>
 * BufferedImage -> boolean[][] <br>
 * boolean[][] -> BufferedImage.TYPE_BYTE_BINARY
 * 
 * @author Source Code
 * @version 1.0
 * 
 */
public class Threshold {

	private static final String TAG_THRESHOLD = "Threshold";
	private static final int MAX_THRESHOLD_COUNT = 5;
	private static int MYTHRESHOLD = 136;

	/**
	 * Makes an RgbImage from the Given Boolean
	 * 
	 * @Param booleanRepresentationOfImage The boolean representation of the
	 * image
	 * 
	 * @Return The RgbImage
	 */
	public static RgbImage makeImage(boolean booleanRepresentationOfImage[][]) {
		if (booleanRepresentationOfImage == null) {
			Log.e(TAG_THRESHOLD,"Make Image Returned NULL because input boolean was NULL");
			return null;
		}
		RgbImage rgbImage = new RgbImage(booleanRepresentationOfImage[0].length, booleanRepresentationOfImage.length);
		Bitmap tempBitmap = RgbImageAndroid.toBitmap(rgbImage).copy(Bitmap.Config.ARGB_8888, true);
		for (int i = 0; i < booleanRepresentationOfImage.length; i++)
			for (int j = 0; j < booleanRepresentationOfImage[0].length; j++)
				if (booleanRepresentationOfImage[i][j] == false)
					tempBitmap.setPixel(j, i, Color.WHITE);
				else
					tempBitmap.setPixel(j, i, Color.BLACK);
		return RgbImageAndroid.toRgbImage(tempBitmap);
	}

	/**
	 * Threshold method uses the Brighten and Darken methods to quickly
	 * threshold the image
	 * 
	 * @param inputImageToThreshold
	 *            The input BufferedImage of the Candidate
	 * 
	 * @param upperLimit
	 *            The upper limit which the B/W ratio of the thresholded image
	 *            should not exceed
	 * 
	 * @param lowerLimit
	 *            The lower limit which the B/W ratio of the thresholded image
	 *            should not exceed
	 * 
	 * @return The thresholded image represented as a boolean array
	 */

	public static boolean[][] threshold(RgbImage inputImageToThreshold,
			float upperLimit, float lowerLimit) {

		RgbImage imageToThreshold = (RgbImage) inputImageToThreshold.clone();
		Bitmap imageToThresholdBitmap = RgbImageAndroid.toBitmap(
				imageToThreshold).copy(Bitmap.Config.ARGB_8888, true);

		int height = imageToThreshold.getHeight();
		int width = imageToThreshold.getWidth();
		boolean thresholdedImage[][] = new boolean[height][width];
		int blackPixelCount = 0, totalThresholdingCount = 0;

		long area = height * width;

		blackPixelCount = thresholdHelper(imageToThresholdBitmap, height,
				width, thresholdedImage);
		float ratio = ((float) blackPixelCount) / ((float) area);
		totalThresholdingCount++;

		while (ratio < lowerLimit || ratio > upperLimit) {
			System.out.println("ratio: " + ratio + " low :" + lowerLimit
					+ "high :" + upperLimit);

			if (ratio > upperLimit)
				imageToThresholdBitmap = Intensity.doBrighten(imageToThresholdBitmap);
			if (ratio < lowerLimit)
				imageToThresholdBitmap = Intensity.doDarken(imageToThresholdBitmap);
			if (totalThresholdingCount > MAX_THRESHOLD_COUNT)
				break;

			blackPixelCount = thresholdHelper(imageToThresholdBitmap, height,
					width, thresholdedImage);
			totalThresholdingCount++;
			
			ratio = ((float) blackPixelCount) / ((float) area);
		}
		System.out.println(" Thresholding done!!");

		return thresholdedImage;
	}

	private static int thresholdHelper(Bitmap imageToThresholdBitmap,
			int height, int width, boolean[][] thresholdedImage) {
		int blackPixelCount = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int x = Math.abs(imageToThresholdBitmap.getPixel(i, j)) / 100000;
				if (x > MYTHRESHOLD) {
					thresholdedImage[j][i] = true;
					blackPixelCount++;
				} else {
					thresholdedImage[j][i] = false;
				}
			}
		}
		return blackPixelCount;
	}

	public static float threshold(RgbImage image) {
		int height = image.getHeight();
		int width = image.getWidth();
		int x, blackPixelCount = 0;
		float area = height * width;
		float ratio = 1.0f;
		Bitmap tempBitmapImage = RgbImageAndroid.toBitmap(image).copy(
				Bitmap.Config.ARGB_8888, true);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				x = Math.abs(tempBitmapImage.getPixel(i, j)) / 100000;
				if (x > MYTHRESHOLD)
					blackPixelCount++;
			}
		}
		ratio = ((float) blackPixelCount) / (area);
		return ratio;
	}

	public static int val(boolean in) {
		if (in == true)
			return 1;
		else
			return 0;
	}
}
