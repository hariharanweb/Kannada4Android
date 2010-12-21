package oldcask.android.Kannada4Android.ocr;

import java.io.FileInputStream;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer{
	public static final int DHEIGHT = 10;
	public static final int DWIDTH = 10;
	int MAX_QUALITY = 100;
	
	@Override
	public void trainNetwork() {
		//some heavy process here takes time....
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void recognize(byte[] jpegData) {
		try {
			FileInputStream fis = new FileInputStream("data/img02.jpg");
			jpegData = new byte[100000];fis.read(jpegData);
			
			Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0,jpegData.length);
			RgbImage img = RgbImageAndroid.toRgbImage(bitmap);
			
			RemoveNoise removeNoise = new RemoveNoise(img);
			RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
			RgbImageAndroid.toFile(null, noiseremovedImage, MAX_QUALITY, "data/noiseremoved.jpg");
			System.out.println("Noise Removal Done!!");
			
			boolean[][] thresholdedBoolean = Threshold.threshold(noiseremovedImage, 0.75f, 0.15f);
			
			Actions actions = new Actions(noiseremovedImage, thresholdedBoolean);
			RgbImage Perfect = actions.perfectImage();
			Perfect = actions.makePerfect(Perfect, Threshold.threshold(Perfect, 0.71f, 0.15f));
			RgbImageAndroid.toFile(null, Perfect, MAX_QUALITY, "data/perfected.jpg");
			System.out.println("*************Perfect Done and printed *************");
			
			boolean thresholdedBoolean2[][] = Threshold.threshold(Perfect, 0.71f, 0.15f);
			System.out.println("Before Hsplit..");
			
			HSplit Splitter = new HSplit(Perfect, thresholdedBoolean2);
			int SplitPoint = Splitter.shouldSplit();
			
			Halves Half = new Halves(SplitPoint , Perfect);
			int halves = Half.getValidCount();
			
			System.out.println("No of halves = " + halves);
			
			BIQueue PicQueue = new BIQueue();
			Splitter.segment(Half, PicQueue );
			
			System.out.println("Ze Queue Holds " + PicQueue.getSize());
			
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
