package pl.agh.edu.nlp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Main {

	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) {
		logger.info("Starting app !");

		final ClassLoader classLoader = Main.class.getClassLoader();
		Dictionary dictionary = new Dictionary(new File(classLoader.getResource("odm.txt").getFile()));

		String input = classLoader.getResource("potop.txt").getFile();
		StatisticsCollector statisticsCollector = new StatisticsCollector(new File(input), dictionary);
		statisticsCollector.createFileWithRanking(new File("ranking.txt"));

	}
}
