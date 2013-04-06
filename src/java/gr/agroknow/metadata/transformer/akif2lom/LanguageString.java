package gr.agroknow.metadata.transformer.akif2lom;

public class LanguageString 
{
	private String language ;
	private String string ;
	
	public LanguageString(String language, String string)
	{
		this.language = language ;
		this.string = string ;
	}
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}

}
