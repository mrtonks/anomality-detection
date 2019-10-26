# Anomality Detection with Apache Flink

## Getting started
The following steps are required in order to get the docker image, run the project and store the results in InfluxDB.

1. Download the project to a local folder.
2. Open an IDE with Maven integrated and build the package.
   - You can also do this by running in a terminal located in the project folder:
     ```bash
     mvn package
     ```
3. To get InfluxDB docker image, in a terminal navigate to the project location and run:
      ```bash
      ./anomaly-detection-docker.sh
      ```
4. In a second terminal, navigate again to the project location and run the Flink project with:
    ```bash
    ./anomaly-detection-flink.sh
    ```
   - Enter the path to Flink binaries folder when prompted (`my/path/to/flink/bin`).
   - Enter the path to the JAR file when prompted (`my/path/to/myjar.jar`).
5. Using the last terminal or in a new terminal stop the cluster by running:
   ```bash
   /my/path/to/flink/bin/stop-cluster.sh
   ```
   - **Note**: replace `/my/path/to/flink` with the path to your Flink folder
   - Run only after you're done with the other tasks or reviewing the results.

Please, note that you can use Ctrl-C to end any task.
