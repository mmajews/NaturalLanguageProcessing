import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.vandermeer.asciitable.v2.RenderedTable;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.V2_AsciiTableRenderer;
import de.vandermeer.asciitable.v2.render.WidthLongestLine;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
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

        List<ArrayList<String>> LCSClusters = testMetrics(new File("LCS.output"), allElements, new LCSMetrics(), 5, 0.10);
        logger.info("Clusters for LSC done");
        List<ArrayList<String>> LevenshteinClusters = testMetrics(new File("Levenshtein.output"), allElements, new LevenshteinMetrics(), 5, 10);
        logger.info("Clusters for Levenshtein done");

        printStatistics(LCSClusters, new LCSMetrics());
        printStatistics(LevenshteinClusters, new LevenshteinMetrics());

        logger.info("Testing done!");
    }

    private static void printStatistics(List<ArrayList<String>> clusters, DistanceMetric<String> lcsMetrics) {
        int numberOfCluster = clusters.size();
        double averageClusterSize = (double) clusters.stream().map(ArrayList::size).mapToInt(el -> el).sum() / (double) clusters.size();
        int maximumSizeOfCluster = clusters.stream().map(ArrayList::size).mapToInt(el -> el).max().getAsInt();
        double dunnIndex = countDunnIndex(clusters, lcsMetrics);

        V2_AsciiTable at = new V2_AsciiTable();
        at.addRule();
        at.addRow("METRICS", "NUMBER OF CLUSTERS", "AVG CLUSTER SIZE", "MAX SIZE OF CLUSTER", "DUNN INDEX");
        at.addRule();
        at.addRow(lcsMetrics.getClass().getName(), numberOfCluster, averageClusterSize, maximumSizeOfCluster, dunnIndex);
        at.addRule();
        V2_AsciiTableRenderer rend = new V2_AsciiTableRenderer();
        rend.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
        rend.setWidth(new WidthLongestLine());
        RenderedTable rt = rend.render(at);
        System.out.println(rt);
    }

    private static double countDunnIndex(List<ArrayList<String>> clusters, DistanceMetric<String> distanceMetrics) {
        double maximumDiameterOfCluster = 0d;

        for (ArrayList<String> cluster : clusters) {
            double diameter = getDiameterOfCluster(cluster, distanceMetrics);
            maximumDiameterOfCluster = diameter > maximumDiameterOfCluster ? diameter : maximumDiameterOfCluster;
        }

        double minimumDistanceBetweenClusters = getMinimumDistanceBetweenClusters(clusters, distanceMetrics);
        return minimumDistanceBetweenClusters / maximumDiameterOfCluster;
    }

    private static double getMinimumDistanceBetweenClusters(List<ArrayList<String>> clusters, DistanceMetric<String> distanceMetric) {
        double minimumDistance = Double.MAX_VALUE;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                double currMin = getMinimumDistanceBetweenTwoCluster(clusters.get(i), clusters.get(j), distanceMetric);
                minimumDistance = currMin < minimumDistance ? currMin : minimumDistance;
            }
        }
        return minimumDistance;
    }

    private static double getMinimumDistanceBetweenTwoCluster(ArrayList<String> cluster1, ArrayList<String> cluster2, DistanceMetric<String> distanceMetric) {
        double minimum = Double.MAX_VALUE;
        try {
            for (int i = 0; i < cluster1.size(); i++) {
                for (int j = i + 1; j < cluster2.size(); j++) {
                    double calculateDistance = distanceMetric.calculateDistance(cluster1.get(i), cluster2.get(j));
                    minimum = calculateDistance < minimum ? calculateDistance : minimum;
                }
            }
        } catch (DBSCANClusteringException e) {
            e.printStackTrace();
        }
        return minimum;
    }

    private static double getDiameterOfCluster(ArrayList<String> cluster, DistanceMetric<String> distanceMetrics) {
        double maximum = 0d;
        try {
            for (int i = 0; i < cluster.size(); i++) {
                for (int j = i + 1; j < cluster.size(); j++) {
                    double calculateDistance = distanceMetrics.calculateDistance(cluster.get(i), cluster.get(j));
                    maximum = calculateDistance > maximum ? calculateDistance : maximum;
                }
            }
        } catch (DBSCANClusteringException e) {
            e.printStackTrace();
        }
        return maximum;
    }

    private static List<ArrayList<String>> testMetrics(File outputFile, List<String> allElements, DistanceMetric<String> distanceMetric, int minNElements, double maxDistance) {
        DBSCANClusterer<String> clusterer;
        List<ArrayList<String>> clusters = null;

        try {
            clusterer = new DBSCANClusterer<>(allElements, minNElements, maxDistance, distanceMetric);
            clusters = clusterer.performClustering();
        } catch (DBSCANClusteringException error) {
            logger.error("Error while clustering!", error);
        }

        outputToFile(outputFile, clusters);
        return clusters;
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
