package com.github.plokhotnyuk.jsoniter_scala.macros

import java.nio.charset.StandardCharsets._

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros.CirceEncodersDecoders._
import com.github.plokhotnyuk.jsoniter_scala.macros.JacksonSerDesers._
import com.github.plokhotnyuk.jsoniter_scala.macros.JsoniterCodecs._
import io.circe.parser._
import io.circe.syntax._
import io.circe.jackson.jackson2
import org.openjdk.jmh.annotations.Benchmark
import play.api.libs.json.Json

class ArrayOfIntsBenchmark extends CommonParams {
  val obj: Array[Int] = (1 to 128).map(i => ((i * 1498724053) / Math.pow(10, i % 10)).toInt).toArray
  val jsonString: String = obj.mkString("[", ",", "]")
  val jsonBytes: Array[Byte] = jsonString.getBytes

  @Benchmark
  def readCirce(): Array[Int] = decode[Array[Int]](new String(jsonBytes, UTF_8)).fold(throw _, x => x)

  @Benchmark
  def readCirceJackson(): Array[Int] = jackson2.decode[Array[Int]](new String(jsonBytes, UTF_8)).fold(throw _, x => x)

  @Benchmark
  def readCirceJackson2(): Array[Int] = jackson2.decodeByteArray[Array[Int]](jsonBytes).fold(throw _, x => x)

  @Benchmark
  def readJacksonScala(): Array[Int] = jacksonMapper.readValue[Array[Int]](jsonBytes)

  @Benchmark
  def readJsoniterScala(): Array[Int] = read[Array[Int]](jsonBytes)

  @Benchmark
  def readPlayJson(): Array[Int] = Json.parse(jsonBytes).as[Array[Int]]

  @Benchmark
  def writeCirce(): Array[Byte] = printer.pretty(obj.asJson).getBytes(UTF_8)

  @Benchmark
  def writeCirceJackson(): Array[Byte] = jackson2.encode(obj.asJson).getBytes(UTF_8)

  @Benchmark
  def writeCirceJackson2(): Array[Byte] = jackson2.encodeByteArray(obj.asJson)

  @Benchmark
  def writeJacksonScala(): Array[Byte] = jacksonMapper.writeValueAsBytes(obj)

  @Benchmark
  def writeJsoniterScala(): Array[Byte] = write(obj)

  @Benchmark
  def writeJsoniterScalaPrealloc(): Int = write(obj, preallocatedBuf, 0)

  @Benchmark
  def writePlayJson(): Array[Byte] = Json.toBytes(Json.toJson(obj))
}