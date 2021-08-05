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

import org.scalatest.matchers.should.Matchers
import org.joda.time.DateTimeZone
import org.scalatest.wordspec.AnyWordSpec

class PointTaxiRideSpec {



  class PointTaxiRideSpec extends AnyWordSpec with Matchers {


    "Taxi ride parsing" should {
      "produce an object" in {
        TestData.obj1.isRight shouldBe true
      }

      "parse datetimes correctly" in {
        val dt = TestData.obj1.right.get.timestamp.toDateTime(DateTimeZone.UTC)
        dt.year.get shouldBe 2019
        dt.monthOfYear.get shouldBe 9
        dt.dayOfMonth.get shouldBe 9
        dt.hourOfDay.get shouldBe 11 + 4
        dt.minuteOfHour.get shouldBe 42
        dt.secondOfMinute.get shouldBe 48
      }

      "parse datetimes correctly with another object" in {
        val dt = TestData.obj2.right.get.timestamp.toDateTime(DateTimeZone.UTC)
        dt.year.get shouldBe 2019
        dt.monthOfYear.get shouldBe 9
        dt.dayOfMonth.get shouldBe 9
        dt.hourOfDay.get shouldBe 11 + 4
        dt.minuteOfHour.get shouldBe 52
        dt.secondOfMinute.get shouldBe 48
      }

      "parse other fields correctly" in {
        val ride = TestData.obj1.right.get

        ride.ride_status shouldBe "pickup"
        ride.passenger_count shouldBe 2
        ride.point_idx shouldBe 160
        ride.meter_increment shouldBe 0.03591837
        ride.meter_reading shouldBe 5.746939
        ride.latitude shouldBe 40.780210000000004
        ride.longitude shouldBe -73.97139
        ride.ride_id shouldBe "0878437d-8dae-4ebc-b4cf-3a3da40396ad"
      }

      "parse other fields correctly with another object" in {
        val ride = TestData.obj2.right.get

        ride.ride_status shouldBe "dropoff"
        ride.passenger_count shouldBe 2
        ride.point_idx shouldBe 161
        ride.meter_increment shouldBe 0.03591837
        ride.meter_reading shouldBe 6.0
        ride.latitude shouldBe 40.180210000000004
        ride.longitude shouldBe -73.17139
        ride.ride_id shouldBe "0878437d-8dae-4ebc-b4cf-3a3da40396ad"
      }
    }

  }

}
