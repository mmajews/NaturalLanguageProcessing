package nlp.lab1.ngram.statistics;

import com.google.common.collect.Lists;

import java.util.List;

public class LanguageConstants {
	public static final String ENGLISH = "English";
	public static final String FINNISH = "Finnish";
	public static final String GERMAN = "German";
	public static final String ITALIAN = "Italian";
	public static final String POLISH = "Polish";
	public static final String SPANISH = "Spanish";
	public static List<String> allLanguages = Lists.newArrayList(ENGLISH, FINNISH, GERMAN, ITALIAN, POLISH, SPANISH);
//	public static List<String> allLanguages = Lists.newArrayList(POLISH);
}
