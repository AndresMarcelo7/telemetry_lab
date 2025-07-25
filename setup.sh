#!/bin/sh

cd python-telemetry-lab
dockerd &
sleep 5
docker pull alpine:3.20
docker-compose up -d