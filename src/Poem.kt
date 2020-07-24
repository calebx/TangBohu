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

fun pickAFile(): String {
    var files = mutableListOf<String>()
    File("assets").walk().forEach { it: File ->
        if (it.name.endsWith(".json")) {
            files.add(it.name)
        }
    }

    val r = Random.nextInt(0, (files.size - 1))
    return files[r]
}

fun readPoems(): List<Poem> {
    val lines = File("assets/" + pickAFile()).readLines().joinToString("")
    val json = Json(JsonConfiguration(ignoreUnknownKeys = true))

    return json.parse(Poem.serializer().list, lines)
}
