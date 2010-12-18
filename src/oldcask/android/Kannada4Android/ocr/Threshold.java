package oldcask.android.Kannada4Android.ocr;

import java.io.IOException;

import jjil.core.RgbImage;
import android.graphics.Bitmap;
import android.graphics.Color;

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

	/**
	 * Empty constructor for the Convert class
	 * 
	 */
	public Threshold() {

	}

	
	/**
	 * Threshold method uses the Brighten and Darken methods to quickly
	 * threshold the image independent of whether the number plate is in shadows
	 * or not
	 * 
	 * @param imageToThreshold
	 *            The input BufferedImage of the Candidate
	 * 
	 * @param high
	 *            The upper limit which the B/W ratio of the thresholded image
	 *            should not exceed
	 * 
	 * @param low
	 *            The lower limit which the B/W ratio of the thresholded image
	 *            should not exceed
	 * 
	 * @return The thresholded image represented as a boolean array
	 */

	public static boolean[][] threshold(RgbImage imageToThresholdInput, float high, float low) {
		final int THRESHOLD = 140;
		RgbImage imageToThreshold = (RgbImage) imageToThresholdInput.clone();
		
		int h = imageToThreshold.getHeight();
		int w = imageToThreshold.getWidth();
		boolean t[][] = new boolean[h][w];
		int x = 1, bcount = 0, count = 0;
		
		RgbImage tempImage = new RgbImage(w, h);
		Bitmap tempBitmapImage = RgbImageAndroid.toBitmap(tempImage).copy(Bitmap.Config.ARGB_8888, true);
		Bitmap imageToThresholdBitmap = RgbImageAndroid.toBitmap(imageToThreshold).copy(Bitmap.Config.ARGB_8888, true);
		
		long area = h * w;
		float ratio = 1.0f;

		while (ratio < low || ratio > high) {
			count++;
			if (count > 2 && ratio > high)
				imageToThresholdBitmap = Intensity.doBrighten(imageToThresholdBitmap);
			if (count > 2 && ratio < low)
				imageToThresholdBitmap = Intensity.doDarken(imageToThresholdBitmap);
			if (count > 10)
				break;
			bcount = 0;
			for (int i = 0; i < w; i++) {
				for (int j = 0; j < h; j++) {
					x = Math.abs(imageToThresholdBitmap.getPixel(i, j)) / 100000;
					//System.out.println(" pixel value x = " + x);
					if (x > THRESHOLD) {
						tempBitmapImage.setPixel(i, j, Color.BLACK);
						t[j][i] = true;
						bcount++;
					} else {
						tempBitmapImage.setPixel(i, j, Color.WHITE);
						t[j][i] = false;
					}
				}
			}
			ratio = ((float) bcount) / ((float) area);
		}
		
		
		System.out.println(" Thresholding done!!");
		try {
			RgbImageAndroid.toFile(null, RgbImageAndroid.toRgbImage(tempBitmapImage), 100, "data/thresholded1.jpg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return t;
	}


	public static int val(boolean in) {
		if (in == true)
			return 1;
		else
			return 0;
	}
}
