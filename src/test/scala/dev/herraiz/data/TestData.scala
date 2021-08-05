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

import dev.herraiz.data.DataTypes.{PointTaxiRide, json2TaxiRide}
import io.circe
import org.joda.time.{Duration, Instant}

object TestData {

  val PIPELINE_START: Int = 10

  val jsonStr1: String =
    """
      |{
      |  "ride_id": "0878437d-8dae-4ebc-b4cf-3a3da40396ad",
      |  "point_idx": 160,
      |  "latitude": 40.780210000000004,
      |  "longitude": -73.97139,
      |  "timestamp": "2019-09-09T11:42:48.33739-04:00",
      |  "meter_reading": 5.746939,
      |  "meter_increment": 0.03591837,
      |  "ride_status": "pickup",
      |  "passenger_count": 2
      |}
      |""".stripMargin

  val obj1: Either[circe.Error, PointTaxiRide] = json2TaxiRide(jsonStr1)

  val jsonStr2: String =
    """
      |{
      |  "ride_id": "0878437d-8dae-4ebc-b4cf-3a3da40396ad",
      |  "point_idx": 161,
      |  "latitude": 40.180210000000004,
      |  "longitude": -73.17139,
      |  "timestamp": "2019-09-09T11:52:48.33739-04:00",
      |  "meter_reading": 6,
      |  "meter_increment": 0.03591837,
      |  "ride_status": "dropoff",
      |  "passenger_count": 2
      |}
      |""".stripMargin

  val obj2: Either[circe.Error, PointTaxiRide] = json2TaxiRide(jsonStr2)

  val badJson: String =
    """"{
      | "hello": "world"
      | }
      |""".stripMargin

  val baseTime: Instant = obj1
    .map(_.timestamp.minus(Duration.standardSeconds(PIPELINE_START)))
    .getOrElse(Instant.now())

}