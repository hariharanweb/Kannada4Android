package oldcask.android.Kannada4Android.ocr.NeuralNetwork;

/**
 * Java Neural Network Example Handwriting Recognition by Jeff Heaton
 * (http://www.jeffheaton.com) 1-2002
 * ------------------------------------------------- A class that is used to
 * store a downsampled character.
 * 
 * @author Jeff Heaton (http://www.jeffheaton.com)
 * @version 1.0
 */

public class SampleData implements Cloneable {

	/**
	 * The downsampled data as a grid of booleans.
	 */
	protected boolean downsampledDataGrid[][];

	/**
	 * The characters.
	 */
	protected String characters;

	/**
	 * The constructor
	 * 
	 * @param characters
	 *            The Characters
	 * @param width
	 *            The width
	 * @param height
	 *            The height
	 */
	public SampleData(String characters, int width, int height) {
		downsampledDataGrid = new boolean[width][height];
		this.characters = characters;
	}

	/**
	 * Set one pixel of sample data.
	 * 
	 * @param x_coordinate
	 *            The x coordinate
	 * @param y_coordinate
	 *            The y coordinate
	 * @param value
	 *            The value to set
	 */
	public void setData(int x_coordinate, int y_coordinate, boolean value) {
		downsampledDataGrid[x_coordinate][y_coordinate] = value;
	}

	/**
	 * Get a pixel from the sample.
	 * 
	 * @param x_coordinate
	 *            The x coordinate
	 * @param y_coordinate
	 *            The y coordinate
	 * @return The requested pixel
	 */
	public boolean getData(int x_coordinate, int y_coordinate) {
		return downsampledDataGrid[x_coordinate][y_coordinate];
	}

	/**
	 * Clear the downsampled image
	 */
	public void clear() {
		for (int x = 0; x < downsampledDataGrid.length; x++)
			for (int y = 0; y < downsampledDataGrid[0].length; y++)
				downsampledDataGrid[x][y] = false;
	}

	/**
	 * Get the height of the down sampled image.
	 * 
	 * @return The height of the downsampled image.
	 */
	public int getHeight() {
		return downsampledDataGrid[0].length;
	}

	/**
	 * Get the width of the downsampled image.
	 * 
	 * @return The width of the downsampled image
	 */
	public int getWidth() {
		return downsampledDataGrid.length;
	}

	/**
	 * Get the characters that this sample represents.
	 * 
	 * @return The characters that this sample represents.
	 */
	public String getCharacters() {
		return characters;
	}

	/**
	 * Set the letter that this sample represents.
	 * 
	 * @param letter
	 *            The letter that this sample represents.
	 */
	public void setCharacters(String characters) {
		this.characters = characters;
	}

	/**
	 * Compare this sample to another, used for sorting.
	 * 
	 * @param o
	 *            The object being compared against.
	 * @return Same as String.compareTo
	 */

	public int compareTo(Object o) {
		SampleData obj = (SampleData) o;
		if (this.getCharacters().compareTo(obj.getCharacters()) > 1)
			return 1;
		else
			return -1;
	}

	/**
	 * Create a copy of this sample
	 * 
	 * @return A copy of this sample
	 */
	public Object clone() {
		SampleData obj = new SampleData(characters, getWidth(), getHeight());
		for (int y = 0; y < getHeight(); y++)
			for (int x = 0; x < getWidth(); x++)
				obj.setData(x, y, getData(x, y));
		return obj;
	}

}