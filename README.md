# SDP

## Team

| Name                 | Email |
|----------------------|
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
