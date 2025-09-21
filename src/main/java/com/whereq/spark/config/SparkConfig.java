package com.whereq.spark.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

    @Value("${spark.app-name}")
    private String appName;

    @Value("${spark.master}")
    private String master;

    @Bean
    public SparkSession sparkSession() {
        SparkConf conf = new SparkConf()
                .setAppName(appName)
                .setMaster(master)
                .set("spark.sql.adaptive.enabled", "true")
                .set("spark.sql.adaptive.coalescePartitions.enabled", "true")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
                .set("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
                .set("spark.ui.enabled", "true")
                .set("spark.ui.port", "4040");

        return SparkSession.builder()
                .config(conf)
                .getOrCreate();
    }
}