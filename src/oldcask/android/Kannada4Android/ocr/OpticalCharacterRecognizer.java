package oldcask.android.Kannada4Android.ocr;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.ocr.imageLibrary.RgbImageAndroid;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Threshold;
import oldcask.android.Kannada4Android.ocr.preProcessing.Localisation;
import oldcask.android.Kannada4Android.ocr.preProcessing.RemoveNoise;
import oldcask.android.Kannada4Android.ocr.recognition.Segmentation;
import oldcask.android.Kannada4Android.ocr.recognition.SegmentedImageQueue;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.KohonenNetwork;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.SampleData;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.TrainingSet;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer {
	private static final String TAG_NAME = "OpticalCharacterRecogniser";
	private KohonenNetwork kohonenNeuralNetwork;
	private String mappedStrings[];
	
	public static final int DOWNSAMPLE_HEIGHT = 15;
	public static final int DOWNSAMPLE_WIDTH = 15;


	@Override
	public void trainNeuralNetwork(InputStream trainingData) {
		try {
			ObjectInputStream objectInputStream = 
				new ObjectInputStream (trainingData);			
			Object serializedObject = objectInputStream.readObject();
			
			kohonenNeuralNetwork = (KohonenNetwork) serializedObject;
			mappedStrings = kohonenNeuralNetwork.getMappedNeurons();
		} catch (Exception e) {
			Log.e(TAG_NAME, "Neural Network Training Error. Check Whether You Have The Proper Serialized Object" + e);
			e.printStackTrace();
		}
	}

	@Override
	public OCRResult recogniseImage(RgbImage localisedImage1) {
		try {

			/*
			 * The following 3 lines will be removed..
			 */
			FileInputStream fis = new FileInputStream("data/c.jpg");
			byte[] jpegData = new byte[1000000];
			fis.read(jpegData);

			RgbImage noiseremovedImage = removeNoise(jpegData);

			RgbImage thresholdImage = thresholdImage(noiseremovedImage);
			

			RgbImage localisedImage = localiseImage(thresholdImage);

			SegmentedImageQueue PicQueue = segmentImage(localisedImage);

			StringBuilder recognisedString = recogniseStrings(PicQueue);

			System.out.println(recognisedString.toString());
			OCRResult ocrResult = new OCRResult(recognisedString.toString(),
					recognisedString.toString());

			return ocrResult;
		} catch (Exception e) {
			Log.e(TAG_NAME, "Recognise Image Spit an error " + e);
			e.printStackTrace();
		}
		return new OCRResult("", "Sorry!! Somethings gone a bit wrong");
	}

	private StringBuilder recogniseStrings(SegmentedImageQueue PicQueue) {
		double input[] = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT];
		String recognisedStrings[] = new String[20];
		int x;
		for (x = 0; x < PicQueue.getSize(); x++) {
			int idx = 0;
			boolean FromQueue[][] = PicQueue.getArray(x);
			for (int i = 0; i < FromQueue.length; i++) {
				for (int j = 0; j < FromQueue[0].length; j++) {
					input[idx++] = (FromQueue[j][i] == true) ? .5 : -.5;
				}
			}

			double normfac[] = new double[1];
			double synth[] = new double[1];
			int best = kohonenNeuralNetwork.winner(input, normfac, synth);

			System.out.println("Kohonen Says: " + best + "  "
					+ mappedStrings[best]);
			recognisedStrings[x] = mappedStrings[best];
		}
		StringBuilder finalRecognisedString = new StringBuilder();
		for (int i = 0; i < x; i++) {
			finalRecognisedString.append(recognisedStrings[i]);
		}

		return finalRecognisedString;
	}

	private SegmentedImageQueue segmentImage(RgbImage localisedImage) {
		boolean localisedThresholdedBoolean[][] = Threshold
				.thresholdIterative(localisedImage);
		RgbImage localisedThresholdedImage = Threshold
				.makeImage(localisedThresholdedBoolean);
		Segmentation Splitter = new Segmentation(localisedThresholdedImage,
				localisedThresholdedBoolean);
		/*
		 * Code below will be needed for multiple lines recognition only
		 */
		// int SplitPoint = Splitter.shouldSplit();
		//
		// Halves Half = new Halves(SplitPoint, localisedImage);
		// int halves = Half.getValidCount();
		//
		// System.out.println("No of halves = " + halves);

		SegmentedImageQueue PicQueue = new SegmentedImageQueue();
		Splitter.segment(PicQueue);

		System.out.println("Ze Queue Holds " + PicQueue.getSize());
		return PicQueue;
	}

	public RgbImage localiseImage(RgbImage thresholdImage){
		Localisation actions = new Localisation(thresholdImage,
				Threshold.thresholdIterative(thresholdImage));
		RgbImage localisedImage = actions.localiseImageByWidth();
		localisedImage = actions.localiseImageByHeight(localisedImage,
				Threshold.thresholdIterative(localisedImage));
//		localisedImage = actions.localiseImageByWidth();
		RgbImageAndroid.toFile(null, localisedImage, 100,
				"perfected.jpg");
		System.out.println("*************Localisation Done *************");
		return localisedImage;
	}

	public RgbImage thresholdImage(RgbImage noiseremovedImage) {
		boolean[][] noiseRemovedThresholdedBoolean = Threshold
				.thresholdIterative(noiseremovedImage);
		RgbImageAndroid.toFile(null, Threshold
				.makeImage(noiseRemovedThresholdedBoolean), 100,
				"thresholded1.jpg");
		System.out.println("Thresholding Done after Noise Removal");
		return Threshold.makeImage(noiseRemovedThresholdedBoolean);
	}

	public RgbImage removeNoise(byte[] jpegData) {
		
		Options options = new BitmapFactory.Options();
		options.inSampleSize =4 ;
		RgbImage inputImage = RgbImageAndroid.toRgbImage(BitmapFactory
				.decodeByteArray(jpegData, 0, jpegData.length,options));
		RemoveNoise removeNoise = new RemoveNoise(inputImage);
		RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
		/*RgbImageAndroid.toFile(null, noiseremovedImage, MAX_QUALITY,
				"noiseremoved.jpg");
		System.out.println("Noise Removal Done!!");*/
		return noiseremovedImage;
	}
	
}
