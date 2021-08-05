#!/bin/sh

#
# Copyright 2021 Israel Herraiz
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

PROJECT_ID=$1
REGION=us-east1
TMP_BUCKET=gs://${PROJECT_ID}
DATASET_NAME=taxi_rides

PUBLIC_IPS=false  # Enable Google Private Access in the regional subnetwork, or change this to true
STREAMING_ENGINE=true

bq rm -f ${DATASET_NAME}.point_rides
bq rm -f ${DATASET_NAME}.total_rides
bq rm -f ${DATASET_NAME}.errors

cd target/universal/stage/bin
./scio-scala-workshop-beam-summit --project=$PROJECT_ID --region=$REGION \
            --runner=DataflowRunner --gcpTempLocation=${TMP_BUCKET}/dataflow-tmp \
            --pubsub-topic=projects/pubsub-public-data/topics/taxirides-realtime \
            --output-table="${PROJECT_ID}:${DATASET_NAME}.point_rides" \
            --accum-table="${PROJECT_ID}:${DATASET_NAME}.total_rides" \
            --errors-table="${PROJECT_ID}:${DATASET_NAME}.errors" \
            --usePublicIps=$PUBLIC_IPS \
            --enableStreamingEngine=$STREAMING_ENGINE