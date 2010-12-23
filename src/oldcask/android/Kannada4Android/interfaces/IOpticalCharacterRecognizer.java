package oldcask.android.Kannada4Android.interfaces;

public interface IOpticalCharacterRecognizer {
	public void trainNeuralNetwork();
	public String recogniseImage(byte[] jpegData);
}
