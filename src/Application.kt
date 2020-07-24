package cn.luedian.t

import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import com.hankcs.hanlp.suggest.Suggester
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.serialization.DefaultJsonConfiguration
import io.ktor.serialization.json
import kotlinx.serialization.json.Json
import java.io.FileInputStream

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
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

    val client = HttpClient(Apache) {
    }

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
                "hola, talk to caleb for detail please. thx."
            )
            call.respond(data)
        }

//        get("/t/{text}") {
//            val text = call.parameters.get("text")
//            val translatedText = translate.translate(
//                text,
//                Translate.TranslateOption.sourceLanguage("en"),
//                Translate.TranslateOption.targetLanguage("zh-CN")
//            );
//            val data = ResponseData(
//                true,
//                translatedText.translatedText
//            )
//            call.respond(data)
//        }

//        get("/q/{text}") {
//            val lines = readPoems().map { it -> it.paragraphs }
//                .flatten()
//            val suggester = Suggester()
//            for (line in lines) {
//                suggester.addSentence(line)
//            }
//            val text = call.parameters.get("text")
//            val translatedText = translate.translate(
//                text,
//                Translate.TranslateOption.sourceLanguage("en"),
//                Translate.TranslateOption.targetLanguage("zh-CN")
//            );
//            val result = suggester.suggest(translatedText.translatedText, 1)
//            val data = ResponseData(
//                true,
//                result[0]
//            )
//
//            call.respond(data)
//        }
    }
}

