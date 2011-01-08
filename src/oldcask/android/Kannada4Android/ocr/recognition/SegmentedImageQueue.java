package oldcask.android.Kannada4Android.ocr.recognition;

import java.util.ArrayList;
import java.util.List;

import oldcask.android.Kannada4Android.ocr.imageLibrary.Hilditch;
import oldcask.android.Kannada4Android.ocr.imageLibrary.Threshold;
import oldcask.android.Kannada4Android.ocr.preProcessing.Localisation;

import jjil.core.RgbImage;

/**
 * @author Source Code
 */
public class SegmentedImageQueue {
	private static final int LAYERS_TO_THIN = 2;
	private static final int MAX_CHARACTERS = 20;
	List<RgbImage> segmentsList;
	List<RgbImage> thinnedSegmentsList;
	int validSegments;
	Hilditch segmentThinner;
	DownSample downSampler[];

	public SegmentedImageQueue() {
		segmentsList = new ArrayList<RgbImage>(MAX_CHARACTERS);
		thinnedSegmentsList = new ArrayList<RgbImage>(MAX_CHARACTERS);
		validSegments = 0;
		segmentThinner = new Hilditch();
		downSampler = new DownSample[MAX_CHARACTERS];
	}

	/**
	 * All segments are processed one by one and inserted into the Queues
	 * 
	 * @param inputSegment
	 *            A Segment of the Image
	 * 
	 * @param inputBoolean
	 *            The boolean representation of the thresholded input image
	 */
	public void insert(RgbImage inputSegment, boolean inputBoolean[][]) {
		segmentsList.add(inputSegment);

		boolean tempBoolean[][] = new boolean[inputBoolean.length + 4][inputBoolean[0].length + 4];
		padFalse(inputBoolean, tempBoolean, inputBoolean.length, inputBoolean[0].length);

		RgbImage TMP = segmentThinner.thinningHilditch(tempBoolean, tempBoolean[0].length,
				tempBoolean.length, LAYERS_TO_THIN);

		if (TMP != null) {
			tempBoolean = new boolean[TMP.getHeight()][TMP.getWidth()];
			tempBoolean = Threshold.thresholdIterative(TMP);
			Localisation.Print(tempBoolean);

			thinnedSegmentsList.add(TMP);
		} else {
			segmentsList.remove(validSegments);
			return;
		}
		downSampler[validSegments] = new DownSample();
		downSampler[validSegments].DoDownSample(TMP, tempBoolean);
		validSegments++;
	}
	/**
	 * 
	 * Adjust method pads the source boolean array with 2 rows of 'false' values
	 * 
	 * @param sourceBoolean
	 *            The source array
	 * @param resultBoolean
	 *            The bigger resulting array
	 * @param height
	 *            Height of the array (t1.length)
	 * @param width
	 *            Width of the array (t1[0].length)
	 * 
	 * @return The padded array
	 */
	public boolean[][] padFalse(boolean sourceBoolean[][], boolean resultBoolean[][], int height, int width) {

		for (int i = 0; i < width + 4; i++) {
			resultBoolean[0][i] = false;
			resultBoolean[height + 1][i] = false;
		}

		for (int j = 0; j < height + 4; j++) {
			resultBoolean[j][0] = false;
			resultBoolean[j][width + 1] = false;
		}

		for (int k = 0, ki = 2; k < height; k++, ki++)
			for (int l = 0, li = 2; l < width; l++, li++)
				resultBoolean[ki][li] = sourceBoolean[k][l];
		return resultBoolean;
	}
	/**
	 * Returns the nth element from the segmentsList Queue
	 * 
	 * @param index
	 *            The index value of the segment required
	 * 
	 * @return The nth element of the segmentsList Queue
	 */
	public RgbImage getPic(int index) {
		return segmentsList.get(index);
	}

	/**
	 * Returns the nth element from the thinnedSegmentsList Queue
	 * 
	 * @param index
	 *            The index value of the Segment required
	 * 
	 * @return The nth element of the thinnedSegmentsList Queue
	 */
	public RgbImage getThinPic(int index) {
		return thinnedSegmentsList.get(index);
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
		return downSampler[index].getDownSampled();
	}

	/**
	 * 
	 * @return The size of the Queue
	 */
	public int getSize() {
		return validSegments;
	}
}
