#!/bin/bash

kafka-topics --create --topic job-topic --bootstrap-server kafka:9092
kafka-topics --create --topic job-retry-topic --bootstrap-server kafka:9092
kafka-topics --create --topic job-dlq-topic --bootstrap-server kafka:9092