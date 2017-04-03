import org.slf4j.Logger
import org.slf4j.LoggerFactory
import services.LanguageModel
import services.ProbabilityCounter

class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class)
    private static final String PATH_TO_LANGUAGE_MODEL = "C:\\Development\\Repos\\NaturalLanguageProcessing\\lab5\\polish_3gm_knigi_kn.lm"

    static void main(String[] args) {
        logger.info("Starting app...")
        def languageModel = new LanguageModel(PATH_TO_LANGUAGE_MODEL)
        def probabilityCounter = new ProbabilityCounter(languageModel: languageModel)
        print probabilityCounter.getNGramModelProbability("dwa zbliżone wyniki z", "1")
    }
}
