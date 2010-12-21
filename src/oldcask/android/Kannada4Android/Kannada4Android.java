package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OpticalCharacterRecognizer;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Kannada4Android extends Activity {
    private final IOpticalCharacterRecognizer ocr;
	private Toast makeText;

    public Kannada4Android() {
    	this(new OpticalCharacterRecognizer());
	}
    
	public Kannada4Android(IOpticalCharacterRecognizer ocr) {
		this.ocr = ocr;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeText = Toast.makeText(this, "Somethings koool going to happen when u click...", Toast.LENGTH_LONG);
        setContentView(R.layout.main);
        trainTheNetwork();
    }

	private void trainTheNetwork() {
		AsyncTask<IOpticalCharacterRecognizer, Integer, Integer> trainerTask = new TrainerDataLoader(this);
		trainerTask.execute(ocr);
	}

	public void setContinueButtonVisible() {
		Button continueButton = (Button) findViewById(R.id.ContinueButton);
		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				makeText.show();
			}
		});
		
		continueButton.setVisibility(View.VISIBLE);
	}
}