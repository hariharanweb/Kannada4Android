package oldcask.android.Kannada4Android.ocr;

import java.io.InputStream;

public interface IOpticalCharacterRecognizer {
	public void trainNeuralNetwork(InputStream trainingData);
	public String recogniseImage(byte[] jpegData);
}