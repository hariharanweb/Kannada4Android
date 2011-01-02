package oldcask.android.Kannada4Android;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.ocr.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OCRResult;
import android.os.AsyncTask;

final class OCRRecognizerTask extends AsyncTask<byte[], ProgressResult, OCRResult> {

	private final IOpticalCharacterRecognizer ocr;
	private final ProcessingActivity processingActivity;

	public OCRRecognizerTask(IOpticalCharacterRecognizer ocr,
			ProcessingActivity processingActivity) {
		this.ocr = ocr;
		this.processingActivity = processingActivity;
	}

	protected OCRResult doInBackground(byte[]... data) {
		byte[] jpegAsBytes = data[0];
		ProgressResult progressResult = new ProgressResult();
	
		RgbImage noiseRemoved = ocr.removeNoise(jpegAsBytes);
		progressResult.setImage(noiseRemoved);
		progressResult.setProgress(25);
		publishProgress(progressResult);
		
		RgbImage thresholdImage = ocr.thresholdImage(noiseRemoved);
		progressResult.setImage(thresholdImage);
		progressResult.setProgress(50);
		publishProgress(progressResult);
		
		RgbImage localisedImage = ocr.localiseImage(thresholdImage);
		progressResult.setImage(localisedImage);
		progressResult.setProgress(75);
		publishProgress(progressResult);
		
		OCRResult recogniseImage = ocr.recogniseImage(localisedImage);
		
		return recogniseImage;
	}

	@Override
	protected void onProgressUpdate(ProgressResult... values) {
		super.onProgressUpdate(values);
		processingActivity.setProgressView(values[0]);
	}
	
	@Override
	protected void onPostExecute(OCRResult result) {
		super.onPostExecute(result);
		processingActivity.showResult(result);
	}
	
}
