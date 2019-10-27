# Anomality Detection with Apache Flink
## Requirements
This project was built using the following software:
- Ubuntu 16.04 LTS
- Java/OpenJDK 1.8
- Apache Flink 1.9.1
- Apache Maven 3.3.9 
- Docker 19.03

## Getting started
The following steps are required in order to get the docker image, run the project and store the results in InfluxDB.

1. Download the project to a local folder.
2. Create a folder named `dataset` inside the project folder.
3. Download the provided dataset into this folder and unzip it.
   - https://www.dropbox.com/s/3ww0xoitwkzaate/TestFile.zip?dl=0
4. Open the project with an IDE with Maven integrated and build the package.
   - You can also do this by running in a terminal located in the project folder:
     ```bash
     $ mvn package
     ```
5. To get InfluxDB docker image, in a terminal navigate to the project location and run:
      ```bash
      $ ./anomaly-detection-docker.sh
      ```
6. In a second terminal, navigate again to the project location and run the Flink project with:
    ```bash
    $ ./anomaly-detection-flink.sh
    ```
   - Enter the path to Flink binaries folder when prompted (`my/path/to/flink/bin`).
   - Enter the path to the JAR file when prompted (`my/path/to/myjar.jar`).
   - A browser window will open with the **Dispatcher's web front end** at http://localhost:8081.
7. Using the last terminal or in a new terminal stop the cluster by running*:
   ```bash
   $ /my/path/to/flink/bin/stop-cluster.sh
   ```
   - Run only after you're done with the other tasks or reviewing the results.

Please, note that you can use Ctrl-C to end any task.

## Manual running
In order to run the project manually, go through steps 1 to 4 from **Getting started** steps, and then
1. Open a terminal and run:
   ```bash
   $ docker pull influxdb
   
   $ docker run --rm \
      -e INFLUXDB_DB=db_anomalies -e INFLUXDB_ADMIN_ENABLED=true \
      -e INFLUXDB_ADMIN_USER=admin -e INFLUXDB_ADMIN_PASSWORD=admin \
      -v "$PWD":/var/lib/influxdb \
      influxdb /init-influxdb.sh
   
   $ docker run -p 8086:8086 \
      -v "$PWD":/var/lib/influxdb \
      influxdb
   ```
2. Open another terminal and navigate to the project folder, then run*:
   ```bash
   $ /my/path/to/flink/bin/start-cluster.sh
   
   $ /my/path/to/flink/bin/flink run my/path/to/myjar.jar --port 9000
   ```
3. Using the last terminal or in a new terminal stop the cluster by running*:
   ```bash
   $ /my/path/to/flink/bin/stop-cluster.sh
   ```
***Note**: replace `/my/path/to/flink` with the path to your Apache Flink folder
