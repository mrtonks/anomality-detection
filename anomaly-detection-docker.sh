#!/bin/bash
# This script will pull the InfluxDB docker image and initialize the database
# with a default name db_anomalies. Then, it will run the container.

echo "Pulling InfluxDB from Docker..."
docker pull influxdb

echo "Initializing database..."
echo "Creating database db_anomalies..."
echo "Creating admin user and setting up password..."
docker run --rm \
      -e INFLUXDB_DB=db_anomalies -e INFLUXDB_ADMIN_ENABLED=true \
      -e INFLUXDB_ADMIN_USER=admin -e INFLUXDB_ADMIN_PASSWORD=admin \
      -v "$PWD":/var/lib/influxdb \
      influxdb /init-influxdb.sh
echo "Process completed!"

echo "Running container..."
docker run -p 8086:8086 \
      -v "$PWD":/var/lib/influxdb \
      influxdb


