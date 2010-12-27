package oldcask.android.Kannada4Android.ocr.imageLibrary;

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
	private static int MYTHRESHOLD = 136;

	/**
	 * Makes an RgbImage from the Given Boolean
	 * 
	 * @Param booleanRepresentationOfImage The boolean representation of the
	 *        image
	 * 
	 * @Return The RgbImage
	 */
	public static RgbImage makeImage(boolean booleanRepresentationOfImage[][]) {
		if (booleanRepresentationOfImage == null) {
			Log.e(TAG_THRESHOLD,
					"Make Image Returned NULL because input boolean was NULL");
			return null;
		}
		RgbImage rgbImage = new RgbImage(
				booleanRepresentationOfImage[0].length,
				booleanRepresentationOfImage.length);
		Bitmap tempBitmap = RgbImageAndroid.toBitmap(rgbImage).copy(
				Bitmap.Config.ARGB_8888, true);
		for (int i = 0; i < booleanRepresentationOfImage.length; i++)
			for (int j = 0; j < booleanRepresentationOfImage[0].length; j++)
				if (booleanRepresentationOfImage[i][j] == false)
					tempBitmap.setPixel(j, i, Color.WHITE);
				else
					tempBitmap.setPixel(j, i, Color.BLACK);
		return RgbImageAndroid.toRgbImage(tempBitmap);
	}

	public static boolean[][] thresholdIterative(RgbImage inputImageToThreshold) {

		RgbImage imageToThreshold = (RgbImage) inputImageToThreshold.clone();
		Bitmap imageToThresholdBitmap = RgbImageAndroid.toBitmap(
				imageToThreshold).copy(Bitmap.Config.ARGB_8888, true);

		int height = imageToThreshold.getHeight();
		int width = imageToThreshold.getWidth();
		boolean thresholdedImage[][] = new boolean[height][width];
		int area = width * height;
		/* Step 1 : To Choose Arbitrary Threshold */
		int ITERATIVETHRESHOLD = MYTHRESHOLD, newThreshold = MYTHRESHOLD;

		do {
			ITERATIVETHRESHOLD = newThreshold;
			/* Step 2 : Find G1 and G2 */
			int[] objectPixels = new int[area], backgroundPixels = new int[area];
			int objectPixelsCount = 0, backgroundPixelsCount = 0;
			int objectPixelsSet = 0, backgroundPixelsSet = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int x = Math.abs(imageToThresholdBitmap.getPixel(i, j)) / 100000;
					if (x > ITERATIVETHRESHOLD) {
						objectPixels[objectPixelsCount++] = x;
						objectPixelsSet += x;
					} else {
						backgroundPixels[backgroundPixelsCount++] = x;
						backgroundPixelsSet += x;
					}
				}
			}

			/* Step 3: Find Average in every set */
			int m1 = objectPixelsSet / objectPixelsCount;
			int m2 = backgroundPixelsSet / backgroundPixelsCount;

			/* Step 4: Find new Threshold */
			newThreshold = (m1 + m2) / 2;
			System.out.println("New Threshold = "+newThreshold);
		} while (newThreshold != ITERATIVETHRESHOLD);
		int blackPixelCount = thresholdIterativeHelper(imageToThresholdBitmap, height, width,thresholdedImage,newThreshold);
		System.out.println("Total Number of Black Pixels = " +blackPixelCount + "Ratio = "+(blackPixelCount/area));
		return thresholdedImage;
	}

	private static int thresholdIterativeHelper(Bitmap imageToThresholdBitmap,
			int height, int width, boolean[][] thresholdedImage,int newThreshold) {
		int blackPixelCount = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int x = Math.abs(imageToThresholdBitmap.getPixel(i, j)) / 100000;
				if (x > newThreshold) {
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
