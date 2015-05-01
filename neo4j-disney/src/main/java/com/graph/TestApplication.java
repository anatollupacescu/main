package com.graph;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Configuration
    @EnableNeo4jRepositories(basePackages = "com.graph")
    static class ApplicationConfig extends Neo4jConfiguration {

        public ApplicationConfig() {
            setBasePackage("com.graph");
        }

        @Bean
        GraphDatabaseService graphDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("target/accessingdataneo4j.db");
        }
    }
}
