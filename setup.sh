#!/bin/sh

cd python-telemetry-lab
dockerd &
sleep 5
docker-compose up -d