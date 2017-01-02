package de.uni_leipzig.imise.BioPortalExtractor;

import java.net.URLEncoder;

/**
 * This class uses the translation API of http://www.transltr.org .
 * 
 * @author cbeger
 *
 */
public class Translator {
	
	private static final String FROM = "de";
	private static final String TO   = "en";
	
	
	/**
	 * Translates a german string into english.
	 * 
	 * @param string german string
	 * @return english translation
	 */
	public static String translate(String string) {
		return translate(string, FROM, TO);
	}
	
	/**
	 * Translates a string from a given language into the requested language.
	 * 
	 * @param string string which will get translated
	 * @param from origin language
	 * @param to translation language
	 * @return resulting translation
	 */
	public static String translate(String string, String from, String to) {
		try {
			String url = 
				"http://www.transltr.org/api/translate?text="
				+ URLEncoder.encode(string, "UTF-8")
				+ "&to=" + to + "&from=" + from;
			
			return JsonRequest.get(url).get("translationText").asText();
		} catch (Exception e) { }
		
		return null;
	}
	
}
