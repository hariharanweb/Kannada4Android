package oldcask.android.Kannada4Android.ocr;

import jjil.algorithm.RgbCrop;
import jjil.core.Error;
import jjil.core.RgbImage;

public class Halves {
	/**
	 * After the class divides the candidate into two, Half[] contains valid
	 * halves.
	 */
	RgbImage Half[] = new RgbImage[2];

	/**
	 * Stores the number of rows the Image has been printed in. Is always
	 * 0,1 or 2
	 */
	int ActHalf;

	/**
	 * 
	 * @param SplitPoint
	 *            The splitpoint is found by the HSplit class. Gives the Y
	 *            position where the Image can be split.
	 * 
	 * @param input
	 *            The Candidate image
	 */
	public Halves(int SplitPoint, RgbImage input) {
		RgbImage[] HalfX = makeSplit(SplitPoint, input);
		Half = HalfX;
		ActHalf = checkGood(Half);
	}

	/**
	 * @param SplitPoint
	 *            The Y position where the Image has to be split. If the
	 *            HSplit class has determined that the Image has been
	 *            printed in one row, it returns the SplitPoint equal to the
	 *            height (h-1) of the image
	 * 
	 * @param input
	 *            The Candidate Image
	 * 
	 * @return The one/two halves of the Image. Both halves will be the
	 *         same if the SpliPoint is equal to the height.
	 */
	public RgbImage[] makeSplit(int SplitPoint, RgbImage input) {
		RgbImage Parts[] = new RgbImage[2];

		try{
		RgbCrop croppedImagePart0 = new RgbCrop(0, 0, input.getWidth(), SplitPoint + 1);
		croppedImagePart0.push(input);
		if(!croppedImagePart0.isEmpty())
			Parts[0] = (RgbImage) croppedImagePart0.getFront();
		else
			Parts[0] = input;

		if (SplitPoint == input.getHeight() - 1)
			Parts[1] = null;
		else
		{
			RgbCrop croppedImagePart1 = new RgbCrop(0, SplitPoint, input.getWidth() ,(input.getHeight() - SplitPoint));
			croppedImagePart1.push(input);
			if(!croppedImagePart1.isEmpty())
				Parts[1]  = (RgbImage) croppedImagePart1.getFront();
			else
				Parts[1] = input;
		}
		}  catch (Error e) {
			System.out.println("*********************Splitting is returning an error. I think because of the width*********************");
			e.printStackTrace();
		}
		return Parts;
	}

	/**
	 * @param inputs
	 *            The RgbImage array, the halves, split by the MakeSplit
	 *            method
	 * 
	 * @return Determines and returns the Number of rows the Image has
	 *         been printed in.
	 */
	public int checkGood(RgbImage inputs[]) {
		float ratio;
		int GoodCount = 0, trialCount = 0;
		for (int i = 0; i < 2; i++) {
			if (inputs[i] == null)
				continue;
			ratio = Threshold.threshold(inputs[i]);
			// System.out.print("r" + ratio+" " );
			if (ratio < 0.56f && Half[i].getHeight() > 5) {
				Half[GoodCount] = inputs[i];
				GoodCount++;
			}
		}
		// System.out.println(GoodCount);
		while (GoodCount == 0 && trialCount < 3) {
			Localisation Act = new Localisation(inputs[0], Threshold.threshold(
					inputs[0], 0.70f, 0.10f));
			Half[0] = Act.localiseImageByWidth();
			ratio = Threshold.threshold(Half[0]);
			if (ratio < 0.56f && Half[0].getHeight() > 5)
				GoodCount++;
			trialCount++;
		}
		if (GoodCount == 0)
			return 1;
		return GoodCount;
	}

	/**
	 * @return The number of rows in which the Image has been printed.
	 */
	public int getValidCount() {
		return ActHalf;
	}

	/**
	 * @return the RgbImage array Halves, either 1 or 2.
	 */
	public RgbImage[] getHalves() {
		return Half;
	}
}
