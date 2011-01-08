package oldcask.android.Kannada4Android.ocr.preprocessing;

import oldcask.android.Kannada4Android.ocr.imagelibrary.Parameters;
import oldcask.android.Kannada4Android.ocr.imagelibrary.RgbImageAndroid;
import jjil.core.RgbImage;
import android.graphics.Bitmap;

public class RemoveNoise {
	private RgbImage imageToRemoveNoise;

	public RemoveNoise(RgbImage imageToRemoveNoise) {
		this.imageToRemoveNoise = imageToRemoveNoise;
	}

	public RgbImage doRemoveNoise() {
//		float[] NoiseKernel = { 0.050f, 0.075f, 0.075f, 0.075f, 0.6f, 0.050f,
//				0.075f, 0.075f, 0.075f }; ** This kernel gives me a brightened image
		
		float[] NoiseKernel = { 0.050f, 0.075f, 0.050f, 0.075f, 0.6f, 0.075f,
				0.050f, 0.075f, 0.050f };

		RgbImage inputRgbImage = (RgbImage) imageToRemoveNoise.clone();
		
		convolve(NoiseKernel, 3, 3);
		
		double ratioOfNoiseSubtract = noiseSubtract(imageToRemoveNoise,inputRgbImage);

		if ((ratioOfNoiseSubtract > Parameters.NOISE_SUBTRACT_RATIO_THRESHOLD)) {
			return inputRgbImage;
		} else {
			return imageToRemoveNoise;
		}
	}

	/**
	 * Convolves the Image Based on the Noise Kernel
	 * 
	 * @Param noiseKernel The Noise Kernel 
	 * 
	 * @Param rows The Number of Rows in the Kernel (Should Not be Divisible By 2)
	 * 
	 * @Param cols The Number of Columns in the Kernel (Should Not be Divisible By 2)
	 */
	private void convolve(float[] noiseKernel, int rows, int cols) {
		int width = imageToRemoveNoise.getWidth();
		int height = imageToRemoveNoise.getHeight();

		int[] rgbData = imageToRemoveNoise.getData();
		int[] convolved = new int[width * height];

		colvolutionAlgorithm(noiseKernel, rows, cols, width, height, rgbData,convolved);
		
		System.arraycopy(convolved, 0, rgbData, 0, width * height);
	}

	private void colvolutionAlgorithm(float[] noiseKernel, int rows, int cols,
			int width, int height, int[] rgbData, int[] convolved) {
		int sumR;
		int sumG;
		int sumB;
		for (int x = (cols - 1) / 2; x < width - (cols + 1) / 2; x++) {
			for (int y = (rows - 1) / 2; y < height - (rows + 1) / 2; y++) {
				sumR = 0;
				sumG = 0;
				sumB = 0;
				for (int x1 = 0; x1 < cols; x1++) {
					for (int y1 = 0; y1 < rows; y1++) {
						int x2 = (x - (cols - 1) / 2 + x1);
						int y2 = (y - (rows - 1) / 2 + y1);
						int R = ((rgbData[y2 * width + x2] >> 16) & 0xff);
						int G = ((rgbData[y2 * width + x2] >> 8) & 0xff);
						int B = ((rgbData[y2 * width + x2]) & 0xff);
						sumR += R * (noiseKernel[y1 * cols + x1]);
						sumG += G * (noiseKernel[y1 * cols + x1]);
						sumB += B * (noiseKernel[y1 * cols + x1]);
					}
				}
				convolved[y * width + x] = 0xff000000 | ((int) sumR << 16
						| (int) sumG << 8 | (int) sumB);
			}
		}
	}

	/**
	 * Used by the RemoveNoise method to determine the fruitfulness of the noise
	 * removal Method sums the difference of corresponding pixels from both
	 * images
	 * 
	 * @param originalImage
	 *            Original image
	 * 
	 * @param noiseRemovedImage
	 *            Noise removed image
	 * 
	 * @return the sum of the difference between corresponding pixels from both
	 *         images
	 */
	private double noiseSubtract(RgbImage originalImage, RgbImage noiseRemovedImage) {
		int val, count = 0;
		Bitmap bitmapImg1 = RgbImageAndroid.toBitmap(originalImage);
		Bitmap bitmapImg2 = RgbImageAndroid.toBitmap(noiseRemovedImage);
		double area = bitmapImg1.getWidth() * bitmapImg1.getHeight();

		for (int i = 0; i < bitmapImg1.getWidth(); i++) {
			for (int j = 0; j < bitmapImg1.getHeight(); j++) {
				val = Math.abs(bitmapImg1.getPixel(i, j)
						- bitmapImg2.getPixel(i, j));
				val = val / 10000000;
				if (val > 0)
					count++;
			}
		}
		return (double) count / area;
	}
}
