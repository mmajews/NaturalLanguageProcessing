import database.LanguageModelToDatabaseReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class)
    private static final String PATH_TO_LANGUAGE_MODEL = "C:\\Development\\Repos\\NaturalLanguageProcessing\\lab5\\polish_3gm_knigi_kn.lm"

    static void main(String[] args) {
        logger.info("Starting app...")
        def languageModel = new LanguageModelToDatabaseReader(PATH_TO_LANGUAGE_MODEL)
//        ProbabilityFinder mongoService = new MongoService()
//        print ProbabilityCounter.getNGramModelProbability("dwa zbliżone wyniki z", "3", mongoService)
    }
}
