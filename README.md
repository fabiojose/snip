![Snip](./artwork/snip-horizontal-s.png)

# The easiest way to scaffold projects

_Are you tired of complex placeholders that breaks your code?_

- 🐦 __Snip__ is the answer! 🐦

🤲 If you (like me) is tired to losing your time with magical scaffolding
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
- [Rust](https://doc.rust-lang.org/reference/identifiers.html)
- [Ruby](https://ruby-doc.org/docs/ruby-doc-bundle/Manual/man-1.4/syntax.html#ident)

🧠 But Snip is not limited to programming languages. We are able to scaffold
any file that is not binary.

> can you imagine the possibilities?

👟 With Snip _we..._:

- have no special structure
- have no special files
- have at same time a template and an application
- have a fancy name for templates: ❤️ __templation__ ❤️
- are able to build and run our templation, naturally without any
  kind of special tool or dependency
- can easily refactor and update our templation

## Getting Stated

### Install

TODO

### Scaffold


- From remote templation hosted in Github:
```bash
# TODO
```

- If you have a templation int the local file system:
```bash
# TODO
```

### Build

- 🎈 Build your brand new app
```bash
# TODO
```

- 🧰 Open it in your favorite IDE . . .

## Placeholders

Every placeholder must follow this pattern:
- `__[0-9a-zA-Z]+_[0-9a-zA-Z]+_`

Simplifing...:
- ✅ valid placeholders names:
  - `__c_myplaceholder_`
  - `__cd_SomePlaceHolder_`
  - `__cde_place0_`
  - `__AB_Place90_`
- ❌ invalid ones:
  - `__c_place-holder_`
  - `_d_placeholder_`
  - `__e_place holder_`
  - `__aplaceholder_`

And you may use placeholders in directory name, file name and file content.

- directory name:
  - `/path/to/some/directory/src/__s_namespace_`
  - `/path/to/directory/resources/__c_placeholderController`
  - `/src/__s_namespace_/__d_domain_/__c_classname`
- file name:
  - `/path/to/src/main/resources/__c_entity.avro`
  - `/path/to/src/main/java/__s_namespace_/controller/__d_domainGet.java`
  - ``

### Build-in Placeholders

- `__s_namespace_`: application's namespace or package
- `__s_app_`: application's name
- `__s_version_`: application's version

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