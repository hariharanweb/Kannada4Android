package oldcask.android.Kannada4Android;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.ocr.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OCRResult;
import oldcask.android.Kannada4Android.ocr.OpticalCharacterRecognizerFactory;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends Activity {

	private final IOpticalCharacterRecognizer opticalCharacterRecognizer;
	private static final String PIC_DATA = "PIC_DATA";

	public ResultActivity() {
		this(OpticalCharacterRecognizerFactory.getOpticalCharacterRecognizer());
	}

	public ResultActivity(IOpticalCharacterRecognizer opticalCharacterRecognizer) {
		this.opticalCharacterRecognizer = opticalCharacterRecognizer;
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		byte[] data = getData();

		AsyncTask<byte[], RgbImage, OCRResult> recogniserTask = new OCRRecognizerTask(
				opticalCharacterRecognizer, this);
		recogniserTask.execute(data);
	}

	private byte[] getData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		byte[] data = (byte[]) bundle.get(PIC_DATA);
		return data;
	}

	public void showResult(OCRResult result) {
		TextView processing = (TextView) findViewById(R.id.yourResult);
		processing.setText("Results");
		
		TextView inKannadaFont = (TextView) findViewById(R.id.inKannadaFont);
		Typeface kannadaFont = Typeface.createFromAsset(getAssets(), "fonts/brhknd.ttf");
		inKannadaFont.setTypeface(kannadaFont);
		
		inKannadaFont.setText(result.getInKannada());
		TextView literalTranslation = (TextView) findViewById(R.id.translation);
		literalTranslation.setText(result.getLiteralTranslation());
	}

	public void setProgressView(Bitmap bitmap) {
		ImageView progressView = (ImageView) findViewById(R.id.ResultImage);
		progressView.setImageBitmap(bitmap);
	}
}
