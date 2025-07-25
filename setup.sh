#!/bin/sh

cd python-telemetry-lab
dockerd &
sleep 5
docker pull alpine:latest
docker-compose up -d