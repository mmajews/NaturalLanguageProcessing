import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import metrics.LCSMetrics;
import metrics.TwoElements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Clusterizer {
    private static final Logger logger = LoggerFactory.getLogger(Clusterizer.class);
    private static List<String> stopList = Lists.newArrayList("NIP", "STR", "SP.Z O.O.", "P.O.BOX", "LTD", "LLC", "TEL", "[-+.^:,\"']");

    private static String processElement(String element) {
        for (String stopEl : stopList) {
            element = element.replaceAll(stopEl, "");
        }
        return element;
    }

    public static void clusterize(File file, String regexSeparator) {
        logger.info("Creating clusters from content of file: {}", file.getAbsolutePath());
        List<String> allElements = getSlicedContentBySep(file, regexSeparator);


        logger.info("Number of elements: {}", allElements.size());

        List<Element> listOfElements = allElements
                .stream()
                .map(Clusterizer::processElement)
                .map(Element::new)
                .collect(Collectors.toList());
        logger.info("Done creating list of elements!");

        int index = 0;
        Map<Integer, String> mapOfElements = new LinkedHashMap<>();
        for (Element element : listOfElements) {
            mapOfElements.put(index, element.getString());
            element.setIndex(index);
            index++;
        }
        logger.info("Done creating map of elements!");

        AtomicInteger atomicInteger = new AtomicInteger(0);
        Integer totalNumberOfElements = listOfElements.size();
        Thread countingThread = new Thread(() -> {
            LocalDateTime startTime = LocalDateTime.now();
            while (true) {
                final double percentage = (double) atomicInteger.get() / (double) totalNumberOfElements;
                logger.info("Current progress : {} %", percentage * 100);
                logger.info("Already lasted: {} minutes", ChronoUnit.MINUTES.between(startTime, LocalDateTime.now()));
                try {
                    Thread.sleep(10000l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        ConcurrentHashMap<TwoElements, Double> distances = new ConcurrentHashMap<>();
        generateDistancesBetweenPairs(listOfElements, mapOfElements, atomicInteger, countingThread, distances);

        double avgDistBetweenClust = distances.values().stream().mapToDouble(e -> e).sum() / (double) distances.size();
        List<Set<Integer>> clusters = getClusters(avgDistBetweenClust, distances);

        logger.info("Done counting distances to other elements!");
    }


    private static List<Set<Integer>> getClusters(double avgDistBetweenClust, ConcurrentHashMap<TwoElements, Double> distances) {
        logger.info("Starting with clusters");
        List<Set<Integer>> clusters = new ArrayList<>();
        Set<Integer> alreadyInSomeCluster = new HashSet<>();

        for (Map.Entry<TwoElements, Double> entry : distances.entrySet()) {
            TwoElements twoElements = entry.getKey();
            Integer firstElement = twoElements.getFirst();
            Integer secondElement = twoElements.getSecond();
            if (entry.getValue() < avgDistBetweenClust) {
                //that means we have elements that are in the same cluster


                if (alreadyInSomeCluster.contains(firstElement) && alreadyInSomeCluster.contains(secondElement)) {
                    //we have two elements already in clusters so that is nothing we can do although it will interfere with clustering
                    continue;
                }

                if (!alreadyInSomeCluster.contains(firstElement) && !alreadyInSomeCluster.contains(secondElement)) {
                    //no elements are present in the cluster so we will create a new one
                    Set<Integer> set = new HashSet<>();
                    set.add(firstElement);
                    set.add(secondElement);
                    alreadyInSomeCluster.add(firstElement);
                    alreadyInSomeCluster.add(secondElement);
                    clusters.add(set);
                    continue;
                }

                if (addToCluster(clusters, alreadyInSomeCluster, firstElement, secondElement)) continue;

                addToCluster(clusters, alreadyInSomeCluster, secondElement, firstElement);
            } else {
                Set<Integer> setFirst = new HashSet<>();
                setFirst.add(firstElement);
                Set<Integer> setSecond = new HashSet<>();
                setSecond.add(secondElement);

                clusters.add(setFirst);
                clusters.add(setSecond);
                alreadyInSomeCluster.add(firstElement);
                alreadyInSomeCluster.add(secondElement);
            }
        }
        return clusters;
    }

    private static boolean addToCluster(List<Set<Integer>> clusters, Set<Integer> alreadyInSomeCluster, Integer firstElement, Integer secondElement) {
        if (alreadyInSomeCluster.contains(firstElement)) {
            //first element is present in the cluster, so the second one due to the distance will be in the same
            boolean added = false;
            for (Set<Integer> cluster : clusters) {
                if (cluster.contains(firstElement)) {
                    cluster.add(secondElement);
                    added = true;
                }
                if (added) {
                    break;
                }
            }
            alreadyInSomeCluster.add(secondElement);
            return true;
        }
        return false;
    }

    private static void generateDistancesBetweenPairs(List<Element> listOfElements, Map<Integer, String> mapOfElements, AtomicInteger counter, Thread countingThread, ConcurrentHashMap<TwoElements, Double> distances) {
        countingThread.start();
        listOfElements
                .parallelStream()
                .forEach(el -> el.countDistanceToAll(mapOfElements, new LCSMetrics(), counter, distances));
        countingThread.stop();
    }

    private static List<String> getSlicedContentBySep(File file, String regexSeparator) {
        String content = FileReader.getStringFromFile(file);
        Preconditions.checkNotNull(content, "Content from file : {} null", file.getAbsolutePath());
        List<String> allElements = new ArrayList<>();
        allElements = Arrays.asList(content.split(regexSeparator));
        return allElements;
    }
}
