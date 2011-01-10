package oldcask.android.Kannada4Android.ocr.NeuralNetwork;

import java.io.Serializable;

/**
 * Java Neural Network Example Handwriting Recognition by Jeff Heaton
 * (http://www.jeffheaton.com) 1-2002
 * -------------------------------------------------
 * 
 * This class holds a training set for the Kohonen network. This is usually the
 * set of characters that were inputted by the user.
 * 
 * @author Jeff Heaton (http://www.jeffheaton.com)
 * @version 1.0
 */

public class TrainingSet implements Serializable {

	private static final long serialVersionUID = 1L;
	protected int inputCount;
	protected int outputCount;
	protected double input[][];
	protected double output[][];
	protected double classify[];
	protected int trainingSetCount;

	/**
	 * The constructor.
	 * 
	 * @param inputCount
	 *            Number of input neurons
	 * @param outputCount
	 *            Number of output neurons
	 */
	public TrainingSet(int inputCount, int outputCount) {
		this.inputCount = inputCount;
		this.outputCount = outputCount;
		trainingSetCount = 0;
	}

	/**
	 * Get the input neuron count
	 * 
	 * @return The input neuron count
	 */
	public int getInputCount() {
		return inputCount;
	}

	/**
	 * Get the output neuron count
	 * 
	 * @return The output neuron count
	 */
	public int getOutputCount() {
		return outputCount;
	}

	/**
	 * Set the number of entries in the training set. This method also allocates
	 * space for them.
	 * 
	 * @param trainingSetCount
	 *            How many entries in the training set.
	 */
	public void setTrainingSetCount(int trainingSetCount) {
		this.trainingSetCount = trainingSetCount;
		input = new double[trainingSetCount][inputCount];
		output = new double[trainingSetCount][outputCount];
		classify = new double[trainingSetCount];
	}

	/**
	 * Get the training set data.
	 * 
	 * @return Training set data.
	 */
	public int getTrainingSetCount() {
		return trainingSetCount;
	}

	/**
	 * Set one of the training set's inputs.
	 * 
	 * @param set
	 *            The entry number
	 * @param index
	 *            The index(which item in that set)
	 * @param value
	 *            The value
	 * @exception java.lang.RuntimeException
	 */
	public void setInput(int set, int index, double value)
			throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		if ((index < 0) || (index >= inputCount))
			throw (new RuntimeException("Training input index out of range:"
					+ index));
		input[set][index] = value;
	}

	/**
	 * Set one of the training set's outputs.
	 * 
	 * @param set
	 *            The entry number
	 * @param index
	 *            The index(which item in that set)
	 * @param value
	 *            The value
	 * @exception java.lang.RuntimeException
	 */
	public void setOutput(int set, int index, double value)
			throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		if ((index < 0) || (set >= outputCount))
			throw (new RuntimeException("Training input index out of range:"
					+ index));
		output[set][index] = value;
	}

	/**
	 * Set one of the training set's classifications.
	 * 
	 * @param set
	 *            The entry number
	 * @param value
	 *            The value
	 * @exception java.lang.RuntimeException
	 */
	public void setClassify(int set, double value) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		classify[set] = value;
	}

	/**
	 * Get a specified input value.
	 * 
	 * @param set
	 *            The input entry.
	 * @param index
	 *            The index
	 * @return An individual input
	 * @exception java.lang.RuntimeException
	 */
	public double getInput(int set, int index) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		if ((index < 0) || (index >= inputCount))
			throw (new RuntimeException("Training input index out of range:"
					+ index));
		return input[set][index];
	}

	/**
	 * Get one of the output values.
	 * 
	 * @param set
	 *            The entry
	 * @param index
	 *            Which value in the entry
	 * @return The output value.
	 * @exception java.lang.RuntimeException
	 */
	public double getOutput(int set, int index) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		if ((index < 0) || (set >= outputCount))
			throw (new RuntimeException("Training input index out of range:"
					+ index));
		return output[set][index];
	}

	/**
	 * Get the classification.
	 * 
	 * @param set
	 *            Which entry.
	 * @return The classification for the specified entry.
	 * @exception java.lang.RuntimeException
	 */
	public double getClassify(int set) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		return classify[set];
	}

	/**
	 * Calculate the classifications.
	 * 
	 * @param c
	 *            The classification
	 */
	public void CalculateClass(int c) {
		for (int i = 0; i <= trainingSetCount; i++) {
			classify[i] = c + 0.1;
		}
	}

	/**
	 * Get an output set.
	 * 
	 * @param set
	 *            The entry requested.
	 * @return The complete output set as an array.
	 * @exception java.lang.RuntimeException
	 */

	public double[] getOutputSet(int set) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		return output[set];
	}

	/**
	 * Get an input set.
	 * 
	 * @param set
	 *            The entry requested.
	 * @return The complete input set as an array.
	 * @exception java.lang.RuntimeException
	 */

	public double[] getInputSet(int set) throws RuntimeException {
		if ((set < 0) || (set >= trainingSetCount))
			throw (new RuntimeException("Training set out of range:" + set));
		return input[set];
	}

}
