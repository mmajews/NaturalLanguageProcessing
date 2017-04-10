package database

import com.mongodb.DBCursor
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoCursor
import com.mongodb.client.MongoDatabase
import org.bson.Document
import services.ProbOfElem
import services.ProbabilityFinder

import static com.mongodb.client.model.Filters.eq

class MongoService implements ProbabilityFinder {
    public static final int CHUNK = 1000000
    public static final String N_GRAMS_PROB_3 = "nGramsProb3"
    public static final String N_GRAMS_PROB_2 = "nGramsProb2"
    public static final String N_GRAMS_PROB_1 = "nGramsProb1"
    private List<Document> toBeInserted = new LinkedList<>()
    private collectionMap
    private MongoClient mongoClient
    private MongoDatabase db
    private host = "localhost"
    private port = 27017
    private databaseName = 'PJN'
    private Integer previousNgram = -1

    MongoService() {
        MongoClientOptions settings = MongoClientOptions.builder().codecRegistry(MongoClient.getDefaultCodecRegistry()).build()
        mongoClient = new MongoClient(host, settings)
        db = mongoClient.getDatabase(databaseName)
        def collection3 = db.getCollection(N_GRAMS_PROB_3)
        def collection2 = db.getCollection(N_GRAMS_PROB_2)
        def collection1 = db.getCollection(N_GRAMS_PROB_1)
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
        Document dbObject = new Document()
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
        def mongoCollection = collectionMap.get(nGram)
        MongoCursor<Document> cursor = mongoCollection.find(eq("value", value)).iterator()
        if(!cursor.hasNext()){
            0d
        } else{
            Document result = cursor.next()
            return result.get("probability") as double
        }
    }
}