import database.LanguageModelToDatabaseReader
import database.MongoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import services.ProbabilityCounter
import services.ProbabilityFinder

class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class)
    private static boolean INITIALIZE_DATABASE = false
    private static NGRAM_USE = 3
    static String PATH_TO_LANGUAGE_MODEL = "/Users/mmajewski/Development/Studies/NaturalLanguageProcessing/lab5/polish_3gm_knigi_kn.lm"
    static String PATH_TO_ACOUSTIC = "/Users/mmajewski/Development/Studies/NaturalLanguageProcessing/lab5/src/main/resources/acoustic.csv"

    static void main(String[] args) {
        logger.info("Starting app...")
        if (INITIALIZE_DATABASE) {
            def languageModel = new LanguageModelToDatabaseReader(PATH_TO_LANGUAGE_MODEL)
        }
        getProbabilityOfAcoustic()
    }

    private static getProbabilityOfAcoustic() {
        ProbabilityFinder probabilityFinder = new MongoService()
        (PATH_TO_ACOUSTIC as File).eachLine {
            it ->
                def line = it.split(",")
                def value = ProbabilityCounter.getNGramModelProbability(line[2], NGRAM_USE, probabilityFinder)
                println value
        }
    }
}
