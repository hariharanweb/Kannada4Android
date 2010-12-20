package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OpticalCharacterRecognizer;
import android.app.Activity;
import android.os.Bundle;

public class Kannada4Android extends Activity {
    private final IOpticalCharacterRecognizer ocr;

    public Kannada4Android() {
    	this(new OpticalCharacterRecognizer());
	}
    
	public Kannada4Android(IOpticalCharacterRecognizer ocr) {
		this.ocr = ocr;
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        trainTheNetwork();
    }

	private void trainTheNetwork() {
		Thread trainerThread = new Thread(new Runnable() {
			public void run() {
				ocr.trainNetwork();
			}
		});
		trainerThread.start();
	}
}