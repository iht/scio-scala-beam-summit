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

import dev.herraiz.data.DataTypes.TaxiRide
import dev.herraiz.data.TestData.obj1
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TaxiRideSpec extends AnyWordSpec with Matchers {

  val ptr: DataTypes.PointTaxiRide = obj1.right.get

  "The toTaxiRide method" should {
    "produce a correct object" in {
      val tr: TaxiRide = ptr.toTaxiRide
      tr shouldBe a[TaxiRide]
      tr.ride_id shouldBe ptr.ride_id
      tr.init shouldBe ptr.timestamp
      tr.finish shouldBe None
      tr.init_status shouldBe ptr.ride_status
      tr.finish_status shouldBe None
      tr.n_points shouldBe 1
      tr.total_meter shouldBe ptr.meter_increment
    }
  }

  "Two TaxiRides" should {
    "be aggregated correcly" in {
      val tr1: TaxiRide = ptr.toTaxiRide
      val tr2: TaxiRide = ptr.toTaxiRide
      val tr: TaxiRide = tr1 + tr2
      tr shouldBe a[TaxiRide]
      tr.ride_id shouldBe tr1.ride_id
      tr.ride_id shouldBe tr2.ride_id
      tr.init shouldBe tr1.init
      tr.finish shouldBe Some(tr1.init)
      tr.init_status shouldBe tr1.init_status
      tr.finish_status shouldBe Some(tr1.init_status)
      tr.n_points shouldBe 2
      tr.total_meter shouldBe tr1.total_meter + tr2.total_meter
    }
  }
}
