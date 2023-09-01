#!/bin/bash

rm builds/out-adv/* -rf

clj -M:runtime.storm.test.build

node builds/out-adv/storm-advanced-test.js
