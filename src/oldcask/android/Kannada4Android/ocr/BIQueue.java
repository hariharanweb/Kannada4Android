package oldcask.android.Kannada4Android.ocr;

import java.util.ArrayList;
import java.util.List;

import jjil.core.RgbImage;

/**
 * @author Source Code
 */
public class BIQueue {

	/**
	 * A Queue of BufferedImages, containing the segments of the Number Plate
	 */
	List<RgbImage> Bi;

	/**
	 * A Queue of BufferedImages, containing the thinned segemnts of the Number
	 * Plates
	 */
	List<RgbImage> Ti;

	/**
	 * Number of valid segemnts obtained from the Number Plates
	 */
	int ElementCount;

	/**
	 * An object of the Hilditch class
	 */
	Hilditch Thinner;

	/**
	 * An object of the Downsampler class
	 */
	DownSample Sampler[];

	/**
	 * Constuctor of the BufferedImageQueue
	 * 
	 */
	public BIQueue() {
		Bi = new ArrayList<RgbImage>(20);
		Ti = new ArrayList<RgbImage>(20);
		ElementCount = 0;
		Thinner = new Hilditch();
		Sampler = new DownSample[20];
	}

	/**
	 * All segments are processed one by one and inserted into the Queues
	 * 
	 * @param inp
	 *            A Segment of the Number Plate
	 * 
	 * @param temp
	 *            The boolean representation of the thresholded input image
	 */
	public void insert(RgbImage inp, boolean temp[][]) {
		Bi.add(inp);

		boolean ttemp[][] = new boolean[temp.length + 4][temp[0].length + 4];
		Thinner.adjust(temp, ttemp, temp.length, temp[0].length);

		RgbImage TMP = Thinner.dothin(ttemp, ttemp[0].length,
				ttemp.length, 3);

		if (TMP != null) {
			ttemp = new boolean[TMP.getHeight()][TMP.getWidth()];
			ttemp = Threshold.threshold(TMP, 0.999f, 0.001f);

			Actions.Print(ttemp);

			Ti.add(TMP);
		} else {
			Bi.remove(ElementCount);
			return;
		}
		Sampler[ElementCount] = new DownSample();
		Sampler[ElementCount].DoDownSample(TMP, ttemp);
		ElementCount++;
	}

	/**
	 * Returns the nth element from the Bi Queue
	 * 
	 * @param index
	 *            The index value of the segemnt required
	 * 
	 * @return The nth element of the Bi Queue
	 */
	public RgbImage getPic(int index) {
		return Bi.get(index);
	}

	/**
	 * Returns the nth element from the Ti Queue
	 * 
	 * @param index
	 *            The index value of the Segment required
	 * 
	 * @return The nth element of the Ti Queue
	 */
	public RgbImage getThinPic(int index) {
		return Ti.get(index);
	}

	/**
	 * Returns the nth Downsampled Segment
	 * 
	 * @param index
	 *            The index value of the segemnt required
	 * 
	 * @return The nth Downsampled Segment
	 */
	public boolean[][] getArray(int index) {
		return Sampler[index].getDownSampled();
	}

	/**
	 * 
	 * @return The size of the Queue
	 */
	public int getSize() {
		return ElementCount;
	}

	/**
	 * A heuristic method to check if the segemnt contains a character
	 * 
	 * @param TMP
	 *            The Segment
	 * 
	 * @param t
	 *            Boolean representation of the Segment
	 * 
	 * @return true if it can possibly contrain a character. Else returns false
	 */
	private boolean checkValid(RgbImage TMP, boolean t[][]) {
		if (TMP == null)
			return false;

		if (TMP.getHeight() < TMP.getWidth())
			return false;

		if (TMP.getHeight() < 10 && TMP.getWidth() < 10)
			return false;

		for (int i = 0; i < TMP.getHeight(); i++) {
			if (HistAnl.getStrengthH(t, i, 0, TMP.getWidth()) == 0)
				return false;
		}
		return true;
	}
}
