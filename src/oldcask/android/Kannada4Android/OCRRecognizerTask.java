package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.ocr.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OCRResult;
import android.os.AsyncTask;

final class OCRRecognizerTask extends AsyncTask<byte[], Integer, OCRResult> {

	private final IOpticalCharacterRecognizer ocr;
	private final ResultActivity resultActivity;

	public OCRRecognizerTask(IOpticalCharacterRecognizer ocr,
			ResultActivity resultActivity) {
		this.ocr = ocr;
		this.resultActivity = resultActivity;
	}

	protected OCRResult doInBackground(byte[]... data) {
		byte[] jpegAsBytes = data[0];
		OCRResult result = ocr.recogniseImage(jpegAsBytes);
		return result;
	}

	@Override
	protected void onPostExecute(OCRResult result) {
		super.onPostExecute(result);
		resultActivity.showResult(result);
	}
}