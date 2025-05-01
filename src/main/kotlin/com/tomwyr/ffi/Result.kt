package com.tomwyr.ffi

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
sealed class Result<T, E : Throwable> {
    fun unwrap(): T = when (this) {
        is Ok -> value
        is Err -> throw value
    }
}

@Serializable
class Ok<T, E : Throwable>(val value: T) : Result<T, E>()

@Serializable
class Err<T, E : Throwable>(val value: E) : Result<T, E>()

class ResultSerializer<T, E : Throwable>(
    private val tSerializer: KSerializer<T>,
    private val eSerializer: KSerializer<E>
) : KSerializer<Result<T, E>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Result")

    override fun serialize(encoder: Encoder, value: Result<T, E>) {
        require(encoder is JsonEncoder)
        val json = when (value) {
            is Ok -> buildJsonObject { put("ok", encoder.json.encodeToJsonElement(tSerializer, value.value)) }
            is Err -> buildJsonObject { put("err", encoder.json.encodeToJsonElement(eSerializer, value.value)) }
        }
        encoder.encodeJsonElement(json)
    }

    override fun deserialize(decoder: Decoder): Result<T, E> {
        require(decoder is JsonDecoder)
        val json = decoder.decodeJsonElement().jsonObject
        return when {
            "ok" in json -> Ok(decoder.json.decodeFromJsonElement(tSerializer, json.getValue("ok")))
            "err" in json -> Err(decoder.json.decodeFromJsonElement(eSerializer, json.getValue("err")))
            else -> throw SerializationException("Invalid data format for the expected Result type")
        }
    }
}

inline fun <reified T, reified E : Throwable> Result.Companion.fromJson(input: String): Result<T, E> {
    val serializer = ResultSerializer<T, E>(serializer(), serializer())
    return Json.decodeFromString(serializer, input)
}

inline fun <reified T, reified E : Throwable> Result<T, E>.toJson(): String {
    val serializer = ResultSerializer<T, E>(serializer(), serializer())
    return Json.encodeToString(serializer, this)
}
