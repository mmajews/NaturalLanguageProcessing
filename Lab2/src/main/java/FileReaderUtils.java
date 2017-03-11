import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class FileReaderUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderUtils.class);

    public static String getStringFromFile(File file) {
        Preconditions.checkArgument(file.exists(), "File {} does not exists", file.getAbsolutePath());
        logger.info("Reading content from file: {}", file.getAbsolutePath());
        String contentOfFile = null;
        try {
            contentOfFile = org.apache.commons.io.FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Exception while reading from file: {} ", file.getAbsolutePath(), e);
        }
        return contentOfFile;
    }

    public static void clearFile(File outputFile) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(outputFile);
        writer.close();
    }

    static void createNewFileIfNotExists(File outputFile) throws IOException {
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
    }
}
