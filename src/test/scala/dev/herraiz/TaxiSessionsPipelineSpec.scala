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

package dev.herraiz

import com.spotify.scio.ScioContext
import com.spotify.scio.testing._
import com.spotify.scio.values.{SCollection, WindowOptions}
import dev.herraiz.data.DataTypes
import dev.herraiz.data.TestData._
import org.apache.beam.sdk.testing.TestStream

class TaxiSessionsPipelineSpec extends PipelineSpec {
  "The TaxiSessionsPipelineSpec" should "run the pipeline correctly" in {



    val stream: TestStream[String] = testStreamOf[String]
      .advanceWatermarkTo(baseTime)
      .addElements(badJson)
      .advanceWatermarkTo(baseTime.plus(PIPELINE_START))
      .addElements(jsonStr1)
      .advanceWatermarkTo(baseTime.plus(PIPELINE_START))
      .addElements(jsonStr2)
      .advanceWatermarkToInfinity

    runWithContext { sc: ScioContext =>
      val messages: SCollection[String] = sc.testStream(stream)
      val (points: SCollection[DataTypes.PointTaxiRide], errors: SCollection[DataTypes.JsonError]) =
        TaxiSessionsPipeline.parseJSONStrings(messages)
      points should haveSize(2)
      errors should haveSize(1)

      val wopts: WindowOptions = TaxiSessionsPipeline.customWindowOptions
      val rides = points.map(_.toTaxiRide)
      val grouped = TaxiSessionsPipeline.groupRidesByKey(rides, wopts)
      grouped should haveSize(1)
      grouped.map(_.init_status) should containInAnyOrder(List("pickup"))
      grouped.map(_.finish).map(_.isDefined) should containInAnyOrder(List(true))
      grouped.map(_.finish_status).map(_.isDefined) should containInAnyOrder(List(true))
      grouped.map(_.finish_status.get) should containInAnyOrder(List("dropoff"))
      grouped.map(_.n_points) should containInAnyOrder(List(2))
    }
  }
}