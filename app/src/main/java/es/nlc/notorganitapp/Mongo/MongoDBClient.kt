package es.nlc.notorganitapp.Mongo
// NO FA FALTA BORRAR
import android.content.Context
import android.widget.Toast
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.Document

object MongoDBClient {
    private const val CONNECTION_STRING = "mongodb://natalia:natalia@cluster0-shard-00-00.nhumvis.mongodb.net:27017,cluster0-shard-00-01.nhumvis.mongodb.net:27017,cluster0-shard-00-02.nhumvis.mongodb.net:27017/test?ssl=true&replicaSet=atlas-zgbruu-shard-0&authSource=admin&retryWrites=true&w=majority"

    private val serverApi = ServerApi.builder()
        .version(ServerApiVersion.V1)
        .build()

    private val mongoClientSettings = MongoClientSettings.builder()
        .applyConnectionString(ConnectionString(CONNECTION_STRING))
        .serverApi(serverApi)
        .build()

    val client: MongoClient = MongoClients.create(mongoClientSettings)

    suspend fun pingDatabase() {
        val database = client.getDatabase("admin")
        val result = database.runCommand(Document("ping", 1)).awaitFirst()
        println("Pinged your deployment. You successfully connected to MongoDB!")

    }
}
