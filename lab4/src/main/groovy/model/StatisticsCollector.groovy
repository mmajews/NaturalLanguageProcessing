package model

import com.google.common.base.Preconditions
import org.apache.commons.lang3.StringUtils
import org.eclipse.collections.impl.factory.Maps
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StatisticsCollector {
    private static Logger logger = LoggerFactory.getLogger(StatisticsCollector.class)
    private static WORD_REGEX = "\\s+"
    private static NON_LETTERS = "^[^a-zA-Z0-9\\\\s]+|[^a-zA-Z0-9\\\\s]+\$"
    private def wordToOccurrence = Maps.mutable.empty();

    void appendFileToDictionary(File inputFile) {
        Preconditions.checkArgument(inputFile.exists(), "Input file: {} does not exist", inputFile.getAbsolutePath())
        def inputText = inputFile.text
        inputText.replaceAll(NON_LETTERS, " ")
        Arrays.asList(inputText.split(WORD_REGEX))
                .stream()
                .map { el -> el.toUpperCase() }
                .map { el -> el.trim() }
                .map { el -> el.replaceAll(NON_LETTERS, "") }
                .filter { el -> StringUtils.isAlpha(el) }
                .forEach { word -> addWordToMap(word) }
        logger.info("Finished creating dictionary from file: {}", inputFile.getAbsolutePath())
    }

    private void addWordToMap(String word) {
        if (wordToOccurrence.containsKey(word)) {
            wordToOccurrence.put(word, wordToOccurrence.get(word) + 1)
        } else {
            wordToOccurrence.put(word, 1)
        }
    }

}
