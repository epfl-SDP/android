# Pawnies – AR Chess

[![Maintainability](https://api.codeclimate.com/v1/badges/e804775d6b20006a3778/maintainability)](https://codeclimate.com/github/epfl-SDP/android/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e804775d6b20006a3778/test_coverage)](https://codeclimate.com/github/epfl-SDP/android/test_coverage)

🎨 [Figma mockups](https://www.figma.com/file/JGLgtpIJcPW7z4YKD4nLeH/Android) 🎨

## Team

| Name                 | Email |
|----------------------|-------|
| Lars Barmettler      | lars.barmettler@epfl.ch |
| Matthieu Burguburu   | matthieu.burguburu@epfl.ch |
| Chau Ying Kot        | chau.kot@epfl.ch |
| Fouad Mahmoud        | fouad.mahmoud@epfl.ch |
| Alexandre Piveteau   | alexandre.piveteau@epfl.ch |
| Mohamed Badr Taddist | mohamed.taddist@epfl.ch |

## Setup

This project depends on a specific release of the Jacoco library, which has been updated to provide coverage support for Jetpack Compose. This release is available on [GitHub](https://github.com/epfl-SDP/jacoco-compose), and requires the use of the GitHub Apache Maven Package Repository. Therefore, you are required to add the following to your `~/.gradle/gradle.properties` file to access the Maven package.

```properties
githubJacocoUsername=YourGitHubUsername
# Requires at least the read:packages scope.
githubJacocoPassword=YourGitHubPersonalAccessToken
```

As this project uses Google Firebase, you will also need to provide your own `google-services.json`.

This file can be generated from the Firebase Console (Pawnies Project -> Project Settings -> Your apps -> SDK setup and configuration -> google-services.json).

It must be placed at `./mobile/google-services.json`

## Architecture and organization overview

This project uses Jetpack Compose and is written in Kotlin. The main packages are organized as follows :

- `ch.epfl.sdp.mobile.infrastructure` features the repositories and their implementations;
- `ch.epfl.sdp.mobile.application` contains the domain facades and business logic;
- `ch.epfl.sdp.mobile.ui` contains all the stateless user interface; and
- `ch.epfl.sdp.mobile.state` contains the viewmodels which bind the stateless user interface to our application logic.
