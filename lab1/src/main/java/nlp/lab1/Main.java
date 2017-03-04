package nlp.lab1;

public class Main {
	public static void main(String[] args) {
		AllLanguageStatistics allLanguageStatistics = new AllLanguageStatistics();
		allLanguageStatistics.generateStatistics();

		String toBeDetected = args[0];
		System.out.println("Finding language that is good fit for sentence : \"" + toBeDetected + "\"");

		System.out.println();
	}

}
