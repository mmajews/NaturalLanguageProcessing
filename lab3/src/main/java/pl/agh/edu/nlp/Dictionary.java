package pl.agh.edu.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pl.agh.edu.nlp.StringHelper.trimCleanUpperCase;

public class Dictionary {
	private static final Logger logger = LoggerFactory.getLogger(Dictionary.class);
	private Map<String, String> dictionaryMap = new HashMap<>();

	public Dictionary(File file) {
		String contentOfFile = FileReader.getContentOfFile(file);

		logger.info("Reading from odm.txt ...");
		List<String> wholeDictionary = Arrays.asList(contentOfFile.split("\\r?\\n"));
		for (String line : wholeDictionary) {
			String[] words = line.split(",");
			String defaultForm = trimCleanUpperCase(words[0]);
			dictionaryMap.put(defaultForm, defaultForm);
			for (String word : words) {
				dictionaryMap.put(trimCleanUpperCase(word), defaultForm);
			}

		}
		logger.info("Finished creating dictionary");
	}

	public String getDefaultForm(String word) {
		String defaultForm = dictionaryMap.get(word.trim().toUpperCase());
		if (defaultForm == null) {
			return word.trim().toUpperCase();
		}
		return defaultForm;
	}
}
