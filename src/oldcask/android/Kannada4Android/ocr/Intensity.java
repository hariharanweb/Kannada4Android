package oldcask.android.Kannada4Android.ocr;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Intensity {
	static float FACTOR = 0.7f;

	/*
	 * Used to Brighten the given Bitmap.
	 * 
	 * @Param image The Bitmap to brighten
	 * 
	 * @return The Brightened Bitmap
	 */
	public static Bitmap doBrighten(Bitmap image) {

		int width = image.getWidth();
		int height = image.getHeight();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = image.getPixel(i, j);
				int r = getRed(rgb);
				int g = getGreen(rgb);
				int b = getBlue(rgb);

				/*
				 * From 2D group: 1. black.brighter() should return grey 2.
				 * applying brighter to blue will always return blue, brighter
				 * 3. non pure color (non zero rgb) will eventually return white
				 */
				int k = (int) (1.0 / (1.0 - FACTOR));
				if (r == 0 && g == 0 && b == 0) {
					image.setPixel(i, j, Color.rgb(k, k, k));
				}
				if (r > 0 && r < i)
					r = i;
				if (g > 0 && g < i)
					g = i;
				if (b > 0 && b < i)
					b = i;

				image.setPixel(i, j, Color.rgb(Math
						.min((int) (r / FACTOR), 255), Math.min(
						(int) (g / FACTOR), 255), Math.min((int) (b / FACTOR),
						255)));

			}
		}
		return image;
	}

	/*
	 * Used to Darken the given Bitmap.
	 * 
	 * @Param image The Bitmap to darken
	 * 
	 * @return The Darkened Bitmap
	 */
	public static Bitmap doDarken(Bitmap img) {

		int w = img.getWidth();
		int h = img.getHeight();

		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int rgb = img.getPixel(i, j);
				img.setPixel(i, j, Color.rgb(Math.max(
						(int) (getRed(rgb) * FACTOR), 0), Math.max(
						(int) (getGreen(rgb) * FACTOR), 0), Math.max(
						(int) (getBlue(rgb) * FACTOR), 0)));
			}
		}
		return img;
	}

	public static int getRed(int rgb) {
		return (rgb >> 16) & 0xFF;
	}

	public static int getGreen(int rgb) {
		return (rgb >> 8) & 0xFF;
	}

	public static int getBlue(int rgb) {
		return (rgb >> 0) & 0xFF;
	}
}
