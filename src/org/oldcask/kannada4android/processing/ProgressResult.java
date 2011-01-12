package org.oldcask.kannada4android.processing;

import org.oldcask.kannada4android.ocr.imagelibrary.RgbImageAndroid;

import android.graphics.Bitmap;
import jjil.core.RgbImage;

public class ProgressResult {
	private int progress;
	private Bitmap image;

	public int getProgress() {
		return progress;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setImage(RgbImage image) {
		this.image = RgbImageAndroid.toBitmap(image);
	}
}