package oldcask.android.Kannada4Android.interfaces;

public interface IOpticalCharacterRecognizer {
	public void trainNetwork();
	public String recognize(byte[] jpegData);
}
