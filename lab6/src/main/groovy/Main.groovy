import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)
    private static final THRESHOLD = 0.005d

    static void main(String[] args) {
        logger.info("Starting app...")
        def sliced = FileReader.readFileIntoDocuments("src/main/resources/pap.txt")
        def stopList = FileReader.generateStopList(sliced, THRESHOLD)
        logger.info("Finishing reading file from documents")
    }

}
