import database.MongoService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import services.ProbabilityCounter
import services.ProbabilityFinder

class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class)
    private static NGRAM_USE = 3
    final String PATH_TO_LANGUAGE_MODEL = "C:\\Development\\Repos\\NaturalLanguageProcessing\\lab5\\polish_3gm_knigi_kn.lm"
    final
    static String PATH_TO_ACOUSTIC = "C:\\Development\\Repos\\NaturalLanguageProcessing\\lab5\\src\\main\\resources\\acoustic.csv"

    static void main(String[] args) {
        logger.info("Starting app...")
//        def languageModel = new LanguageModelToDatabaseReader(PATH_TO_LANGUAGE_MODEL)
        ProbabilityFinder probabilityFinder = new MongoService()
        def input = "ale wciąż w pierwsze"

        PATH_TO_ACOUSTIC.asType(File.class).eachLine {
            it ->
                def line = it.split(",")
                def value = ProbabilityCounter.getNGramModelProbability(line[2], NGRAM_USE, probabilityFinder)
                println value
        }
    }
}
