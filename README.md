![Snip](./artwork/snip-horizontal-s.png)

# The easiest way to scaffold projects

Are you tired of complex placeholders that breaks your code?

- __snip__ is the answer! 🐦

If you, like me, is tired to loosing your time with magical scaffolding
tools with weird synthaxes and tons of useless functions. Because
at the of the day we just want a new app based in some template to
start programming.

Why user `${placeholder}`, `{{placeholder}}` or `[placeholder]`
if you can use `__s_placeholder_`?

In any modern language, underscore `_` is a valid name identifier,
because of that we just employ it at placeholder syntax.

- [Java](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/variables.html#naming)
- [JavaScritp](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types#variables)
- [Python](https://www.w3schools.com/python/gloss_python_variable_names.asp)
- [C#](https://docs.microsoft.com/en-us/dotnet/csharp/programming-guide/inside-a-program/identifier-names)
- [Golang](https://golang.org/ref/spec#Identifiers)

With Snip placeholder synthax, we:

- have no special structure
- have no special files
- have at same time we have a template and an application
- have a fancy name for templates: __templation__ ❤️
- can build and run our templation
- can easily refactor and update our templation

## Getting Stated

TODO

## Placeholders

TODO

### Custom

TODO

## Required Options

TODO

## Quarkus

### Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

### Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related guides

- Picocli ([guide](https://quarkus.io/guides/picocli)): Develop command line applications with Picocli
