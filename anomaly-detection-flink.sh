#!/bin/bash
# This script will ask the user the necesary parameters for the project to run.
# Then it will run the FLink program.

response=
echo -n "Enter FLINK binary path [my/path/to/flink/bin] > "
read -r response
if [ -n "$response" ]; then
  FLINKPATH=$response
fi

response=
echo -n "Enter JAR file path [my/path/to/myjar.jar] > "
read -r response
if [ -n "$response" ]; then
  JARPATH=$response
fi

"${FLINKPATH}/start-cluster.sh"
echo "Opening Dispatcher's web fron end at http://localhost:8081."
x-www-browser http://localhost:8081
echo "Submitting the Flink program..."
"${FLINKPATH}/flink" run "$JARPATH" --port 9000
echo "Done!"
