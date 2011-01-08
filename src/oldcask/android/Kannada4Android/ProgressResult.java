package oldcask.android.Kannada4Android;

import android.graphics.Bitmap;
import oldcask.android.Kannada4Android.ocr.imagelibrary.RgbImageAndroid;
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