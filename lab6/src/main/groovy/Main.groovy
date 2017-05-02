import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tfidf.TFIDFHelper

import static FileReader.generateStopList
import static FileReader.readFileIntoDocuments

class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)
    private static final THRESHOLD = 0.005d

    static void main(String[] args) {
        logger.info("Starting app...")
        List<List<String>> sliced = createTermsForDocuments()
        Set<String> stopList = createStopList(sliced)

        def toTest = "Mariusz Czerkawski strzelił dwie bramki i przy dwóch asystował\n" +
                "w wygranym przez jego zespół New York Islanders meczu hokejowej\n" +
                "ligi NHL z Tampa Bay Lightning 5:4."
        TFIDFHelper.tfidfTest(sliced, stopList, toTest)

//        GraphHelper.test(sliced, stopList, toTest, 5)
        logger.info("Finishing reading file from documents")
    }

    private static createTermsForDocuments() {
        def sliced = readFileIntoDocuments("src/main/resources/pap_trimmed.txt")
        logger.info("Finished reading files into arrays with terms")
        sliced
    }

    private static createStopList(List<List<String>> sliced) {
        def stopList = generateStopList(sliced, THRESHOLD)
        logger.info("Finished creating stoplist")
        stopList
    }




}
