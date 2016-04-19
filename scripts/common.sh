#!/bin/sh

suffix=tmaki
domain=cfapps.pez.pivotal.io
git_url=https://github.com/making/metflix-config
git_label=spring-cloud-services

dir="$dir membership"
dir="$dir recommendations"
dir="$dir ui"

service="$service config-server"
service="$service eureka-server"
service="$service hystrix-dashboard"