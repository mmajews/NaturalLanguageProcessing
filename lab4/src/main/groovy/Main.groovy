import model.StatisticsCollector
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    static void main(String[] args) {
        logger.info("Starting app...")
        StatisticsCollector statisticsCollector = new StatisticsCollector();
        final resourcesPath = "C:\\Development\\Repos\\NaturalLanguageProcessing\\lab4\\src\\main\\resources"
        resourcesPath.normalize()
        statisticsCollector.appendFileToDictionary(new File((resourcesPath + "/dramat.txt").normalize()))
        statisticsCollector.appendFileToDictionary(new File((resourcesPath + "/formy.txt").normalize()))
        statisticsCollector.appendFileToDictionary(new File((resourcesPath + "/popul.txt").normalize()))
        statisticsCollector.appendFileToDictionary(new File((resourcesPath + "/proza.txt").normalize()))
        statisticsCollector.appendFileToDictionary(new File((resourcesPath + "/publ.txt").normalize()))
        statisticsCollector.appendFileToDictionary(new File((resourcesPath + "/wp.txt").normalize()))

        statisticsCollector.getFileWithFixedWords(new File((resourcesPath + "/bledy.txt").normalize()))
    }
}
