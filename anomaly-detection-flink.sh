# Require from the user Apache Flink binary folder and the JAR file locations
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
echo "Submitting the Flink program..."
"${FLINKPATH}/flink" run "$JARPATH" --port 9000
echo "Done!"
