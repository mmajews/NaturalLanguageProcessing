import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import metrics.LCSMetrics;
import metrics.LevenshteinMetrics;
import org.apache.commons.io.FileUtils;
import org.christopherfrantz.dbscan.DBSCANClusterer;
import org.christopherfrantz.dbscan.DBSCANClusteringException;
import org.christopherfrantz.dbscan.DistanceMetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class Clusterizer {
    private static final Logger logger = LoggerFactory.getLogger(Clusterizer.class);
    private static List<String> stopList = Lists.newArrayList("NIP", "STR", "SP.Z O.O.", "P.O.BOX", "LTD", "LLC", "TEL", "[-+.^:,\"']");

    private static String processElement(String element) {
        for (String stopEl : stopList) {
            element = element.replaceAll(stopEl, "");
        }
        return element;
    }

    static void clusterize(File file, String regexSeparator) {
        logger.info("Creating clusters from content of file: {}", file.getAbsolutePath());
        List<String> allElements = getSlicedContentBySep(file, regexSeparator).stream()
                .map(Clusterizer::processElement)
                .collect(Collectors.toList());
        logger.info("Number of elements: {}", allElements.size());

        testMetrics(new File("/Users/mmajewski/LCS.output"), allElements, new LCSMetrics(), 5, 0.10);
        testMetrics(new File("/Users/mmajewski/Levenshtein.output"), allElements, new LevenshteinMetrics(), 5, 10);

        logger.info("Testing done!");
    }

    private static void testMetrics(File outputFile, List<String> allElements, DistanceMetric<String> distanceMetric, int minNElements, double maxDistance) {
        DBSCANClusterer<String> clusterer;
        List<ArrayList<String>> clusters = null;

        try {
            clusterer = new DBSCANClusterer<>(allElements, minNElements, maxDistance, distanceMetric);
            clusters = clusterer.performClustering();
        } catch (DBSCANClusteringException error) {
            logger.error("Error while clustering!", error);
        }

        outputToFile(outputFile, clusters);
    }

    private static void outputToFile(File outputFile, List<ArrayList<String>> clusters) {
        try {
            FileReaderUtils.createNewFileIfNotExists(outputFile);
            FileReaderUtils.clearFile(outputFile);

            String output = "";
            for (ArrayList<String> cluster : clusters) {
                output += "#########################";
                output += System.lineSeparator();
                for (String string : cluster) {
                    output += string;
                }
            }

            FileUtils.writeStringToFile(outputFile, output);
        } catch (IOException ex) {
            logger.error("Error while writing output to file!", ex);
        }
    }

    private static List<String> getSlicedContentBySep(File file, String regexSeparator) {
        String content = FileReaderUtils.getStringFromFile(file);
        Preconditions.checkNotNull(content, "Content from file : {} null", file.getAbsolutePath());
        List<String> allElements;
        allElements = Arrays.asList(content.split(regexSeparator));
        return allElements;
    }
}
