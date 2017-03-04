package nlp.lab1.ngram.statistics;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

class FileReader {

	private static final Logger logger = Logger.getLogger(FileReader.class.getName());

	static String getContentOfFile(File sourceFile){
		Preconditions.checkArgument(sourceFile.exists(), "File does not exist!");
		String wholeFile = null;
		try {
			wholeFile = FileUtils.readFileToString(sourceFile, "UTF-8");
		} catch (IOException e) {
			logger.warning("Exception during reading from file to string !");
		}
		return wholeFile;
	}


}
