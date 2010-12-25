package oldcask.android.Kannada4Android.ocr;


public class OpticalCharacterRecognizerFactory {
	private static IOpticalCharacterRecognizer ocr;
	
	public static IOpticalCharacterRecognizer getOpticalCharacterRecognizer(){
		if(ocr==null)
			ocr = new OpticalCharacterRecognizer();
		return ocr;
	}
}
