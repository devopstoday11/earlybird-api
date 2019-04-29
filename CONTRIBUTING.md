# Contributing

First off, welcome to EarlyBird! We're glad you're here and interested in working on an open source project.

## Steps to Contribute

1. Ensure you've installed gradle and >= Java 8 on your machine. 
1. Create an issue with a title and description of what you're planning on working on, and assign that issue to yourself
1. Fork this repository, and create a branch based on the title of your issue, e.g. `feature/delete-subscription`.
1. Make the necessary changes to the code base, and ensure it builds via `./gradlew build`. 
1. Open a Pull Request back into the original repository, with appropriate details about the changes you've made.

## Guidelines

- Style Guide is enforced via `./gradlew checkStyleMain checkStyleTest`. Our style guide can be integrated in IntelliJ, 
via the `intellij-java-google-style.xml` file at the root of this project. 
- It is preferred that changes made are new features, bug fixes, or other enhancements. Stylistic 
or opinion-based changes are discouraged. 

