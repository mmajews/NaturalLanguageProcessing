import database.LanguageModelToDatabaseReader
import database.MongoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import services.ProbabilityCounter
import services.ProbabilityFinder

class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class)
    private static final String PATH_TO_LANGUAGE_MODEL = "C:\\Development\\Repos\\NaturalLanguageProcessing\\lab5\\polish_3gm_knigi_kn.lm"

    static void main(String[] args) {
        logger.info("Starting app...")
//        def languageModel = new LanguageModelToDatabaseReader(PATH_TO_LANGUAGE_MODEL)
        ProbabilityFinder probabilityFinder = new MongoService()
        print ProbabilityCounter.getNGramModelProbability("dwa zbliżone wyniki z", "2", probabilityFinder)
    }
}
