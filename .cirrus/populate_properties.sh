#!/bin/sh

# Takes $GITHUB_USERNAME and $GITHUB_TOKEN and writes them to the ~/.gradle/gradle.properties
mkdir ~/.gradle
echo "githubJacocoUsername=$GITHUB_USERNAME\ngithubJacocoPassword=$GITHUB_TOKEN" > ~/.gradle/gradle.properties
