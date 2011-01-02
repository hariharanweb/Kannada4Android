package oldcask.android.Kannada4Android.ocr;

import java.io.Serializable;

public class OCRResult implements Serializable{	
	private static final long serialVersionUID = 1L;
	private String inKannada;
	private String literalTranslation;
	
	public OCRResult(String inKannada,String literalTranslation) {
		this.inKannada = inKannada;
		this.literalTranslation = literalTranslation;
	}

	public String getInKannada() {
		return inKannada;
	}

	public String getLiteralTranslation() {
		return literalTranslation;
	}
}
