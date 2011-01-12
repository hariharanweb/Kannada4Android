package org.oldcask.kannada4android.ocr.imagelibrary;

import jjil.core.RgbImage;
import android.graphics.Bitmap;

public class RgbImageAndroid {
	static public RgbImage toRgbImage(Bitmap bmp) {
		int nWidth = bmp.getWidth();
		int nHeight = bmp.getHeight();
		RgbImage rgb = new RgbImage(nWidth, nHeight);
		bmp.getPixels(rgb.getData(), 0, nWidth, 0, 0, nWidth, nHeight);
		return rgb;
	}

	static public Bitmap toBitmap(RgbImage rgb) {
		return Bitmap.createBitmap(rgb.getData(), rgb.getWidth(), rgb
				.getHeight(), Bitmap.Config.ARGB_8888);
	}
}
