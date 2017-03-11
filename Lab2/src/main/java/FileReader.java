import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class FileReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    public static String getStringFromFile(File file) {
        Preconditions.checkArgument(file.exists(), "File {} does not exists", file.getAbsolutePath());
        logger.info("Reading content from file: {}", file.getAbsolutePath());
        String contentOfFile = null;
        try {
            contentOfFile = FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Exception while reading from file: {} ", file.getAbsolutePath(), e);
        }
        return contentOfFile;
    }

}
