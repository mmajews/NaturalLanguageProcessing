import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static FileReader.generateStopList
import static FileReader.readFileIntoDocuments

class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class)
    private static final THRESHOLD = 0.005d

    static void main(String[] args) {
        logger.info("Starting app...")

        def sliced = readFileIntoDocuments("src/main/resources/pap_trimmed.txt")
        logger.info("Finished reading files into arrays with terms")

        def stopList = generateStopList(sliced, THRESHOLD)
        logger.info("Finished creating stoplist")

        ArrayList<Document> documents = createDocumentsList(sliced, stopList)
        def corpus = new Corpus(documents)
        def vectorSpace = new VectorSpaceModel(corpus)


        logger.info("Finishing reading file from documents")
    }


    private static createDocumentsList(List<List<String>> sliced, Set<String> stopList) {
        def id = 1
        def documents = new ArrayList<Document>()
        sliced.forEach {
            el ->
                documents.add(new Document(el, id, stopList))
                id++
        }
        documents
    }


}
