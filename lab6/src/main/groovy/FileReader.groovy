import com.google.common.base.Preconditions

import java.util.stream.Collectors

import static tfidf.TermsHelper.getTermsForString

class FileReader {

    static Set<String> generateStopList(List<List<String>> docs, double stopListThreshold) {
        def stopList = new HashSet<String>()
        def occurrence = new HashMap<String, Integer>()
        def numberOfTotalOccurrences = 0d

        for (List<String> document : docs) {
            for (String el : document) {
                if (occurrence.containsKey(el)) {
                    occurrence.put(el, occurrence.get(el) + 1)
                } else {
                    occurrence.put(el, 1)
                }
                numberOfTotalOccurrences++
            }
        }

        for (Map.Entry<String, Integer> ent : occurrence) {
            if ((ent.getValue() as double) / (numberOfTotalOccurrences as double) > stopListThreshold) {
                stopList.add(ent.getKey())
            }
        }
        stopList
    }

    static List<List<String>> readFileIntoDocuments(String path) {
        def inputFile = new File(path)
        def filePath = inputFile.getAbsolutePath()
        Preconditions.checkArgument(inputFile.exists(), "File $filePath does not exist")
        def content = inputFile.text
        def documents = content.split("#\\d+\\n").toList()
        documents = documents.stream().filter {
            el -> el != ""
        }.collect(Collectors.toList())


        def slicedElements = new ArrayList<List<String>>()
        documents.forEach {
            el ->
                def ready = getTermsForString(el)
                slicedElements.add(ready)
        }
        slicedElements
    }
}
