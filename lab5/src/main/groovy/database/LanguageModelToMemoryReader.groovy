package database

import com.google.common.base.Preconditions
import groovy.time.TimeCategory
import org.apache.commons.lang3.NotImplementedException
import org.eclipse.collections.impl.factory.Maps
import org.slf4j.LoggerFactory

class LanguageModelToMemoryReader {
    private static final def logger = LoggerFactory.getLogger(LanguageModelToMemoryReader.class)
    private static final String UNI_GRAMS = "1"
    private static final String DI_GRAMS = "2"
    private static final String TRI_GRAMS = "3"
    private static final String TAB_REGEX = "\t"
    private static final String N_GRAM_START_REGES = ".*\\d-grams:.*"
    private static final String EVERYTHING_BUT_NUMBERS_REGEX = "\\D+"

    def probabilityMap = Maps.mutable.of(UNI_GRAMS, Maps.mutable.empty(), DI_GRAMS, Maps.mutable.empty(), TRI_GRAMS, Maps.mutable.empty())

    LanguageModelToMemoryReader(String pathToLanguageModel) {
        def fileWithLanguageModel = new File(pathToLanguageModel)
        Preconditions.checkArgument(fileWithLanguageModel.exists(), "File with path:$pathToLanguageModel does not exists")
        logger.info("Reading language model from $pathToLanguageModel")
        initializeMap(fileWithLanguageModel)
    }

    def initializeMap(File inputFile) {
        def lineCounter = 0
        def stopCondition = 150000

        def start = new Date()
        def currentGrams = ""

        try {
            inputFile.eachLine { line ->
                lineCounter++
                if (line.matches(N_GRAM_START_REGES)) {
                    currentGrams = line.split("-regex")[0].replaceAll(EVERYTHING_BUT_NUMBERS_REGEX, "")
                    logger.info("Found starting pattern for $currentGrams")
                } else if (currentGrams != "") {
                    processLineWithProbability(line, currentGrams)
                }
                if (currentGrams == DI_GRAMS) {
                    throw new NotImplementedException("Breaked - temporary solution")
                }
            }
        } catch (NotImplementedException ex) {
            //fixme temporary solution -> read only 1-grams
        }

        def end = new Date()
        def diff = TimeCategory.minus(end, start)
        logger.info("Process of reading model took: $diff.ago.seconds seconds")
    }

    private void processLineWithProbability(String line, String currentGrams) {
        if(currentGrams != TRI_GRAMS){
            return
        }

        def elements = line.split(TAB_REGEX)
        if (elements.size() != 3 && elements.size() != 2) {
            return
        }

        Preconditions.checkArgument(elements.size() == 2 || elements.size() == 3, "Size of $line is not 3 or 2!")

        def map = probabilityMap.get(currentGrams);
        def element = elements[1].toUpperCase()
        if (elements.size() == 3) {
            map.put(element, new ProbOfElem(probability: elements[0] as double, backOffValue: elements[2] as double))
        } else {
            map.put(element, new ProbOfElem(probability: elements[0] as double))
        }
        probabilityMap.put(currentGrams, map)
    }

    class ProbOfElem {
        double probability
        double backOffValue
    }
}
