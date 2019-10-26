package com.myorg.detection;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple11;
import org.apache.flink.api.java.tuple.Tuple21;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBConfig;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBPoint;
import org.apache.flink.streaming.connectors.influxdb.InfluxDBSink;
import org.apache.flink.util.Collector;
import com.myorg.detection.util.IQR;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AnomalyDetection {

    /*
    * Function checkDouble
    * This function parses values to Double, otherwise returns default value.
    *
    * Params:
    * String s, string to parse
    * */
    private static Double checkDouble(String s) {
        try {
            return Double.parseDouble(s.replaceAll("\\s+", ""));
        }
        catch (Exception e) {
            return 0.0;
        }
    }

    /*
    * Function scoring
    * This function evaluates every sensor value accordingly to an IQR
    *
    * Params:
    * Double iqr, interquartile range value
    * Double value, sensor value
    * */
    private static Double scoring(Double iqr, Double val) {
        Double result = 0.0;

        if (val < 1.5 * iqr) {
            result = 0.0;
        } else if (val >= 1.5 * iqr && val < 3 * iqr) {
            result = 0.5;
        } else if (val >= 3 * iqr) {
            result = 1.0;
        }
        return result;
    }

    public static void main(String[] args) throws Exception {

        // Setup the flink streaming environments
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        final String csvDataset = new File( "./dataset/TestFile.csv").getAbsolutePath();
        final String outDataset = new File("./dataset/Results.csv").getAbsolutePath();

        System.out.println("Executing AnomalyDetection with the CSV dataset");
        DataStream<Tuple21<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>> source =
                env.readTextFile(csvDataset)
                        .filter(s -> !s.contains("Date")) // Filter out first line
                        .map(new MapFunction<String, Tuple11<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>>() {
                            public Tuple11<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> map(String string) throws Exception {
                                // Return a new Tuple11 object with the fields from the file
                                String[] fields = string.split(",");
                                return new Tuple11<>(
                                        fields[0], // Date
                                        checkDouble(fields[1]), // Sensor-1
                                        checkDouble(fields[2]), // Sensor-2
                                        checkDouble(fields[3]), // Sensor-3
                                        checkDouble(fields[4]), // Sensor-4
                                        checkDouble(fields[5]), // Sensor-5
                                        checkDouble(fields[6]), // Sensor-6
                                        checkDouble(fields[7]), // Sensor-7
                                        checkDouble(fields[8]), // Sensor-8
                                        checkDouble(fields[9]), // Sensor-9
                                        checkDouble(fields[10])); // Sensor-10
                            }
                        })
                .countWindowAll(100) // Window of 100 lines
                .apply(new AllWindowFunction<Tuple11<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>,
                        Tuple21<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>,
                        GlobalWindow>() {
                    @Override
                    public void apply(GlobalWindow globalWindow,
                                      Iterable<Tuple11<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>> iterable,
                                      Collector<Tuple21<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>> collector) throws Exception {
                        List<Double> sensor_1, sensor_2, sensor_3, sensor_4, sensor_5, sensor_6,
                                sensor_7, sensor_8, sensor_9, sensor_10;
                        sensor_1 = sensor_2 = sensor_3 = sensor_4 = sensor_5 = sensor_6 = sensor_7 =
                                sensor_8 = sensor_9 = sensor_10 = new ArrayList<>();

                        // Creates an array list for every column or sensor with 100 values
                        for (Tuple11 element : iterable) {
                            sensor_1.add((Double) element.f1);
                            sensor_2.add((Double) element.f2);
                            sensor_3.add((Double) element.f3);
                            sensor_4.add((Double) element.f4);
                            sensor_5.add((Double) element.f5);
                            sensor_6.add((Double) element.f6);
                            sensor_7.add((Double) element.f7);
                            sensor_8.add((Double) element.f8);
                            sensor_9.add((Double) element.f9);
                            sensor_10.add((Double) element.f10);
                        }

                        // Obtains the IQR for every sensor
                        Double sensor_1_IQR, sensor_2_IQR, sensor_3_IQR, sensor_4_IQR, sensor_5_IQR,
                                sensor_6_IQR, sensor_7_IQR, sensor_8_IQR, sensor_9_IQR, sensor_10_IQR;
                        sensor_1_IQR = new IQR().IQR(sensor_1);
                        sensor_2_IQR = new IQR().IQR(sensor_2);
                        sensor_3_IQR = new IQR().IQR(sensor_3);
                        sensor_4_IQR = new IQR().IQR(sensor_4);
                        sensor_5_IQR = new IQR().IQR(sensor_5);
                        sensor_6_IQR = new IQR().IQR(sensor_6);
                        sensor_7_IQR = new IQR().IQR(sensor_7);
                        sensor_8_IQR = new IQR().IQR(sensor_8);
                        sensor_9_IQR = new IQR().IQR(sensor_9);
                        sensor_10_IQR = new IQR().IQR(sensor_10);

                        // Scores every value using the respective IQR and creates a mixed Tuple with
                        // original values and scored values
                        for (Tuple11 element : iterable) {
                            collector.collect(new Tuple21<>(
                                    (String) element.f0,
                                    (Double) element.f1,
                                    scoring(sensor_1_IQR, (Double) element.f1),
                                    (Double) element.f2,
                                    scoring(sensor_2_IQR, (Double) element.f2),
                                    (Double) element.f3,
                                    scoring(sensor_3_IQR, (Double) element.f3),
                                    (Double) element.f4,
                                    scoring(sensor_4_IQR, (Double) element.f4),
                                    (Double) element.f5,
                                    scoring(sensor_5_IQR, (Double) element.f5),
                                    (Double) element.f6,
                                    scoring(sensor_6_IQR, (Double) element.f6),
                                    (Double) element.f7,
                                    scoring(sensor_7_IQR, (Double) element.f7),
                                    (Double) element.f8,
                                    scoring(sensor_8_IQR, (Double) element.f8),
                                    (Double) element.f9,
                                    scoring(sensor_9_IQR, (Double) element.f9),
                                    (Double) element.f10,
                                    scoring(sensor_10_IQR, (Double) element.f10)
                            ));
                        }
                    }
                });

        DataStream<InfluxDBPoint> dataStream = source.map(
                new RichMapFunction<Tuple21<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double>, InfluxDBPoint>() {
                    @Override
                    public InfluxDBPoint map(Tuple21<String, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double, Double> tuple) throws Exception {
                        String measurement = "anomaly_detection";
                        long timestamp = System.currentTimeMillis();
                        HashMap<String, String> tags = new HashMap<>();
                        HashMap<String, Object> fields = new HashMap<>();
                        fields.put("date", tuple.f0);
                        fields.put("sensor_1", tuple.f1);
                        fields.put("sensor_1_score", tuple.f2);
                        fields.put("sensor_2", tuple.f3);
                        fields.put("sensor_2_score", tuple.f4);
                        fields.put("sensor_3", tuple.f5);
                        fields.put("sensor_3_score", tuple.f6);
                        fields.put("sensor_4", tuple.f7);
                        fields.put("sensor_4_score", tuple.f8);
                        fields.put("sensor_5", tuple.f9);
                        fields.put("sensor_5_score", tuple.f10);
                        fields.put("sensor_6", tuple.f11);
                        fields.put("sensor_6_score", tuple.f12);
                        fields.put("sensor_7", tuple.f13);
                        fields.put("sensor_7_score", tuple.f14);
                        fields.put("sensor_8", tuple.f15);
                        fields.put("sensor_8_score", tuple.f16);
                        fields.put("sensor_9", tuple.f17);
                        fields.put("sensor_9_score", tuple.f18);
                        fields.put("sensor_10", tuple.f19);
                        fields.put("sensor_10_score", tuple.f20);

                        return new InfluxDBPoint(measurement, timestamp, tags, fields);
                    }
                }
        );

        System.out.println("Storing results into InfluxDB");
        InfluxDBConfig influxDBConfig = InfluxDBConfig.builder("http://localhost:8086", "admin", "admin", "db_anomalies")
                .batchActions(1000)
                .flushDuration(100, TimeUnit.MILLISECONDS)
                .enableGzip(true)
                .build();

        dataStream.addSink(new InfluxDBSink(influxDBConfig));

        env.execute("Anomaly Detection");
    }
}
