package database

import com.google.common.base.Preconditions
import groovy.time.TimeCategory
import org.slf4j.LoggerFactory
import services.ProbOfElem

class LanguageModelToDatabaseReader {
    private def logger = LoggerFactory.getLogger(LanguageModelToDatabaseReader.class)
    private static final String UNI_GRAMS = "1"
    private static final String DI_GRAMS = "2"
    private static final String TRI_GRAMS = "3"
    private static final String TAB_REGEX = "\t"
    private static final String N_GRAM_START_REGES = ".*\\d-grams:.*"
    private static final String EVERYTHING_BUT_NUMBERS_REGEX = "\\D+"
    private final MongoService mongoService = new MongoService();

    LanguageModelToDatabaseReader(String pathToFile) {
        def fileWithLanguageModel = new File(pathToFile)
        Preconditions.checkArgument(fileWithLanguageModel.exists(), "File with path:$pathToFile does not exists")
        logger.info("Reading language model from $pathToFile")
        initializeDatabase(pathToFile as File)
    }


    def initializeDatabase(File inputFile) {
        def start = new Date()
        def currentGrams = ""

        inputFile.eachLine { line ->
            if (line.matches(N_GRAM_START_REGES)) {
                currentGrams = line.split("-regex")[0].replaceAll(EVERYTHING_BUT_NUMBERS_REGEX, "")
                logger.info("Found starting pattern for $currentGrams")
            } else if (currentGrams != "") {
                processLineWithProbability(line, currentGrams)
            }
        }

        def end = new Date()
        def diff = TimeCategory.minus(end, start)
        logger.info("Process of reading model took: $diff.ago.seconds seconds")
    }

    private void processLineWithProbability(String line, String currentGrams) {
        def elements = line.split(TAB_REGEX)

        if (elements.size() != 3 && elements.size() != 2) {
            return
        }

        Preconditions.checkArgument(elements.size() == 2 || elements.size() == 3, "Size of $line is not 3 or 2!")
        def element = elements[1].toUpperCase()
        ProbOfElem elem;
        if (elements.size() == 3) {
            elem = new ProbOfElem(probability: elements[0] as double, backOffValue: elements[2] as double, nGram: currentGrams, value: elements[1])
        } else {
            elem = new ProbOfElem(probability: elements[0] as double, nGram: currentGrams, value: elements[1])
        }

        mongoService.saveProbOfElement(elem, currentGrams as Integer)
    }


}
