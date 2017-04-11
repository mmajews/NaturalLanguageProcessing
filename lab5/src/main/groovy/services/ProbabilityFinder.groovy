package services


interface ProbabilityFinder {
    Double getProbabilityOfElement(String value, Integer nGram)

    Double getBackOffValue(String value, Integer nGram)
}