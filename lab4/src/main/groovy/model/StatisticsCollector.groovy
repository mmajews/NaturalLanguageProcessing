package model

import com.google.common.base.Preconditions
import metrics.LevensteinCounter
import org.eclipse.collections.impl.factory.Lists
import org.eclipse.collections.impl.factory.Maps
import org.eclipse.collections.impl.map.mutable.ConcurrentHashMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicInteger

class StatisticsCollector {
    private static def counter = new AtomicInteger(0);
    private static Logger logger = LoggerFactory.getLogger(StatisticsCollector.class)
    private static WORD_REGEX = "\\s+"
    private static NON_LETTERS = "^[^a-zA-Z0-9\\\\s]+|[^a-zA-Z0-9\\\\s]+\$"
    public static final int INITIAL_CAPACITY = 16000
    private long occurenceOfAllWords = 0d;
    public static final double MAXIMUM_LEVENSTEIN_DISTANCE = 1.5
    private def wordToOccurrence = Maps.mutable.empty();

    void appendFileToDictionary(File inputFile) {
        Preconditions.checkArgument(inputFile.exists(), "Input file: {} does not exist", inputFile.getAbsolutePath())
        def inputText = inputFile.text
        inputText.replaceAll(NON_LETTERS, " ")
        Arrays.asList(inputText.split(WORD_REGEX))
                .stream()
                .map { el -> el.toUpperCase() }
                .map { el -> el.trim() }
//                .map { el -> el.replaceAll(NON_LETTERS, "") }
//                .filter { el -> StringUtils.isAlpha(el) }
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

        def listOfLines = Lists.mutable.empty()

        for (def entry : fixedWords.entrySet()) {
            listOfLines.add(entry.key + ";" + entry.value + "\n\r")
        }
        listOfLines.sort().each {
            line -> outputFile.append(line)
        }
        outputFile
    }


    private Map<String, String> fixWords(File inputFile) {
        def wordsToFix = Lists.mutable.empty()
        def fixedWords = new ConcurrentHashMap<String, String>(INITIAL_CAPACITY)
        inputFile.eachLine {
            el -> wordsToFix.add(el.split(";")[0].toUpperCase())
        }

        wordsToFix.parallelStream().forEach({
            el ->
                fixedWords.put(el as String, correctWord(el as String))
                counter.incrementAndGet()
                def sizeOfMap = wordsToFix.size()
                logger.info("Progress $counter / $sizeOfMap")
//                println Thread.currentThread().id
        })
        fixedWords
    }

    private String correctWord(String wordToCorrect) {
        def similarWords = findWordSimilarInLevensteinMetrics(wordToCorrect, MAXIMUM_LEVENSTEIN_DISTANCE)


        double maximumChance = 0d
        def mostChanceCandidate = "";

        for (Object similarWord : similarWords) {
            def word = similarWord.getAt("element") as String
            def chance = getWordChance(word)
            if (chance > maximumChance) {
                maximumChance = chance
                mostChanceCandidate = word
            }
        }

        mostChanceCandidate == "" ? wordToCorrect : mostChanceCandidate
//        logger.info("Correction for $wordToCorrect found: $mostChanceCandidate", wordToCorrect)
        mostChanceCandidate
    }

    private getWordChance(String similarWord) {
        (double) wordToOccurrence.get(similarWord) / (double) occurenceOfAllWords
    }

    private void addWordToMap(String word) {
        if (wordToOccurrence.containsKey(word)) {
            wordToOccurrence.put(word.toUpperCase(), wordToOccurrence.get(word) + 1)
        } else {
            wordToOccurrence.put(word.toUpperCase(), 1)
        }
    }


    private List findWordSimilarInLevensteinMetrics(String word, double n) {
        def similarWords = Collections.synchronizedList(Lists.mutable.empty())

        wordToOccurrence.keySet().parallelStream().forEach({ element ->
//            println Thread.currentThread().id
            def ldCounter = new LevensteinCounter()
            double levensteinMetrics = ldCounter.getLD(element as String, word, 1)
            if (levensteinMetrics < n) {
                similarWords.add([element: element, metrics: levensteinMetrics])
            }
        }
        )
        similarWords
    }

}
