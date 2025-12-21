package com.retisio.mill.templates

import os.Path

/**
 * Template generator for build.sc files.
 *
 * Generates Mill build configuration for spun-off services with:
 * - Java or Scala module configuration
 * - Dependencies (Pekko, Kafka, reactive Postgres, testing)
 * - Publish module for shared libraries
 * - Test configuration
 *
 * @param serviceName Name of service
 * @param contextMapPath Path to CONTEXT-MAP.md (for dependency detection)
 */
class BuildScTemplate(serviceName: String, contextMapPath: Path) {

  def generate(): String = {
    val packageName = serviceName.toLowerCase
    
    s"""import mill._
       |import mill.scalalib._
       |import mill.scalalib.publish._
       |
       |object ${packageName}Service extends JavaModule with PublishModule {
       |  def javaVersion = "21"
       |  
       |  def artifactName = "${packageName}-service"
       |  
       |  def publishVersion = "0.1.0"
       |  
       |  def pomSettings = PomSettings(
       |    description = "${serviceName} Service - Spun off from training repository",
       |    organization = "com.retisio",
       |    url = "https://github.com/RETISIO/${packageName}-service",
       |    licenses = Seq(License.`Apache-2.0`),
       |    versionControl = VersionControl.github("RETISIO", "${packageName}-service"),
       |    developers = Seq(
       |      Developer("retisio-team", "RETISIO Team", "https://github.com/RETISIO")
       |    )
       |  )
       |  
       |  def ivyDeps = Agg(
       |    // Pekko Actors
       |    ivy"org.apache.pekko::pekko-actor-typed:1.0.2",
       |    ivy"org.apache.pekko::pekko-stream:1.0.2",
       |    ivy"org.apache.pekko::pekko-http:1.0.1",
       |    
       |    // Pekko Serialization
       |    ivy"org.apache.pekko::pekko-serialization-jackson:1.0.2",
       |    
       |    // Pekko Cluster (if needed)
       |    // ivy"org.apache.pekko::pekko-cluster-typed:1.0.2",
       |    // ivy"org.apache.pekko::pekko-cluster-sharding-typed:1.0.2",
       |    
       |    // Kafka
       |    ivy"org.apache.kafka:kafka-clients:3.6.1",
       |    ivy"org.apache.pekko::pekko-connectors-kafka:1.0.0",
       |    
       |    // Reactive PostgreSQL
       |    ivy"io.vertx:vertx-pg-client:4.5.1",
       |    ivy"io.vertx:vertx-sql-client:4.5.1",
       |    
       |    // Database Migrations
       |    ivy"org.flywaydb:flyway-core:10.4.1",
       |    ivy"org.flywaydb:flyway-database-postgresql:10.4.1",
       |    
       |    // OpenTelemetry
       |    ivy"io.opentelemetry:opentelemetry-api:1.34.1",
       |    ivy"io.opentelemetry:opentelemetry-sdk:1.34.1",
       |    ivy"io.opentelemetry:opentelemetry-exporter-otlp:1.34.1",
       |    ivy"io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:1.32.0",
       |    
       |    // Logging
       |    ivy"ch.qos.logback:logback-classic:1.4.14",
       |    ivy"net.logstash.logback:logstash-logback-encoder:7.4",
       |    
       |    // Configuration
       |    ivy"com.typesafe:config:1.4.3",
       |    
       |    // JSON
       |    ivy"com.fasterxml.jackson.core:jackson-databind:2.16.1",
       |    ivy"com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1",
       |    ivy"com.fasterxml.jackson.module:jackson-module-parameter-names:2.16.1"
       |  )
       |  
       |  object test extends JavaTests {
       |    def ivyDeps = Agg(
       |      // JUnit 5
       |      ivy"org.junit.jupiter:junit-jupiter-api:5.10.1",
       |      ivy"org.junit.jupiter:junit-jupiter-engine:5.10.1",
       |      ivy"org.junit.jupiter:junit-jupiter-params:5.10.1",
       |      
       |      // AssertJ
       |      ivy"org.assertj:assertj-core:3.25.1",
       |      
       |      // Mockito
       |      ivy"org.mockito:mockito-core:5.8.0",
       |      ivy"org.mockito:mockito-junit-jupiter:5.8.0",
       |      
       |      // jqwik (Property-Based Testing)
       |      ivy"net.jqwik:jqwik:1.8.2",
       |      ivy"net.jqwik:jqwik-engine:1.8.2",
       |      
       |      // Pekko Testkit
       |      ivy"org.apache.pekko::pekko-actor-testkit-typed:1.0.2",
       |      ivy"org.apache.pekko::pekko-stream-testkit:1.0.2",
       |      
       |      // Testcontainers
       |      ivy"org.testcontainers:testcontainers:1.19.3",
       |      ivy"org.testcontainers:postgresql:1.19.3",
       |      ivy"org.testcontainers:kafka:1.19.3",
       |      
       |      // REST Assured (for API testing)
       |      ivy"io.rest-assured:rest-assured:5.4.0"
       |    )
       |    
       |    def testFramework = "com.github.sbt.junit.JupiterFramework"
       |  }
       |  
       |  // Integration tests
       |  object itest extends JavaTests {
       |    def ivyDeps = test.ivyDeps
       |    def testFramework = test.testFramework
       |  }
       |}
       |""".stripMargin
  }
}
