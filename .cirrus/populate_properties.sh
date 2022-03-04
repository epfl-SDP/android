#!/bin/sh

# Takes $GITHUB_USERNAME and $GITHUB_TOKEN and writes them to the ~/.gradle/gradle.properties
mkdir ~/.gradle
echo "githubJacocoUsername=$GITHUB_USERNAME\ngithubJacocoPassword=$GITHUB_TOKEN" > ~/.gradle/gradle.properties

# Takes $GOOGLE_SERVICES and writes it to ./mobile/google-services.json
echo $GOOGLE_SERVICES > .mobile/google-services.json