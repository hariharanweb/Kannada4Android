package oldcask.android.Kannada4Android.ocr;

import java.io.File;
import java.io.PrintStream;

import jjil.algorithm.RgbCrop;
import jjil.core.Error;
import jjil.core.RgbImage;

public class Actions {
	int Strength[];
	RgbImage inputImage;
	boolean[][] inputBoolean;
	int h, w;

	/**
	 * 
	 * @param input
	 *            The input BufferedImage on which the operations will be done
	 * @param t
	 *            Boolean representation of the thresholded version of the input
	 *            image
	 */
	public Actions(RgbImage inputImage, boolean inputBoolean[][]) {
		this.inputImage = inputImage;
		this.inputBoolean = inputBoolean;
		h = inputImage.getHeight();
		w = inputImage.getWidth();
		Strength = new int[h];
		for (int i = 0; i < h; i++) {
			Strength[i] = HistAnl.getStrengthH(inputBoolean, i, 0, w);
		}
	}

	/**
	 * Localises the Number Plate vertically from the given candidate
	 * 
	 * @param inputImage
	 *            The candidate
	 * 
	 * @param inputBoolean
	 *            The candidate's thresholded boolean representation
	 * 
	 * @return A better localization of the Number Plate on the candidate The
	 *         returned image is reduced in Width
	 */

	public RgbImage perfectImage() {

		int h = inputImage.getHeight();
		int w = inputImage.getWidth();

		int strength[];
		try {
			strength = new int[w];
			int prev = 0, lend = 0, j = 0, rend = w - 1;

			for (int i = 0; i < w; i++)
				strength[i] = HistAnl.getStrengthV(inputBoolean, 0, i, h);

			boolean found = false;
			while (!found && j < w) {
				prev = lend;
				lend = j++;
				if (j == 1)
					prev = lend;
				if (Math.abs(strength[prev] - strength[lend]) >= (int) (h / 4))
					found = true;
			}

			found = false;
			j = w - 1;
			while (!found && j >= 0) {
				prev = rend;
				rend = j--;
				if (j == w - 2)
					prev = rend;
				if (Math.abs(strength[prev] - strength[rend]) > (int) (h / 4))
					found = true;
			}
			RgbCrop croppedImage = new RgbCrop(lend, 0, (rend - lend), h - 1);
			croppedImage.push(inputImage);
			if(!croppedImage.isEmpty())
				inputImage = (RgbImage) croppedImage.getFront();
			
		} catch (Exception e) {
			System.out.print(e);
		} catch (Error e) {
			e.printStackTrace();
		}

		refreshData(inputImage, Threshold.threshold(inputImage, 0.71f, 0.15f));
		return inputImage;
	}
	
	/**
	 * Once the PerfectImage method is completed, the Image may be
	 * altered The refresh method refreshes the constructor initiated values It
	 * alters the parameters w,h & Strength
	 * 
	 * @param input
	 *            the altered BufferedImage received from PerfectPlate method
	 * @param startx
	 *            the beginning horizontal pos of the image relative to the
	 *            original image
	 * @param endx
	 *            the eding horizontal pos of the image relative to the original
	 *            image
	 * @param t
	 *            the boolean representation of the thrtesholded version of the
	 *            image
	 */
	private void refreshData(RgbImage input, boolean t[][]) {
		h = input.getHeight();
		w = input.getWidth();
		for (int i = 0; i < h; i++) {
			Strength[i] = HistAnl.getStrengthH(t, i, 0, w);
		}
	}

	/**
	 * The Makeperfect further localizes the Image on the candidate
	 * Vertical dimensions may be changed
	 * 
	 * @param imageToBePerfected
	 *            the input, the candidate on which the number plate is to be
	 *            localized
	 * @param t
	 *            the boolean representation thresholded input image
	 * @return the localized number plate on the image
	 */

	public RgbImage makePerfect(RgbImage imageToBePerfected, boolean t[][]) {
		int uend = 0, j = 0, dend = imageToBePerfected.getHeight() - 1;

		try {
			boolean found = false;
			while (!found & j < h / 4) {
				uend = j++;
				if (Strength[uend] <= 5)
					found = true;
			}
			if (found == false)
				uend = 0;

			found = false;
			j = h - 1;
			while (!found & j >= (3 * h / 4)) {
				dend = j--;
				if (Strength[dend] <= 5)
					found = true;
			}
			if (found == false)
				dend = h - 1;

			System.out.print("\n UEND" + uend + " DEND" + dend + "HEIGHT"
					+ imageToBePerfected.getHeight());
			
			RgbCrop croppedImage = new RgbCrop(uend, 0, w-1, (dend - uend + 1));
			croppedImage.push(imageToBePerfected);
			if(!croppedImage.isEmpty())
				imageToBePerfected = (RgbImage) croppedImage.getFront();
			
		} catch (Exception e) {
			System.out.println(e);
		} catch (Error e) {
			System.out.println(" Printing stack trace");
			e.printStackTrace();
		}
		Print(Threshold.threshold(imageToBePerfected, 0.71f, 0.15f));
		return imageToBePerfected;
	}

	/**
	 * Prints the image on the console
	 * 
	 * @param t
	 *            the boolean representation of the image to be printed
	 */
	public static void Print(boolean t[][]) {
		File f = new File("data/fullimage.txt");
		try {
			PrintStream ps = new PrintStream(f);
			
		
		for (int i = 0; i < t.length/* h */; i++) {
			for (int j = 0; j < t[0].length/* w */; j++) {
				if (t[i][j] == true)
					ps.print("@");
				else
					ps.print(" ");
			}
			ps.println("|" + (i));
		}
	ps.close();
	
	for (int i = 0; i < t.length/* h */; i++) {
		for (int j = 0; j < t[0].length/* w */; j++) {
			if (t[i][j] == true)
				System.out.print("@");
			else
				System.out.print(" ");
		}
		System.out.println("|" + (i));
	}
	}catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
}
}
