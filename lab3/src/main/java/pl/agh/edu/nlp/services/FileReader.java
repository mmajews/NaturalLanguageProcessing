package pl.agh.edu.nlp.services;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FileReader {

	private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

	public static String getContentOfFile(File file) {
		Preconditions.checkNotNull(file, "File cannot be null");

		final String absolutePath = file.getAbsolutePath();
		Preconditions.checkArgument(file.exists(), "File : {} does not exist", absolutePath);

		try {
			return FileUtils.readFileToString(file, "UTF-8");
		} catch (IOException e) {
			logger.error("Cannot read file: {}", absolutePath);
		}
		return null;
	}
}
