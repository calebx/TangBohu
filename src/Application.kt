package cn.luedian.t

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.suggest.Suggester
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
// import io.ktor.client.HttpClient
// import io.ktor.client.engine.apache.Apache
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import java.io.FileInputStream
import kotlin.random.Random

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    System.setProperty("java.net.useSystemProxies", "true");

    install(CallLogging)

    install(ContentNegotiation) {
        json(
            Json(
                DefaultJsonConfiguration.copy(
                    prettyPrint = true,
                    ignoreUnknownKeys = true
                )
            ),
            ContentType.Application.Json
        )
    }

    // never used, keep here only for future refer
    // val client = HttpClient(Apache) { }

    val translate = TranslateOptions
        .newBuilder()
        .setCredentials(
            ServiceAccountCredentials
                .fromStream(
                    FileInputStream(
                        "/usr/local/etc/xiang-pi.json"
                    )
                )
        )
        .build().service

    routing {
        get("/") {
            val data = ResponseData(
                true,
                "hola, buddy! You can talk to Caleb[caleb.xiang(AT)gmail.com] for detail. thx!"
            )
            call.respond(data)
        }

        get("/t/{text}") {
            val text = call.parameters.get("text")
            val translatedText = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage("zh-CN")
            );
            val data = ResponseData(true, translatedText.translatedText)
            call.respond(data)
        }

        get("/q/{text}") {
            val suggester = Suggester()

            // pick 3 books of poems, combine them to pick a single line
            for (i in 0..2) {
                val lines = PoemAssets.loadRandomPoems().map { it -> it.paragraphs }
                    .flatten()
                lines.forEach(suggester::addSentence)
            }

            val text = call.parameters.get("text")
            val translatedText = translate.translate(
                text,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.targetLanguage("zh-CN")
            );

            // pick two lines from suggester, but random reply one of them
            val result = suggester.suggest(translatedText.translatedText, 2)

            val resultIdx = 0; // or we can try random pick as: Random.nextInt(0, 2)
            var resultText = HanLP.convertToSimplifiedChinese(result[resultIdx])
            val data = ResponseData(true, resultText)

            call.respond(data)
        }
    }
}

