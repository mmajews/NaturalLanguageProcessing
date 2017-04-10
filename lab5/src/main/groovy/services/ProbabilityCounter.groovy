package services

import database.MongoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProbabilityCounter {
    private static final Logger logger = LoggerFactory.getLogger(ProbabilityCounter.class)

    static double getNGramModelProbability(String input, Integer nGram, MongoService mongoService) {
        double probability = 0d;
//        logger.info("Getting probability of $input for ngrams: $nGram")
        def slicedInput = input.toUpperCase().split("\\s+")
        def allNGrams = []
        def ngram = nGram as Integer

        for (def i = 0; ngram + i <= slicedInput.size(); i++) {
            def currWord = ""
            for (def j = 0; j < ngram; j++) {
                currWord += slicedInput[j + i] + " "
            }
            allNGrams.add(currWord)
        }

        allNGrams.each {
            it -> probability += mongoService.getProbabilityOfElement(it as String, ngram)
        }

        return probability
    }

}
