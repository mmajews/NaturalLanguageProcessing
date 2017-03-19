package pl.agh.edu.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.agh.edu.nlp.model.Dictionary;
import pl.agh.edu.nlp.services.StatisticsCollector;

import java.io.File;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("Starting app !");

		final ClassLoader classLoader = Main.class.getClassLoader();
		Dictionary dictionary = new Dictionary(new File(classLoader.getResource("odm.txt").getFile()));

		String input = classLoader.getResource("potop.txt").getFile();
		StatisticsCollector statisticsCollector = new StatisticsCollector(new File(input), dictionary);
		final String outputPath = "src\\main\\resources\\output\\";
		statisticsCollector.createFileWithRanking(new File("ranking.txt"));
		statisticsCollector.getCumulativePercentageDataForZipfLaw(new File("zipfLawPercentage.txt"));
		logger.info("Hapax legonema: {}", statisticsCollector.getHapaxLegonema());
		logger.info("Number of words that are 50% counting from the top of ranked list: {}",
				statisticsCollector.get50PercentageFromTop());
		logger.info("Number of words that are 50% counting from the botton of ranked list: {}",
				statisticsCollector.get50PercentageFromBottom());
		statisticsCollector.getOutputRankingListNGrams(new File("outputDiGrams.txt"), new File("outputTriGrams.txt"));
	}
}
