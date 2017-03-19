package pl.agh.edu.nlp.services;

/**
 * Created by mmajewski on 19.03.2017.
 */
public class StringHelper {

	public static String trimCleanUpperCase(String input) {
		return input.trim().toUpperCase().replaceAll("[,!;.?:\"'^%$#@]", "");
	}
}
