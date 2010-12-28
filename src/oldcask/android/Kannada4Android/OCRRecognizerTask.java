package oldcask.android.Kannada4Android;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.ocr.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OCRResult;
import oldcask.android.Kannada4Android.ocr.imageLibrary.RgbImageAndroid;
import android.os.AsyncTask;

final class OCRRecognizerTask extends AsyncTask<byte[], RgbImage, OCRResult> {

	private final IOpticalCharacterRecognizer ocr;
	private final ResultActivity resultActivity;

	public OCRRecognizerTask(IOpticalCharacterRecognizer ocr,
			ResultActivity resultActivity) {
		this.ocr = ocr;
		this.resultActivity = resultActivity;
	}

	protected OCRResult doInBackground(byte[]... data) {
		byte[] jpegAsBytes = data[0];
		RgbImage noiseRemoved = ocr.removeNoise(jpegAsBytes);
		publishProgress(noiseRemoved);
		
		RgbImage thresholdImage = ocr.thresholdImage(noiseRemoved);
		publishProgress(thresholdImage);
		
		RgbImage localisedImage = ocr.localiseImage(thresholdImage);
		publishProgress(localisedImage);
		
		OCRResult recogniseImage = ocr.recogniseImage(localisedImage);
		
		return recogniseImage;
	}

	@Override
	protected void onProgressUpdate(RgbImage... values) {
		super.onProgressUpdate(values);
		resultActivity.setProgressView(RgbImageAndroid.toBitmap(values[0]));
	}
	
	@Override
	protected void onPostExecute(OCRResult result) {
		super.onPostExecute(result);
		resultActivity.showResult(result);
	}
}