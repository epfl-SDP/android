# SDP

[![Maintainability](https://api.codeclimate.com/v1/badges/e804775d6b20006a3778/maintainability)](https://codeclimate.com/github/epfl-SDP/android/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/e804775d6b20006a3778/test_coverage)](https://codeclimate.com/github/epfl-SDP/android/test_coverage)

## Team

| Name                 | Email |
|----------------------|-------|
| Lars Barmettler      |
| Matthieu Burguburu   |
| Chau Ying Kot        |
| Fouad Mahmoud        |
| Alexandre Piveteau   | alexandre.piveteau@epfl.ch |
| Mohamed Badr Taddist |

## Setup

This project depends on a specific release of the Jacoco library, which has been updated to provide
coverage support for Jetpack Compose. This release is available
on [GitHub](https://github.com/epfl-SDP/jacoco-compose), and requires the use of the GitHub Apache
Maven Package Repository. Therefore, you are required to add the following to
your `~/.gradle/gradle.properties` file to access the Maven package.

```properties
githubJacocoUsername=YourGitHubUsername
# Requires at least the read_packages scope.
githubJacocoPassword=YourGitHubPersonalAccessToken
```
