package nlp.lab1.ngram.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NgramStatisticsCounter {
	private static final String REGEX_WORD_SPLIT = "[,.!? \":\n\r-]+";

	public void getNGramStatistics(File sourceFile) {
		String contentOfFile = FileReader.getContentOfFile(sourceFile);
		List<String> allWordsInText = splitContentToListOfWords(contentOfFile);

	}

	private List<String> splitContentToListOfWords(String content){
		String[] splitedContentByWords = content.split(REGEX_WORD_SPLIT);
		return Arrays.asList(splitedContentByWords);
	}
}
