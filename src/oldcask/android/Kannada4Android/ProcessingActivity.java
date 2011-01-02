package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.ocr.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OCRResult;
import oldcask.android.Kannada4Android.ocr.OpticalCharacterRecognizerFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ProcessingActivity extends Activity {

	private static final String OCR_RESULT = "OCR_RESULT";
	private final IOpticalCharacterRecognizer opticalCharacterRecognizer;
	private static final String PIC_DATA = "PIC_DATA";

	public ProcessingActivity() {
		this(OpticalCharacterRecognizerFactory.getOpticalCharacterRecognizer());
	}

	public ProcessingActivity(
			IOpticalCharacterRecognizer opticalCharacterRecognizer) {
		this.opticalCharacterRecognizer = opticalCharacterRecognizer;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.processing);
		byte[] data = getData();

		AsyncTask<byte[], ProgressResult, OCRResult> recogniserTask = new OCRRecognizerTask(
				opticalCharacterRecognizer, this);
		recogniserTask.execute(data);
	}

	private byte[] getData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		byte[] data = (byte[]) bundle.get(PIC_DATA);
		return data;
	}

	public void setProgressView(ProgressResult progressResult) {
		ImageView progressView = (ImageView) findViewById(R.id.ResultImage);
		ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressUpdate);
		progressView.setImageBitmap(progressResult.getImage());
		progressBar.setProgress(progressResult.getProgress());
	}

	public void showResult(OCRResult result) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(OCR_RESULT, result);
		Intent resultActivityIntent = new Intent(getBaseContext(),ResultActivity.class);
		resultActivityIntent.putExtras(bundle);
		startActivity(resultActivityIntent);
	}
}
