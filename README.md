![Snip](./artwork/snip-horizontal-s.png)

# The easiest way to scaffold projects

_Are you tired of complex placeholders that breaks your code?_

- 🐦 __Snip__ is the answer! 🐦

If you, like me, is tired to losing your time with magical scaffolding
tools with weird syntaxes and tons of useless functions, install
Snip and scaffold now a new application.

> because at the of the day we just want a new app based on some template to
start our programming

💡 Why use `${placeholder}`, `{{placeholder}}` or `[placeholder]`
if we can just use `__s_placeholder_`? 

🦊 In any modern language, underscore `_` is a valid name identifier,
because of that we just employ it in the placeholder syntax.

- [Java](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/variables.html#naming)
- [JavaScritp](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types#variables)
- [Python](https://www.w3schools.com/python/gloss_python_variable_names.asp)
- [C#](https://docs.microsoft.com/en-us/dotnet/csharp/programming-guide/inside-a-program/identifier-names)
- [Golang](https://golang.org/ref/spec#Identifiers)
- [Rust]()
- [Ruby]()

🧠 But Snip is not limited to programming languages. We are able to scaffold
any file that is not binary.

> can you imagine the possibilities?

👟 With Snip _we..._:

- have no special structure
- have no special files
- have at same time a template and an application
- have a fancy name for templates: __templation__ ❤️
- are able to build and run our templation, naturally without any
  kind of special tool or dependency
- can easily refactor and update our templation

## Getting Stated

TODO

## Placeholders

TODO

### Custom

TODO

## How to Create a Templation?

TODO

## Required Options

TODO

## Quarkus

### Packaging and Running Snip from Source

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
 
## Notes

- <div>Snip icon made by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>