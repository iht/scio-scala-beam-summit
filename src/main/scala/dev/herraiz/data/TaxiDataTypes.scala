/*
 * Copyright 2021 Israel Herraiz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.herraiz.data

import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser.decode
import com.spotify.scio.bigquery.types.BigQueryType
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{Instant, Interval}

import scala.util.{Failure, Success, Try}

object DataTypes {
  // Decoder for Joda dates
  protected val dateFormatter: DateTimeFormatter =
    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSZ")

  // String to Json using Circe
  def json2TaxiRide(jsonStr: String): Either[Error, PointTaxiRide] = {
    decode[PointTaxiRide](jsonStr)
  }

  // To map to a convenience class that can be written to BQ
  def circeErrorToCustomError(origError: Error): JsonError =
    JsonError(origError.getMessage)

  // Decoder to parse Joda Instants
  protected lazy implicit val instantDecoder: Decoder[Instant] =
    Decoder.decodeString.emap { s: String =>
      val ti: Try[Instant] = Try(dateFormatter.parseDateTime(s).toInstant)

      ti match {
        case Success(instant: Instant) => Right(instant)
        case Failure(e: Throwable)     => Left(e.getMessage)
      }
    }

  // Decoder to produce TaxiRide objects from Json
  protected lazy implicit val taxiRideDecoder: Decoder[PointTaxiRide] =
    deriveDecoder[PointTaxiRide]

  @BigQueryType.toTable
  case class PointTaxiRide(
      ride_id: String,
      point_idx: Int,
      latitude: Double,
      longitude: Double,
      timestamp: Instant,
      meter_reading: Double,
      meter_increment: Double,
      ride_status: String,
      passenger_count: Int
  ) {
    def toTaxiRide: TaxiRide =
      TaxiRide(
        this.ride_id,
        1,
        this.timestamp,
        None,
        this.meter_increment,
        this.ride_status,
        None
      )
  }

  @BigQueryType.toTable
  case class TaxiRide(
      ride_id: String,
      n_points: Int,
      init: Instant,
      finish: Option[Instant],
      total_meter: Double,
      init_status: String,
      finish_status: Option[String]
  ) {
    def +(taxiRide: TaxiRide): TaxiRide = {

      // Something is missing here

      val (first, second) =
        if (this.init.isAfter(taxiRide.init)) {
          (taxiRide, this)
        } else {
          (this, taxiRide)
        }

      val (finishStatus: Option[String], finishInstant: Option[Instant]) =
        first.finish match {
          case None =>
            (
              Some(second.finish_status.getOrElse(second.init_status)),
              Some(second.finish.getOrElse(second.init))
            )
          case Some(i) =>
            val interval: Interval = new Interval(first.init, i)
            val testInstant: Instant = second.finish.getOrElse(second.init)
            if (interval.contains(testInstant)) {
              (
                Some(first.finish_status.getOrElse(first.init_status)),
                Some(first.finish.getOrElse(first.init))
              )
            } else {
              (
                Some(second.finish_status.getOrElse(second.init_status)),
                Some(second.finish.getOrElse(second.init))
              )
            }
        }

      TaxiRide(
        taxiRide.ride_id,
        0,
        first.init,
        finishInstant,
        first.total_meter + second.total_meter,
        first.init_status,
        finishStatus
      )
    }
  }

  @BigQueryType.toTable
  case class JsonError(msg: String)

}
