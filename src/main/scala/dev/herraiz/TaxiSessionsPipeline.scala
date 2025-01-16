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

import io.circe.Error
import com.spotify.scio._
import com.spotify.scio.bigquery._
import com.spotify.scio.pubsub._
import com.spotify.scio.values._
import dev.herraiz.data.DataTypes._
import org.apache.beam.sdk.transforms.windowing.{
  AfterProcessingTime,
  AfterWatermark
}
import org.joda.time.Duration

object TaxiSessionsPipeline {
  val SESSION_GAP = 600
  val EARLY_RESULT = 10
  val LATENESS = 900

  def main(cmdlineArgs: Array[String]): Unit = {
    val (scontext: ScioContext, opts: Args) = ContextAndArgs(cmdlineArgs)
    implicit val sc = scontext

    val pubsubTopic: String = opts("pubsub-topic")
    val goodTable = opts("output-table")
    val badTable = opts("errors-table")
    val accumTable = opts("accum-table")

    val messages: SCollection[String] = getMessagesFromPubSub(pubsubTopic)
    val (
      rides: SCollection[PointTaxiRide],
      writableErrors: SCollection[JsonError]
    ) = parseJSONStrings(messages)

    rides
      .saveAsTypedBigQueryTable(
        Table.Spec(goodTable)
      )

    writableErrors.saveAsTypedBigQueryTable(
      Table.Spec(badTable)
    )

    // Group by session with a max duration of 5 mins between events
    // Window options
    val wopts: WindowOptions = customWindowOptions
    val groupRides = groupRidesByKey(rides.map(_.toTaxiRide), wopts)
    groupRides.saveAsTypedBigQueryTable(
      Table.Spec(accumTable)
    )

    sc.run
  }

  def customWindowOptions: WindowOptions =
    WindowOptions(
      trigger = AfterWatermark
        .pastEndOfWindow()
        .withEarlyFirings(
          AfterProcessingTime.pastFirstElementInPane
            .plusDelayOf(Duration.standardSeconds(EARLY_RESULT))
        )
        .withLateFirings(
          AfterProcessingTime
            .pastFirstElementInPane()
            .plusDelayOf(Duration.standardSeconds(LATENESS))
        ),
      accumulationMode = streaming.ACCUMULATING_FIRED_PANES,
      allowedLateness = Duration.standardSeconds(LATENESS)
    )

  def getMessagesFromPubSub(
      pubsubTopic: String
  )(implicit sc: ScioContext): SCollection[String] = {
    ???
  }

  def parseJSONStrings(
      messages: SCollection[String]
  ): (SCollection[PointTaxiRide], SCollection[JsonError]) = {
    ???
  }

  def groupRidesByKey(
      rides: SCollection[TaxiRide],
      wopts: WindowOptions
  ): SCollection[TaxiRide] = {
    ???
  }
}
