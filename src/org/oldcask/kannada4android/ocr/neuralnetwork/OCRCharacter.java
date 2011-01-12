package org.oldcask.kannada4android.ocr.neuralnetwork;

import java.io.Serializable;

public class OCRCharacter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String character;
	private String literalTranslation;

	public OCRCharacter(String character, String literalTranslation) {
		this.character = character;
		this.literalTranslation = literalTranslation;
	}
	/**
	 * @return the character
	 */
	public String getCharacter() {
		return character;
	}

	/**
	 * @param character
	 *            the character to set
	 */
	public void setCharacter(String character) {
		this.character = character;
	}

	/**
	 * @return the literalTranslation
	 */
	public String getLiteralTranslation() {
		return literalTranslation;
	}

	/**
	 * @param literalTranslation
	 *            the literalTranslation to set
	 */
	public void setLiteralTranslation(String literalTranslation) {
		this.literalTranslation = literalTranslation;
	}
}
