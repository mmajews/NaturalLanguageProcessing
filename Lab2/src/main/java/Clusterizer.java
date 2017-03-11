import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clusterizer {
    private static final Logger logger = LoggerFactory.getLogger(Clusterizer.class);

    public static void clusterize(File file, String regexSeparator) {
        logger.info("Creating clusters from content of file: {}", file.getAbsolutePath());
        List<String> allElements = getSlicedContentBySep(file, regexSeparator);
        logger.info("Number of elements: {}", allElements.size());


    }

    private static List<String> getSlicedContentBySep(File file, String regexSeparator) {
        String content = FileReader.getStringFromFile(file);
        Preconditions.checkNotNull(content, "Content from file : {} null", file.getAbsolutePath());
        List<String> allElements = new ArrayList<>();
        allElements = Arrays.asList(content.split(regexSeparator));
        return allElements;
    }
}
