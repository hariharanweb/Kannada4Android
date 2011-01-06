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

	public static final int DOWNSAMPLE_HEIGHT = 10;
	public static final int DOWNSAMPLE_WIDTH = 10;

	private ArrayList<SampleData> downSampleDataList = new ArrayList<SampleData>();
	private KohonenNetwork kohonenNeuralNetwork;
	String mappedStrings[];

	int MAX_QUALITY = 100;

	@Override
	public void trainNeuralNetwork(InputStream trainingData) {
		/*loadTrainingDataFromFile(trainingData);
		trainKohonenNeuralNetwork();*/
		long start = System.currentTimeMillis();
		System.out.println(start);
		FileInputStream f_in;
		try {
			f_in = new 
				FileInputStream("\\sdcard\\network.data");
			// Read object using ObjectInputStream
			ObjectInputStream obj_in = 
				new ObjectInputStream (f_in);			
			Object obj = obj_in.readObject();
			
			kohonenNeuralNetwork = (KohonenNetwork) obj;
			mappedStrings = kohonenNeuralNetwork.getMappedNeurons();
			for (int i = 0; i < mappedStrings.length; i++) {
				System.out.println(mappedStrings[i]);
			}
			System.out.println(" hurray");
			System.out.println("****************Total*********** " + mappedStrings.length);
			System.out.println("sadjaksjdbask "+(System.currentTimeMillis()-start));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void trainKohonenNeuralNetwork() {
		try {
			int inputNeuron = DOWNSAMPLE_HEIGHT * DOWNSAMPLE_WIDTH;
			int outputNeuron = downSampleDataList.size();

			TrainingSet set = new TrainingSet(inputNeuron, outputNeuron);
			set.setTrainingSetCount(downSampleDataList.size());

			populateTrainingSet(set);

			kohonenNeuralNetwork = new KohonenNetwork(inputNeuron, outputNeuron);
			kohonenNeuralNetwork.setTrainingSet(set);
			kohonenNeuralNetwork.learn();
			System.out.println("Training done!!!");
			
			
			FileOutputStream f_out = new FileOutputStream("\\sdcard\\network1.data");
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(kohonenNeuralNetwork);
			obj_out.flush();
			obj_out.close();
			
			
			mappedStrings = kohonenNeuralNetwork.mapNeurons(downSampleDataList);
			System.out.println("Mapping done!!!");
		} catch (Exception e) {
			Log
					.e(TAG_NAME,
							"Kohonen Neural Network Training Not Done Properly");
			e.printStackTrace();
		}
	}

	private void populateTrainingSet(TrainingSet set) {
		for (int index = 0; index < downSampleDataList.size(); index++) {
			int idx = 0;
			SampleData sampleData = (SampleData) downSampleDataList.get(index);
			for (int y = 0; y < sampleData.getHeight(); y++) {
				for (int x = 0; x < sampleData.getWidth(); x++) {
					set.setInput(index, idx++, sampleData.getData(x, y) ? .5
							: -.5);
				}
			}
		}
	}

	

	private void loadTrainingDataFromFile(InputStream trainingDataStream) {
		try {
			InputStreamReader streamReader = new InputStreamReader(
					trainingDataStream);
			BufferedReader bufferedReader = new BufferedReader(streamReader);
			String lineInTheTrainingDataFile;

			downSampleDataList.clear();

			while ((lineInTheTrainingDataFile = bufferedReader.readLine()) != null) {
				String[] characterPronunciationDownsampleDataArray = lineInTheTrainingDataFile
						.split(":");
				SampleData sampleData = new SampleData(
						characterPronunciationDownsampleDataArray[0],
						DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);

				System.out.println("Text"
						+ characterPronunciationDownsampleDataArray[0]
						+ ": characters : "
						+ characterPronunciationDownsampleDataArray[1]);

				populateSampleData(characterPronunciationDownsampleDataArray,
						sampleData);
				downSampleDataList.add(sampleData);
			}

			bufferedReader.close();
			streamReader.close();
			System.out.println("Loaded from characters.txt file");
		} catch (Exception e) {
			Log.e(TAG_NAME, "Training Data File Not Found");
			e.printStackTrace();
		}
	}

	private void populateSampleData(
			String[] characterPronunciationDownsampleDataArray, SampleData ds) {
		int idx = 0;
		for (int y = 0; y < ds.getHeight(); y++) {
			for (int x = 0; x < ds.getWidth(); x++) {
				ds.setData(x, y, characterPronunciationDownsampleDataArray[1]
						.charAt(idx++) == '1');
			}
		}
	}

	@Override
	public OCRResult recogniseImage(RgbImage localisedImage) {
		try {

			/*
			 * The following 3 lines will be removed..
			 */
			/*FileInputStream fis = new FileInputStream("data/img06.jpg");
			jpegData = new byte[1000000];
			fis.read(jpegData);*/

			/*RgbImage noiseremovedImage = removeNoise(jpegData);

			RgbImage thresholdImage = thresholdImage(noiseremovedImage);
			

			RgbImage localisedImage = localiseImage(thresholdImage);*/

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
					input[idx++] = (FromQueue[i][j] == true) ? .5 : -.5;
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
		RgbImageAndroid.toFile(null, localisedImage, MAX_QUALITY,
				"perfected.jpg");
		System.out.println("*************Localisation Done *************");
		return localisedImage;
	}

	public RgbImage thresholdImage(RgbImage noiseremovedImage) {
		boolean[][] noiseRemovedThresholdedBoolean = Threshold
				.thresholdIterative(noiseremovedImage);
		RgbImageAndroid.toFile(null, Threshold
				.makeImage(noiseRemovedThresholdedBoolean), MAX_QUALITY,
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
