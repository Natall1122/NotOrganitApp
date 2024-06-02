package es.nlc.notorganitapp.Mongo
// NO FA FALTA BORRAR
import com.mongodb.reactivestreams.client.MongoClient
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.bson.Document

class DatabaseOperations(private val mongoClient: MongoClient) {

    suspend fun addNoteToCategory(category: String, note: Document) {
        val database = mongoClient.getDatabase("categories")
        val collection = database.getCollection(category)
        collection.insertOne(note).awaitFirstOrNull()
    }

    suspend fun deleteNoteFromCategory(category: String, noteId: String) {
        val database = mongoClient.getDatabase("categories")
        val collection = database.getCollection(category)
        collection.deleteOne(Document("_id", noteId)).awaitFirstOrNull()
    }

    suspend fun updateNoteInCategory(category: String, noteId: String, updatedNote: Document) {
        val database = mongoClient.getDatabase("categories")
        val collection = database.getCollection(category)
        collection.updateOne(Document("_id", noteId), Document("\$set", updatedNote)).awaitFirstOrNull()
    }

    suspend fun getNotesFromCategory(category: String): List<Document> {
        val database = mongoClient.getDatabase("categories")
        val collection = database.getCollection(category)
        return collection.find().asFlow().toList()
    }
}
