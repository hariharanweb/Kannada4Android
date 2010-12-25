package oldcask.android.Kannada4Android.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.KohonenNetwork;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.SampleData;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.TrainingSet;
import android.graphics.BitmapFactory;
import android.util.Log;

public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer {
	private static final String TAG_NAME = "OpticalCharacterRecogniser";
	private static final String TRAINING_DATA_FILE = "data/characters.txt";

	public static final int DOWNSAMPLE_HEIGHT = 20;
	public static final int DOWNSAMPLE_WIDTH = 20;

	private ArrayList<SampleData> downSampleDataList = new ArrayList<SampleData>();
	private KohonenNetwork kohonenNeuralNetwork;
	String mappedStrings[];

	int MAX_QUALITY = 100;

	@Override
	public void trainNeuralNetwork() {
		loadTrainingDataFromFile();
		trainKohonenNeuralNetwork();

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
			
			mappedStrings = mapNeurons();
			System.out.println("Training done!!!");
		} catch (Exception e) {
			Log.e(TAG_NAME,	"Kohonen Neural Network Training Not Done Properly");
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

	private String[] mapNeurons() {
		String map[] = new String[downSampleDataList.size()];
		double normfac[] = new double[1];
		double synth[] = new double[1];

		for (int i = 0; i < map.length; i++) {
			map[i] = "?";
		}
		for (int i = 0; i < downSampleDataList.size(); i++) {
			double input[] = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT];
			int idx = 0;
			SampleData sampleData = (SampleData) downSampleDataList.get(i);
			for (int y = 0; y < sampleData.getHeight(); y++) {
				for (int x = 0; x < sampleData.getWidth(); x++) {
					input[idx++] = sampleData.getData(x, y) ? .5 : -.5;
				}
			}

			int best = kohonenNeuralNetwork.winner(input, normfac, synth);
			map[best] = sampleData.getCharacters();
		}
		return map;
	}

	private void loadTrainingDataFromFile() {
		try {
			FileReader fileReader = new FileReader(new File(TRAINING_DATA_FILE));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
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
			fileReader.close();
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
	public String recogniseImage(byte[] jpegData) {
		try {

			/*
			 * The following 3 lines will be removed..
			 */
			FileInputStream fis = new FileInputStream("data/img01.jpg");
			jpegData = new byte[100000];
			fis.read(jpegData);

			RgbImage noiseremovedImage = removeNoise(jpegData);

			boolean[][] noiseRemovedThresholdedBoolean = thresholdImage(noiseremovedImage);

			RgbImage localisedImage = localiseImage(noiseRemovedThresholdedBoolean);

			BIQueue PicQueue = segmentImage(localisedImage);

			StringBuilder recognisedString = recogniseStrings(PicQueue);
			
			System.out.println(recognisedString.toString());
			
			return recognisedString.toString();
		} catch (Exception e) {
			Log.e(TAG_NAME, "Recognise Image Spit an error "+e);
			e.printStackTrace();
		}
		return "Sorry!! Somethings gone a bit wrong";
	}

	private StringBuilder recogniseStrings(BIQueue PicQueue) {
		double input[] = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT];
		String recognisedStrings[] = new String[20];
		for (int x = 0; x < PicQueue.getSize(); x++) {
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
			
			System.out.println("Kohonen Says: " + best + "  " + mappedStrings[best]);
			recognisedStrings[x] = mappedStrings[best];
		}
		StringBuilder finalRecognisedString = new StringBuilder();
		for (int i = 0; i < recognisedStrings.length; i++) {
			finalRecognisedString.append(recognisedStrings[i]);
		}
		return finalRecognisedString;
	}

	private BIQueue segmentImage(RgbImage localisedImage) {
		boolean localisedThresholdedBoolean[][] = Threshold.threshold(
				localisedImage, 0.75f, 0.15f);
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

		BIQueue PicQueue = new BIQueue();
		Splitter.segment(PicQueue);

		System.out.println("Ze Queue Holds " + PicQueue.getSize());
		return PicQueue;
	}

	private RgbImage localiseImage(boolean[][] noiseRemovedThresholdedBoolean)
			throws IOException {
		Localisation actions = new Localisation(Threshold
				.makeImage(noiseRemovedThresholdedBoolean),
				noiseRemovedThresholdedBoolean);
		RgbImage localisedImage = actions.localiseImageByWidth();
		localisedImage = actions.localiseImageByHeight(localisedImage,
				Threshold.threshold(localisedImage, 0.75f, 0.15f));
		RgbImageAndroid.toFile(null, localisedImage, MAX_QUALITY,
				"data/perfected.jpg");
		System.out.println("*************Localisation Done *************");
		return localisedImage;
	}

	private boolean[][] thresholdImage(RgbImage noiseremovedImage)
			throws IOException {
		boolean[][] noiseRemovedThresholdedBoolean = Threshold.threshold(
				noiseremovedImage, 0.75f, 0.15f);
		RgbImageAndroid.toFile(null, Threshold
				.makeImage(noiseRemovedThresholdedBoolean), MAX_QUALITY,
				"data/thresholded1.jpg");
		System.out.println("Thresholding Done after Noise Removal");
		return noiseRemovedThresholdedBoolean;
	}

	private RgbImage removeNoise(byte[] jpegData) throws IOException {
		RgbImage inputImage = RgbImageAndroid.toRgbImage(BitmapFactory
				.decodeByteArray(jpegData, 0, jpegData.length));
		RemoveNoise removeNoise = new RemoveNoise(inputImage);
		RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
		RgbImageAndroid.toFile(null, noiseremovedImage, MAX_QUALITY,
				"data/noiseremoved.jpg");
		System.out.println("Noise Removal Done!!");
		return noiseremovedImage;
	}
}
