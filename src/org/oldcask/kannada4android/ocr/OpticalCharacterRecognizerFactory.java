package org.oldcask.kannada4android.ocr;


public class OpticalCharacterRecognizerFactory {
	private static IOpticalCharacterRecognizer ocr;
	
	public static IOpticalCharacterRecognizer getOpticalCharacterRecognizer(){
		if(ocr==null)
			ocr = new OpticalCharacterRecognizer();
		return ocr;
	}
}
