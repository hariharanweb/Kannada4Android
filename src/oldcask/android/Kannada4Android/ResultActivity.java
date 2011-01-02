package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.ocr.OCRResult;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends Activity {

	private static final String OCR_RESULT = "OCR_RESULT";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		OCRResult result = getData();
		showResult(result);
	}

	private OCRResult getData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		OCRResult data = (OCRResult) bundle.get(OCR_RESULT);
		return data;
	}

	public void showResult(OCRResult result) {
		TextView inKannadaFont = (TextView) findViewById(R.id.inKannadaFont);
		Typeface kannadaFont = Typeface.createFromAsset(getAssets(),
				"fonts/brhknd.ttf");
		inKannadaFont.setTypeface(kannadaFont);

		inKannadaFont.setText(result.getInKannada());
		TextView literalTranslation = (TextView) findViewById(R.id.translation);
		literalTranslation.setText(result.getLiteralTranslation());
	}
}
