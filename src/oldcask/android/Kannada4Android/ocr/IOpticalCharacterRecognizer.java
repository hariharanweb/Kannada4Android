package oldcask.android.Kannada4Android.ocr;

import java.io.InputStream;

import jjil.core.RgbImage;

public interface IOpticalCharacterRecognizer {
	public void trainNeuralNetwork(InputStream trainingData);
	
	public OCRResult recogniseImage(RgbImage localisedImage);
	public RgbImage removeNoise(byte[] jpegData);
	public RgbImage thresholdImage(RgbImage noiseremovedImage);
	public RgbImage localiseImage(RgbImage thresholdImage);
}
