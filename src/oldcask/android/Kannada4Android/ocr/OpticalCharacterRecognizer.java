package oldcask.android.Kannada4Android.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;

import jjil.core.RgbImage;
import oldcask.android.Kannada4Android.interfaces.IOpticalCharacterRecognizer;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.KohonenNetwork;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.SampleData;
import oldcask.android.Kannda4Android.ocr.NeuralNetwork.TrainingSet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class OpticalCharacterRecognizer implements IOpticalCharacterRecognizer {
	public static final int DHEIGHT = 20;
	public static final int DWIDTH = 20;
	int MAX_QUALITY = 100;
	private ArrayList<SampleData> sampleDataList = new ArrayList<SampleData>();
	private KohonenNetwork net;

	@Override
	public void trainNetwork() {
		Load_Data();
		try {
			int inputNeuron = DHEIGHT * DWIDTH;
			int outputNeuron = sampleDataList.size();

			TrainingSet set = new TrainingSet(inputNeuron, outputNeuron);
			set.setTrainingSetCount(sampleDataList.size());

			for (int t = 0; t < sampleDataList.size(); t++) {
				int idx = 0;
				SampleData ds = (SampleData) sampleDataList.get(t);
				for (int y = 0; y < ds.getHeight(); y++) {
					for (int x = 0; x < ds.getWidth(); x++) {
						set.setInput(t, idx++, ds.getData(x, y) ? .5 : -.5);
					}
				}
			}

			net = new KohonenNetwork(inputNeuron, outputNeuron);
			net.setTrainingSet(set);
			net.learn();
			System.out.println("Training done!!!");
		} catch (Exception e) {
			System.out.println("Exception in Training...");
			e.printStackTrace(); 
		}

	}

	public void Load_Data() {
		try {
			FileReader f;// the actual file stream
			BufferedReader r;// used to read the file line by line
			f = new FileReader(new File("data/characters.txt"));
			r = new BufferedReader(f);
			String line;
			int i = 0;

			sampleDataList.clear();

			while ((line = r.readLine()) != null) {
				String[] split = line.split(":");
				SampleData ds = new SampleData(split[0], DWIDTH, DHEIGHT);

				System.out.println("Text" + split[0] + ": characters : "
						+ split[1]);

				int idx = 0;
				for (int y = 0; y < ds.getHeight(); y++) {
					for (int x = 0; x < ds.getWidth(); x++) {
						ds.setData(x, y, split[1].charAt(idx++) == '1');
					}
				}
				sampleDataList.add(i++, ds);
			}

			r.close();
			f.close();
			System.out.println("Loaded from characters.txt file");
		} catch (Exception e) {
			System.out.println("Exception while reading file");
			e.printStackTrace();
		}
	}

	@Override
	public String recognize(byte[] jpegData) {
		try {
			
			/*
			 * The following 3 lines will be removed..
			 */
			FileInputStream fis = new FileInputStream("data/img01.jpg");
			jpegData = new byte[100000];
			fis.read(jpegData);

			RgbImage inputImage = RgbImageAndroid.toRgbImage(BitmapFactory.decodeByteArray(jpegData, 0,
					jpegData.length));

			RemoveNoise removeNoise = new RemoveNoise(inputImage);
			RgbImage noiseremovedImage = removeNoise.doRemoveNoise();
			//RgbImageAndroid.toFile(null, noiseremovedImage, MAX_QUALITY,"data/noiseremoved.jpg");
			System.out.println("Noise Removal Done!!");

			boolean[][] thresholdedBoolean = Threshold.threshold(
					noiseremovedImage, 0.75f, 0.15f);

			Actions actions = new Actions(noiseremovedImage, thresholdedBoolean);
			RgbImage Perfect = actions.perfectImage();
			Perfect = actions.makePerfect(Perfect, Threshold.threshold(Perfect,
					0.71f, 0.15f));
			//RgbImageAndroid.toFile(null, Perfect, MAX_QUALITY,"data/perfected.jpg");
			System.out
					.println("*************Perfect Done and printed *************");

			boolean thresholdedBoolean2[][] = Threshold.threshold(Perfect,
					0.71f, 0.15f);
			System.out.println("Before Hsplit..");

			HSplit Splitter = new HSplit(Perfect, thresholdedBoolean2);
			int SplitPoint = Splitter.shouldSplit();

			Halves Half = new Halves(SplitPoint, Perfect);
			int halves = Half.getValidCount();

			System.out.println("No of halves = " + halves);

			BIQueue PicQueue = new BIQueue();
			Splitter.segment(Half, PicQueue);

			System.out.println("Ze Queue Holds " + PicQueue.getSize());
			
			 double input[] = new double[DWIDTH * DHEIGHT];
		        String Mapped[] = mapNeurons();
		        String characters[] = new String[100];
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

					int best = net.winner(input, normfac, synth);
					System.out.println(best + "  " + Mapped[best]);
					characters[x] = Mapped[best];
		        }
		        StringBuilder recogchar = new StringBuilder();
		        for (int i=0;i<x;i++){
		            recogchar.append(characters[i]);
		        }
		       System.out.println(recogchar.toString());
		       return recogchar.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Somethings gone a bit wrong";
	}
	String[] mapNeurons() {
        String map[] = new String[sampleDataList.size()];
        double normfac[] = new double[1];
        double synth[] = new double[1];

        for (int i = 0; i < map.length; i++) {
            map[i] = "?";
        }
        for (int i = 0; i < sampleDataList.size(); i++) {
            double input[] = new double[DWIDTH * DHEIGHT];
            int idx = 0;
            SampleData ds = (SampleData) sampleDataList.get(i);
            for (int y = 0; y < ds.getHeight(); y++) {
                for (int x = 0; x < ds.getWidth(); x++) {
                    input[idx++] = ds.getData(x, y) ? .5 : -.5;
                }
            }

            int best = net.winner(input, normfac, synth);
            map[best] = ds.getCharacters();
        }
        return map;
    }

}
