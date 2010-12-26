package oldcask.android.Kannada4Android.ocr;

public class OCRResult {	
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
