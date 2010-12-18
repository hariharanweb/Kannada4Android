package oldcask.android.Kannada4Android;

import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import oldcask.android.Kannada4Android.ocr.OpticalCharacterRecognizer;
import android.app.Activity;
import android.os.Bundle;

public class Kannada4Android extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        IOpticalCharacterRecognizer ocr = new OpticalCharacterRecognizer();
        ocr.recognize(null);
        System.out.println("Done here!!");
    }
}