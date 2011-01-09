package oldcask.android.Kannada4Android.ocr;

import java.io.InputStream;
import java.io.ObjectInputStream;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Parameters;
import oldcask.android.Kannada4Android.ocr.imageLibrary.RgbImageAndroid;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Threshold;
import oldcask.android.Kannada4Android.ocr.neuralnetwork.KohonenNetwork;
import oldcask.android.Kannada4Android.ocr.preProcessing.Localisation;
import oldcask.android.Kannada4Android.ocr.preProcessing.RemoveNoise;
import oldcask.android.Kannada4Android.ocr.recognition.Segmentation;
import oldcask.android.Kannada4Android.ocr.recognition.SegmentedImageProcessor;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer {
	private KohonenNetwork kohonenNeuralNetwork;
	private String mappedNeurons[];
	
	@Override
	public void trainNeuralNetwork(InputStream trainingData) {
		try {
			ObjectInputStream objectInputStream = 
				new ObjectInputStream (trainingData);			
			Object serializedNeuralNetwork = objectInputStream.readObject();
			
			kohonenNeuralNetwork = (KohonenNetwork) serializedNeuralNetwork;
			mappedNeurons = kohonenNeuralNetwork.getMappedNeurons();
		} catch (Exception e) {
			Log.e(Parameters.TAG_OCR, "Neural Network Training Error. Check Whether You Have The Proper Serialized Object" + e);
			e.printStackTrace();
		}
	}

	@Override
	public OCRResult recogniseImage(RgbImage localisedImage) {
		try {
			/*FileInputStream fis = new FileInputStream("data/D.jpg");
			byte[] jpegData = new byte[1000000];
			fis.read(jpegData);
			RgbImage noiseremovedImage = removeNoise(jpegData);
			RgbImage thresholdImage = thresholdImage(noiseremovedImage);
			RgbImage localisedImage = localiseImage(thresholdImage);*/

			SegmentedImageProcessor segmentedImageProcessor = segmentImage(localisedImage);
			StringBuilder recognisedString = recogniseStrings(segmentedImageProcessor);

			System.out.println(recognisedString.toString());
			OCRResult ocrResult = new OCRResult(recognisedString.toString(),
					recognisedString.toString());

			return ocrResult;
		} catch (Exception e) {
			Log.e(Parameters.TAG_OCR, "Recognise Image Spit an error " + e);
			e.printStackTrace();
		}
		return new OCRResult("", "Sorry!! Somethings gone a bit wrong");
	}

	private StringBuilder recogniseStrings(SegmentedImageProcessor segmentedImageProcessor) {
		double input[] = new double[Parameters.DOWNSAMPLE_WIDTH * Parameters.DOWNSAMPLE_HEIGHT];
		String recognisedStrings[] = new String[Parameters.MAX_CHARACTERS_RECOGNISABLE];
		int x;
		for (x = 0; x < segmentedImageProcessor.getNumberOfValidSegments(); x++) {
			int idx = 0;
			boolean downsample[][] = segmentedImageProcessor.getDownsample(x);
			for (int i = 0; i < downsample.length; i++) {
				for (int j = 0; j < downsample[0].length; j++) {
					input[idx++] = (downsample[j][i] == true) ? .5 : -.5;
				}
			}

			double normfac[] = new double[1];
			double synth[] = new double[1];
			int best = kohonenNeuralNetwork.winner(input, normfac, synth);

			recognisedStrings[x] = mappedNeurons[best];
		}
		StringBuilder finalRecognisedString = new StringBuilder();
		for (int i = 0; i < x; i++) {
			finalRecognisedString.append(recognisedStrings[i]);
		}

		return finalRecognisedString;
	}

	private SegmentedImageProcessor segmentImage(RgbImage localisedImage) {
		boolean localisedThresholdedBoolean[][] = Threshold.thresholdIterative(localisedImage);
		RgbImage localisedThresholdedImage = Threshold.makeImage(localisedThresholdedBoolean);
		Segmentation segmenter = new Segmentation(localisedThresholdedImage,
				localisedThresholdedBoolean);

		SegmentedImageProcessor segmentedImageProcessor = new SegmentedImageProcessor();
		segmenter.segment(segmentedImageProcessor);

		return segmentedImageProcessor;
	}

	public RgbImage localiseImage(RgbImage thresholdImage){
		Localisation actions = new Localisation(thresholdImage,Threshold.thresholdIterative(thresholdImage));
		/* Doing Localisation By Width, Height and then again Width 
		 * Removes Some Shadows and Gives Significant improvement in the result
		 */
		RgbImage localisedImage = actions.localiseImageByWidth();
		localisedImage = actions.localiseImageByHeight();
		localisedImage = actions.localiseImageByWidth();
		return localisedImage;
	}

	public RgbImage thresholdImage(RgbImage noiseremovedImage) {
		boolean[][] noiseRemovedThresholdedBoolean = Threshold
				.thresholdIterative(noiseremovedImage);
		return Threshold.makeImage(noiseRemovedThresholdedBoolean);
	}

	public RgbImage removeNoise(byte[] jpegData) {
		Options options = new BitmapFactory.Options();
		options.inSampleSize = 4 ;
		RgbImage inputImage = RgbImageAndroid.toRgbImage(BitmapFactory
				.decodeByteArray(jpegData, 0, jpegData.length,options));
		RemoveNoise removeNoise = new RemoveNoise(inputImage);
		RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
		return noiseremovedImage;
	}
}
