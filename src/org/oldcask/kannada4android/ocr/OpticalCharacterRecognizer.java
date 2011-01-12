package org.oldcask.kannada4android.ocr;

import java.io.InputStream;
import java.io.ObjectInputStream;

import org.oldcask.kannada4android.ocr.imagelibrary.Parameters;
import org.oldcask.kannada4android.ocr.imagelibrary.RgbImageAndroid;
import org.oldcask.kannada4android.ocr.imagelibrary.Threshold;
import org.oldcask.kannada4android.ocr.neuralnetwork.KohonenNetwork;
import org.oldcask.kannada4android.ocr.neuralnetwork.OCRCharacter;
import org.oldcask.kannada4android.ocr.preprocessing.Localisation;
import org.oldcask.kannada4android.ocr.preprocessing.RemoveNoise;
import org.oldcask.kannada4android.ocr.recognition.Segmentation;
import org.oldcask.kannada4android.ocr.recognition.SegmentedImageProcessor;


import jjil.core.RgbImage;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer {
	private KohonenNetwork kohonenNeuralNetwork;
	private OCRCharacter mappedNeurons[];

	@Override
	public void trainNeuralNetwork(InputStream trainingData) {
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(
					trainingData);
			Object serializedNeuralNetwork = objectInputStream.readObject();

			kohonenNeuralNetwork = (KohonenNetwork) serializedNeuralNetwork;
			mappedNeurons = kohonenNeuralNetwork.getMappedNeurons();
		} catch (Exception e) {
			Log.e(Parameters.TAG_OCR,
					"Neural Network Training Error. Check Whether You Have The Proper Serialized Object"
							+ e);
			e.printStackTrace();
		}
	}

	@Override
	public OCRResult recogniseImage(RgbImage localisedImage) {
		try {
			/*
			 * FileInputStream fis = new FileInputStream("data/D.jpg"); byte[]
			 * jpegData = new byte[1000000]; fis.read(jpegData); RgbImage
			 * noiseremovedImage = removeNoise(jpegData); RgbImage
			 * thresholdImage = thresholdImage(noiseremovedImage); RgbImage
			 * localisedImage = localiseImage(thresholdImage);
			 */

			SegmentedImageProcessor segmentedImageProcessor = segmentImage(localisedImage);
			OCRCharacter recognisedCharacter = recogniseStrings(segmentedImageProcessor);
			OCRResult ocrResult = new OCRResult(recognisedCharacter.getCharacter(),recognisedCharacter.getLiteralTranslation());

			return ocrResult;
		} catch (Exception e) {
			Log.e(Parameters.TAG_OCR, "Recognise Image Spit an error " + e);
			e.printStackTrace();
		}
		return new OCRResult("", "Sorry!! Somethings gone a bit wrong");
	}

	private OCRCharacter recogniseStrings(
			SegmentedImageProcessor segmentedImageProcessor) {
		double input[] = new double[Parameters.DOWNSAMPLE_WIDTH
				* Parameters.DOWNSAMPLE_HEIGHT];
		OCRCharacter ocrCharacters[] = new OCRCharacter[Parameters.MAX_CHARACTERS_RECOGNISABLE];
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
			ocrCharacters[x] = new OCRCharacter(
					mappedNeurons[best].getCharacter(),
					mappedNeurons[best].getLiteralTranslation());
			System.out.println("Recognised = "
					+ mappedNeurons[best].getCharacter() + " Literal ="
					+ mappedNeurons[best].getLiteralTranslation());
		}
		StringBuilder recognisedString = new StringBuilder();
		StringBuilder literalTranslation = new StringBuilder();
		for (int i = 0; i < x; i++) {
			recognisedString.append(ocrCharacters[i].getCharacter());
			literalTranslation.append(ocrCharacters[i].getLiteralTranslation());
		}
		return new OCRCharacter(recognisedString.toString(),
				literalTranslation.toString());
	}

	private SegmentedImageProcessor segmentImage(RgbImage localisedImage) {
		boolean localisedThresholdedBoolean[][] = Threshold
				.thresholdIterative(localisedImage);
		RgbImage localisedThresholdedImage = Threshold
				.makeImage(localisedThresholdedBoolean);
		Segmentation segmenter = new Segmentation(localisedThresholdedImage,
				localisedThresholdedBoolean);

		SegmentedImageProcessor segmentedImageProcessor = new SegmentedImageProcessor();
		segmenter.segment(segmentedImageProcessor);

		return segmentedImageProcessor;
	}

	public RgbImage localiseImage(RgbImage thresholdImage) {
		Localisation actions = new Localisation(thresholdImage,
				Threshold.thresholdIterative(thresholdImage));
		/*
		 * Doing Localisation By Width, Height and then again Width Removes Some
		 * Shadows and Gives Significant improvement in the result
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
		options.inSampleSize = 4;
		RgbImage inputImage = RgbImageAndroid.toRgbImage(BitmapFactory
				.decodeByteArray(jpegData, 0, jpegData.length, options));
		RemoveNoise removeNoise = new RemoveNoise(inputImage);
		RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
		return noiseremovedImage;
	}
}
