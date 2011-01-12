package oldcask.android.Kannada4Android;

import java.util.Locale;

import oldcask.android.Kannada4Android.ocr.OCRResult;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity implements
		TextToSpeech.OnInitListener {

	private static final String OCR_RESULT = "OCR_RESULT";
	private TextToSpeech textToSpeech;
	private Button speakButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		OCRResult result = getData();
		showResult(result);
		speakButton = (Button) findViewById(R.id.SpeakOut);
		textToSpeech = new TextToSpeech(this, this);
		speakButton.setOnClickListener(new SpeakOutClickListener());
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

	private final class SpeakOutClickListener implements View.OnClickListener {
		public void onClick(View v) {
			TextView literalTranslation = (TextView) findViewById(R.id.translation);
			textToSpeech.speak("hello world",
					TextToSpeech.QUEUE_ADD, null);
		}
	}

	@Override
	public void onInit(int status) {
		textToSpeech.setLanguage(Locale.US);
		speakButton.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onDestroy() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		super.onDestroy();
	}
}
