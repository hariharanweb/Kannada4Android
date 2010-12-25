package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import android.os.AsyncTask;

final class OCRRecognizerTask extends AsyncTask<byte[], Integer, Integer> {
	
	private final IOpticalCharacterRecognizer ocr;

	public OCRRecognizerTask(IOpticalCharacterRecognizer ocr) {
		this.ocr = ocr;
	}
	protected Integer doInBackground(byte[]... data) {
		byte[] jpegAsBytes = data[0];
		ocr.recogniseImage(jpegAsBytes);
		return 10;
	}
}