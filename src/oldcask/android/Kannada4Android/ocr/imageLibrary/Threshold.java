package oldcask.android.Kannada4Android.ocr.imagelibrary;

import jjil.core.RgbImage;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Threshold {

	public static RgbImage makeImage(boolean booleanRepresentationOfImage[][]) {
		if (booleanRepresentationOfImage == null) {
			Log.e(Parameters.TAG_THRESHOLD,"Input boolean is NULL");
			return null;
		}
		int height = booleanRepresentationOfImage.length;
		int width = booleanRepresentationOfImage[0].length;
		RgbImage rgbImage = new RgbImage(width, height);
		
		Bitmap tempBitmap = RgbImageAndroid.toBitmap(rgbImage).copy(
				Bitmap.Config.ARGB_8888, true);
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
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
		int iterativeThreshold = Parameters.INITIAL_THRESHOLD, newThreshold = Parameters.INITIAL_THRESHOLD;

		do {
			iterativeThreshold = newThreshold;
			
			/* Step 2 : Find G1 and G2 */
			int[] objectPixels = new int[area], backgroundPixels = new int[area];
			int objectPixelsCount = 0, backgroundPixelsCount = 0;
			int objectPixelsSet = 0, backgroundPixelsSet = 0;
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					int x = Math.abs(imageToThresholdBitmap.getPixel(i, j)) / 100000;
					if (x > iterativeThreshold) {
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
		} while (newThreshold != iterativeThreshold);
		
		thresholdIterativeHelper(imageToThresholdBitmap, height, width,thresholdedImage,newThreshold);
		return thresholdedImage;
	}

	private static void thresholdIterativeHelper(Bitmap imageToThresholdBitmap,
			int height, int width, boolean[][] thresholdedImage,int newThreshold) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int x = Math.abs(imageToThresholdBitmap.getPixel(i, j)) / 100000;
				if (x > newThreshold) {
					thresholdedImage[j][i] = true;
				} else {
					thresholdedImage[j][i] = false;
				}
			}
		}
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
				if (x > Parameters.INITIAL_THRESHOLD)
					blackPixelCount++;
			}
		}
		ratio = ((float) blackPixelCount) / (area);
		return ratio;
	}
}
