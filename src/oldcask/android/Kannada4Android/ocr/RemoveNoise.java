package oldcask.android.Kannada4Android.ocr;

import jjil.core.RgbImage;
import android.graphics.Bitmap;

public class RemoveNoise {
	private RgbImage imageToRemoveNoise;

	/**
	 * Constructor to initialize the toRemoveNoise object and the Kernel
	 * 
	 * @param inp The image for which noise has to be removed
	 */
	public RemoveNoise(RgbImage imageToRemoveNoise) {
		this.imageToRemoveNoise = imageToRemoveNoise;
	}

	/**
	 * Removes noise pixels from the input image to a pre-defined extent
	 * 
	 * @param imageToRemoveNoise
	 *            The Image whose noise is to be removed
	 * 
	 * @return A noise removed image or the input image as it is depending upon
	 *         the apparent fruitfullness of the noise removal
	 */
	
	public RgbImage doRemoveNoise() {
//		float[] NoiseKernel = { 0.050f, 0.075f, 0.050f, 0.075f, 1.0f, 0.075f,
//				0.050f, 0.075f, 0.050f };
		
		float[] NoiseKernel = { 0.050f, 0.075f, 0.050f, 0.075f,0.1f, 0.075f,
				0.050f, 0.075f, 0.050f };
		
		convolve(NoiseKernel, 3, 3);
		
		RgbImage prevRgbImage = (RgbImage)imageToRemoveNoise.clone();
		double ratioOfNoiseSubtract = noiseSubtract(imageToRemoveNoise, prevRgbImage);
		System.out.print("Ratio of noise subtract  is " + ratioOfNoiseSubtract);
		
		if ((ratioOfNoiseSubtract > 0.60)) {
			System.out.println("Noise NOT Subtrated");
			return prevRgbImage;
		} else {
			System.out.println("Noise Subtrated");
			return imageToRemoveNoise;
		}
	}

	public void convolve(float[] mat, int rows, int cols)
	{
		int width = imageToRemoveNoise.getWidth();
		int height = imageToRemoveNoise.getHeight();
		if((rows % 2) == 0 || (cols % 2) == 0)
		{
		}
		else
		{
			int[] rgbData = imageToRemoveNoise.getData();
			int[] conv = new int[width*height];
			int sumR = 0;
			int sumG = 0;
			int sumB = 0;
			
			
			for(int x=(cols-1)/2; x<width-(cols+1)/2;x++)
			{
				for(int y=(rows-1)/2; y<height-(rows+1)/2;y++)
				{
					sumR=0;
					sumG=0;
					sumB=0;
					for(int x1=0;x1<cols;x1++)
					{
						for(int y1=0;y1<rows;y1++)
						{
							int x2 = (x-(cols-1)/2+x1);
							int y2 = (y-(rows-1)/2+y1);
							int R = ((rgbData[y2*width+x2]>>16) & 0xff);
							int G = ((rgbData[y2*width+x2]>>8) & 0xff);
							int B = ((rgbData[y2*width+x2]) & 0xff);
							sumR += R * (mat[y1*cols+x1]);
							sumG += G * (mat[y1*cols+x1]);
							sumB += B * (mat[y1*cols+x1]);
						}
					}
					conv[y*width+x] = 0xff000000 | ((int)sumR << 16 | (int)sumG << 8 |
							(int)sumB);
				}
			}
			System.arraycopy(conv, 0, rgbData, 0, width * height);
			System.out.println("\n\nSomething happened here in convolve***********\n\n\n");
		}
	}
	private static double noiseSubtract(RgbImage img1, RgbImage img2) {
		int val, count = 0;
		Bitmap bitmapImg1 = RgbImageAndroid.toBitmap(img1);
		Bitmap bitmapImg2 = RgbImageAndroid.toBitmap(img2);
		double area = bitmapImg1.getWidth() * bitmapImg1.getHeight();

		for (int i = 0; i < bitmapImg1.getWidth(); i++) {
			for (int j = 0; j < bitmapImg1.getHeight(); j++) {
				val = Math.abs(bitmapImg1.getPixel(i, j) - bitmapImg2.getPixel(i, j));
				val = val / 10000000;
				if (val > 0)
					count++;
			}
		}
		return (double) count / area;
	}
}
