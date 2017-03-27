package model

import com.google.common.base.Preconditions
import metrics.LDCounter
import metrics.LevensteinMetrics
import org.apache.commons.lang3.StringUtils
import org.eclipse.collections.impl.factory.Lists
import org.eclipse.collections.impl.factory.Maps
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class StatisticsCollector {
    private static Logger logger = LoggerFactory.getLogger(StatisticsCollector.class)
    private static WORD_REGEX = "\\s+"
    private static NON_LETTERS = "^[^a-zA-Z0-9\\\\s]+|[^a-zA-Z0-9\\\\s]+\$"
    public static final int INITIAL_CAPACITY = 16000
    private long occurenceOfAllWords = 0d;
    public static final int MAXIMUM_LEVENSTEIN_DISTANCE = 3
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
                .forEach { word ->
            addWordToMap(word)
            occurenceOfAllWords++
        }
        logger.info("Finished creating dictionary from file: {}", inputFile.getAbsolutePath())
    }


    File getFileWithFixedWords(File inputFile) {
        def outputFile = new File("output.txt")
        outputFile.createNewFile()
        def fixedWords = fixWords(inputFile)
        fixedWords.each {
            k, v -> outputFile.append("$k;$v\n\r")
        }
        outputFile
    }


    private Map<String, String> fixWords(File inputFile) {
        def wordsToFix = Lists.mutable.empty()
        def fixedWords = new ConcurrentHashMap<String, String>(INITIAL_CAPACITY)
        inputFile.eachLine {
            el -> wordsToFix.add(el.split(";")[0].toUpperCase())
        }

        wordsToFix.parallelStream().each {
            el -> fixedWords.put(el as String, correctWord(el as String))
        }
        fixedWords
    }

    private String correctWord(String wordToCorrect) {
        List<String> similarWords = findSimilarWords(wordToCorrect)

        double maximumChance = 0d
        def mostChanceCandidate = "";

        for (String similarWord : similarWords) {
            double chance = (double) wordToOccurrence.get(similarWord) / (double) occurenceOfAllWords
            if (chance > maximumChance) {
                maximumChance = chance
                mostChanceCandidate = similarWord
            }
        }

        logger.info("Correction for $wordToCorrect found: $mostChanceCandidate", wordToCorrect)
        return mostChanceCandidate
    }

    private List<String> findSimilarWords(String wordToCorrect) {
        def similarWords = findWordSimilarInLevensteinMetrics(wordToCorrect, MAXIMUM_LEVENSTEIN_DISTANCE)
//        def i = 1
//        while (similarWords.size() == 0) {
//            similarWords = findWordSimilarInLevensteinMetrics(wordToCorrect, MAXIMUM_LEVENSTEIN_DISTANCE + 1)
//            i++
//        }
        similarWords
    }

    private void addWordToMap(String word) {
        if (wordToOccurrence.containsKey(word)) {
            wordToOccurrence.put(word.toUpperCase(), wordToOccurrence.get(word) + 1)
        } else {
            wordToOccurrence.put(word.toUpperCase(), 1)
        }
    }


    private List<String> findWordSimilarInLevensteinMetrics(String word, int n) {
        def similarWords = Lists.mutable.empty()
        for (String element : wordToOccurrence.keySet()) {
            LDCounter ldCounter = new LDCounter()
            int levensteinMetrics = LevensteinMetrics.calculateDistance(element, word, n)
//            int levensteinMetrics = ldCounter.getLD(element, word)
            if (levensteinMetrics < n) {
                similarWords.add(element)
            }
        }
        similarWords
    }

}
