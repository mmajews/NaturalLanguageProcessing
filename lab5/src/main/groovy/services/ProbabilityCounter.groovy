package services

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProbabilityCounter {
    private static final Logger logger = LoggerFactory.getLogger(ProbabilityCounter.class)
    private LanguageModel languageModel

    double getNGramModelProbability(String input, String nGram) {
        logger.info("Getting probability")
        def mapOfProbabilities = languageModel.getProbabilityMap().get(nGram)
        def slicedInput = input.toUpperCase().split("\\s+")

        for (def i = 0; i + 1 < slicedInput.size(); i++) {
            def j = i+1
            def diGram = slicedInput[i] + " " + slicedInput[j]
        }

    }

}
