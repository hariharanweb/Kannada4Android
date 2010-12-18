package oldcask.android.Kannada4Android.ocr;

import java.io.FileInputStream;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer{
	int MAX_QUALITY = 100;
	@Override
	public void trainNetwork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recognize(byte[] jpegData) {
		try {
			FileInputStream fis = new FileInputStream("data/img01.jpg");
			jpegData = new byte[100000];fis.read(jpegData);
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0,jpegData.length);
			RgbImage img = RgbImageAndroid.toRgbImage(bitmap);
			
			RemoveNoise removeNoise = new RemoveNoise(img);
			RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
			RgbImageAndroid.toFile(null, noiseremovedImage, MAX_QUALITY, "data/img02.jpg");
			System.out.println("Noise Removal Done!!");
			
			Threshold.threshold(noiseremovedImage, 0.75f, 0.15f);
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
