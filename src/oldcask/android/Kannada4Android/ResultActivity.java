package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.ocr.OCRResult;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity {
	
	private static final String OCR_RESULT = "OCR_RESULT";
	private TextToSpeech textToSpeech;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result);
		OCRResult result = getData();
		showResult(result);
		final Button speakButton = (Button) findViewById(R.id.SpeakOut);
		OnInitListener ttsListener = new TextToSpeechInitListener(speakButton);
		textToSpeech = new TextToSpeech(getApplicationContext(), ttsListener);
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
			textToSpeech.speak(literalTranslation.getText().toString(),
					TextToSpeech.QUEUE_ADD, null);
		}
	}

	private final class TextToSpeechInitListener implements OnInitListener {
		private final Button speakButton;

		private TextToSpeechInitListener(Button speakButton) {
			this.speakButton = speakButton;
		}

		public void onInit(int status) {
			speakButton.setVisibility(View.VISIBLE);
		}
	}

}
