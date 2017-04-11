package services

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProbabilityCounter {
    private static final Logger logger = LoggerFactory.getLogger(ProbabilityCounter.class)

    static double getNGramModelProbability(String input, Integer nGram, ProbabilityFinder mongoService) {
        double probability = 0d
        logger.debug("Getting probability of $input for ngrams: $nGram")
        List allNGrams = slicedToNgrams(input, nGram)

        allNGrams.each {
            it ->
                def prob = mongoService.getProbabilityOfElement(it as String, nGram)
                probability += Optional.ofNullable(prob).orElse(getProbabilityUsingBackOffValue(it as String, nGram - 1, mongoService))
        }

        return Math.pow(10, probability)
    }

    private static slicedToNgrams(String input, int nGram) {
        def slicedInput = splitByWhitespace(input)
        def allNGrams = []

        for (def i = 0; nGram + i <= slicedInput.size(); i++) {
            def currWord = ""
            for (def j = 0; j < nGram; j++) {
                currWord += slicedInput[j + i] + " "
            }
            allNGrams.add(currWord.trim())
        }
        allNGrams
    }

    private static splitByWhitespace(String input) {
        input.toUpperCase().split("\\s+")
    }

    private static double getProbabilityUsingBackOffValue(String input, Integer nGram, ProbabilityFinder mongoService) {
        double probability = 0d
        List allNgrams = slicedToNgrams(input, nGram)
        def first = allNgrams[0]
        def second = allNgrams[1]
        def firstProb = mongoService.getProbabilityOfElement(first as String, nGram)
        def secondBackOff = mongoService.getBackOffValue(second as String, nGram)

        if (nGram == 2) {
            if (firstProb == null || secondBackOff == null) {
                def prob1 = getProbabilityUsingBackOffValue(first as String, 1, mongoService)
                def prob2 = getProbabilityUsingBackOffValue(second as String, 1, mongoService)
                return prob1 + prob2
            }
            return firstProb * secondBackOff
        } else if (nGram == 1) {
            return Optional.ofNullable(firstProb).orElse(0d) * Optional.ofNullable(secondBackOff).orElse(0d)
        }

        return probability
    }


}
