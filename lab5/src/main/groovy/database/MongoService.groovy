package database

import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import services.ProbOfElem
import services.ProbabilityFinder

class MongoService implements ProbabilityFinder {
    public static final int CHUNK = 1000000
    public static final String N_GRAMS_PROB_3 = "nGramsProb3"
    public static final String N_GRAMS_PROB_2 = "nGramsProb2"
    public static final String N_GRAMS_PROB_1 = "nGramsProb1"
    private List<Document> toBeInserted = new LinkedList<>();
    private collectionMap;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private host = "localhost";
    private port = 27017;
    private databaseName = 'PJN';
    private Integer previousNgram = -1

    MongoService() {
        mongoClient = new MongoClient(host, port)
        db = mongoClient.getDatabase(databaseName);
        def collection3 = db.getCollection(N_GRAMS_PROB_3);
        def collection2 = db.getCollection(N_GRAMS_PROB_2);
        def collection1 = db.getCollection(N_GRAMS_PROB_1);
        collectionMap = [1: collection1, 2: collection2, 3: collection3]
    }

    void saveProbOfElement(ProbOfElem probOfElem, Integer nGram) {
        checkForPreviousData(nGram)

        Document dbObject = createDocumentFromElem(probOfElem)
        toBeInserted.add(dbObject)

        if (toBeInserted.size() > CHUNK) {
            insertMany(nGram)
        }
    }

    private static Document createDocumentFromElem(ProbOfElem probOfElem) {
        Document dbObject = new Document();
        dbObject.put("value", probOfElem.value.toUpperCase())
        dbObject.put("probability", probOfElem.probability)
        dbObject.put("nGram", probOfElem.nGram as Integer)
        dbObject.put("backOffValue", probOfElem.backOffValue)
        dbObject
    }

    private void checkForPreviousData(int nGram) {
        if (previousNgram != nGram) {
            insertMany(previousNgram)
            previousNgram = nGram
        }
    }

    private void insertMany(Integer nGram) {
        if (toBeInserted.size() > 0) {
            collectionMap.get(nGram).insertMany(toBeInserted)
        }
        toBeInserted.clear()
    }


    @Override
    double getProbabilityOfElement(String value, Integer nGram) {
        value = value.toUpperCase().trim()
        def query = new Document()
        query.put("value", value)
        query.put("nGram", Integer)
        def cursor = collectionMap.get(nGram).find(query).limit(1)
        def first = cursor.first()
        def result = (++first)["probability"]
        Optional.ofNullable(result as Double).orElse(0d)
    }
}