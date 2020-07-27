package cn.luedian.t

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.io.File
import kotlin.random.Random

@Serializable
data class Poem(val paragraphs: List<String>) {
}

class PoemAssets() {
    companion object {
        var files = mutableListOf<String>()
        var filesCount = 0;

        init {
            File("assets").walk().forEach { it: File ->
                if (it.name.endsWith(".json")) {
                    files.add(it.name)
                }
            }

            filesCount = files.size
        }

        fun pickAFile() = files[Random.nextInt(0, filesCount)]

        fun loadRandomPoems(): List<Poem> {
            val lines = File("assets/" + pickAFile())
                .readLines()
                .joinToString("")

            val json = Json(JsonConfiguration(ignoreUnknownKeys = true))

            return json.parse(Poem.serializer().list, lines)
        }
    }
}

