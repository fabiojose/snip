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

- [Java](https://docs.oracle.com/javase/tutorial/java/nutsandbolts/variables.html#naming),
  [JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Grammar_and_types#variables),
  [Python](https://www.w3schools.com/python/gloss_python_variable_names.asp),
  [C#](https://docs.microsoft.com/en-us/dotnet/csharp/programming-guide/inside-a-program/identifier-names),
  [Golang](https://golang.org/ref/spec#Identifiers),
  [Rust](https://doc.rust-lang.org/reference/identifiers.html),
  [Ruby](https://ruby-doc.org/docs/ruby-doc-bundle/Manual/man-1.4/syntax.html#ident)

🧠 But Snip is not limited only to programming languages. We are able to
process any file that is not binary.

> can you imagine the possibilities?

👟 With Snip _we..._:

- have no special structure
- have no special files
- have at same time a template and an application
- have a fancy name for templates: ❤️ __templation__ ❤️
- are able to build and run our templation, naturally, without any
  kind of special tool or dependency
- can easily refactor and update our templation

## Getting Started

### Install Snip

TODO

### Scaffold

- From remote templation hosted in Github:
```bash
# TODO
```

- If you have a templation at localhost file system:
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

Simplifying...:
- ✅ valid placeholders names are:
  - `__c_myplaceholder_`
  - `__cd_SomePlaceHolder_`
  - `__cde_place0_`
  - `__AB_Place90_`
- ❌ invalid ones:
  - `__c_place-holder_`
  - `_d_placeholder_`
  - `__e_place holder_`
  - `__aplaceholder_`

And you may use placeholders in directory names, file names and file content.

- directory name:
  - /path/to/some/directory/src/`__s_namespace_`
  - /path/to/directory/resources/`__c_placeholder_`Controller
  - /src/`__s_namespace_`/`__d_domain_`/`__c_classname_`
- file name:
  - /path/to/src/main/resources/`__c_entity_`.avsc
  - /path/to/src/main/java/`__s_namespace_`/controller/`__d_domain_`Get.java
- file content:
```java
package __s_namespace_;
public class __d_domain_Get{

}
```

### Built-in Placeholders

- `__s_namespace_`: application's namespace or package
- `__s_app_`: application's name
- `__s_version_`: application's version

#### Processing of `__s_namespace_`

When `__s_namespace_` is used in directories names, there is a special
processing.

__Example__:

When the templation has a directory structure like this:

```
.
└── src
    └── __s_namespace_
```

And `__s_namespace_=com.example`. After Snip scaffolding, the
new directory structure will be:

```
.
└── src
    └── com
        └── example
```

### Custom Placeholders

As long as you follow the [placeholder pattern](#placeholders) and do not use
any [built-in placeholders](#built-in-placeholders), you may define your own
placeholders.

You must make clear in the templation docs what are the placeholders names
and the expected values.

To pass your custom placeholder to Snip scaffold is so simple, just use the
`-p` option as many as you want.

```bash
snip c <options> \
  -p '__c_comment_=Some comments to use' \
  -p "__c_author_=$USER" \
  -p '__c_domain=payments'
```

## How to Create a Templation?

To create your templation is so simple, you must refactor an existing project
to use some of [built-in placeholders](#built-in-placeholders) and
[your own](#custom-placeholders).

Examples:

TODO
- Java 11 with Gradle
- Java 11 with Maven
- Python 3

### `.snipignore`

Always there are files and folders that you want to ignore during the scaffolding
process, for this you may create a `.snipignore` file.

Example:
```gitignore
## Folders ##
.git/
.gradle/
build/

## Files ##
.project
.classpath
my-file.txt

## Wildcards ##
*.png
*.iml
*.iws
```

### `.snip.yml`

To declare your custom placeholders and post scaffold scripts to execute,
you may create the `.snip.yml` within your templation repository.

Example:
```yaml
description: |
  Java 11 with Gradle
placeholders:
  strict: yes # Every placeholder in the spec must be present
  spec:
    - name: __c_author_
      pattern: ".+" # [optional] Java Regex to validate the value: https://cutt.ly/OxOZBZY
      label: Author Name
    - name: __c_domain_
      pattern: '[A-Z][\w]+' # [optional] Java Regex to validate the value: https://cutt.ly/OxOZBZY
      label: Class name for Domain
post:
  script: # The base directory for scripts is --directory
    linux: # To run on Linux & MacOS
      - ls -alh .
      - ./gradlew clean test --info
    windows: # to run on Windows
      - .\gradlew.bat clean test --info
```

## Installation

The following instructions per O.S. may be used to brand new
installations or updates.

### Linux

#### Binary

| Method    | Command                                                                               |
|:----------|:--------------------------------------------------------------------------------------|
| **curl**  | `sh -c "$(curl -fsSL https://raw.githubusercontent.com/kattlo/snip/main/install.sh)"` |
| **wget**  | `sh -c "$(wget -O- https://raw.githubusercontent.com/kattlo/snip/main/install.sh)"`   |
| **fetch** | `sh -c "$(fetch -o - https://raw.githubusercontent.com/kattlo/snip/main/install.sh)"` |

__Manual inspection__

It's a good idea to inspect the install script from projects you don't yet know. You can do
that by downloading the install script first, looking through it so everything looks normal,
then running it:

```shell
wget https://raw.githubusercontent.com/kattlo/snip/main/install.sh
sh install.sh
```

#### Packages

- `.deb` [are available]()
- `.rpm` [are available]()

### MacOS

- Download the lates [MacOS binary version]()
- Save it as `snip`
- Run the following commands:
```shell
sudo chmod +x snip
sudo mv snip /usr/local/bin/snip
snip -h
```

### Windows

#### Using the Setup

- Download the latest [Windows setup version]()
- Open the downloaded setup and follow the instructions

#### Binary

- download the latest [zip for Windows]()
- unzip it
- copy `VCRUNTIME140.dll` to `C:\Windows\System32\`
- get the absolute path to that unzipped directory
- add the absolute path of Snip to your user `PATH` environment variable
- open the prompt and type: `snip -h`

### All

- Install the [Java 11 for your O.S.](https://adoptopenjdk.net/releases.html)
- Download the latest [über-jar]()
- Run it:
```bash
# Replace '<version>' with the downloaded version
java -jar snip-v<version>-all.jar -h
```

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
./gradlew clean build
  -Dquarkus.package.type=native \
  -Dquarkus.native.additional-build-args=-H:EnableURLProtocols=https,-H:IncludeResources='.*json$'
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:
```shell script
./gradlew clean build \
  -Dquarkus.package.type=native \
  -Dquarkus.native.container-build=true \
  -Dquarkus.native.additional-build-args=-H:EnableURLProtocols=https,-H:IncludeResources='.*json$'
```

You can then execute your native executable with: `./build/code-with-quarkus-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related guides

- Picocli ([guide](https://quarkus.io/guides/picocli)): Develop command line applications with Picocli

## Notes

- <div>Snip icon made by <a href="https://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
