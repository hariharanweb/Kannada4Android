package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.interfaces.OpticalCharacterRecognizerFactory;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Kannada4Android extends Activity {
	private final IOpticalCharacterRecognizer ocr;

	public Kannada4Android() {
		this(OpticalCharacterRecognizerFactory.getOpticalCharacterRecognizer());
	}

	public Kannada4Android(IOpticalCharacterRecognizer ocr) {
		this.ocr = ocr;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		trainTheNetwork();
	}

	private void trainTheNetwork() {
		AsyncTask<IOpticalCharacterRecognizer, Integer, Integer> trainerTask = new TrainerDataLoader(
				this);
		trainerTask.execute(ocr);
	}

	public void setContinueButtonVisible() {
		Button continueButton = (Button) findViewById(R.id.ContinueButton);
		continueButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						CameraActivity.class);
				startActivity(intent);
			}
		});

		continueButton.setVisibility(View.VISIBLE);
	}
}