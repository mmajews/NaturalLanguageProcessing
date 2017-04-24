import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static FileReader.generateStopList
import static FileReader.readFileIntoDocuments
import static tfidf.TFIDFHelper.tfidfTest

class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)
    private static final THRESHOLD = 0.005d

    static void main(String[] args) {
        logger.info("Starting app...")
        List<List<String>> sliced = createTermsForDocuments()
        Set<String> stopList = createStopList(sliced)

        def toTest = "Jugosłowiański minister informacji Goran Matić oskarżył\n" +
                "zachodnie służby wywiadowcze o zorganizowanie niedawnego\n" +
                "zabójstwa ministra obrony Pavle Bulatovicia."
        tfidfTest(sliced, stopList, toTest)
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
