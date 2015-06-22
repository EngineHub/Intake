# Intake

Intake is a IoC-oriented command parsing library.

Consider the following command:

```
/body settype pluto dwarfplanet
```

Consider a theoretical `Body` class and `CelestialType` enum in the project. The command above presumably would execute the following code:

```java
pluto.setType(CelestialType.DWARF_PLANET);
```

Rather than write argument parsing code in the routine for that command, it'd be simpler to simply request a `Body` and a `CelestialType` from the user, so it'd be possible to write the command like so:

```java
void setType(Body body, CelestialType type) {
	body.setType(type);
}
```

The purpose of Intake is to make that possible.

## Overview

Intake consists of four parts:

### Command Framework

The command framework consists of some basic interfaces and classes:

* Commands are modeled by `CommandCallable`
* Groups of sub-commands are modeled by `Dispatcher`
* Descriptions of commands are modeled by `Description`
* Individual parameters (for introspection) are modeled by `Parameter`
* Commands that can suggest completions are modeled by `CommandCompleter`
* Arguments (accessed as a stack) are represented by `CommandArgs`

There is also support for:

* Boolean single-character flags (`/command -f`)
* Value flags (`/command -v value`)
* Testing whether a user has permission to execute a command

The goal of the framework is to provide a compromise between a heavily-opinionated framework and a flexible one.

### Parameter Injection

The parameter injection framework provides IoC-oriented argument parsing and completion.

Raw use of the injection framework can be best seen in an example:

```java
Injector injector = Intake.createInjector();
injector.install(new PrimitivesModule());
injector.install(new UniverseModule());

Builder argParserBuilder = new Builder(injector);
argParserBuilder.addParameter(Body.class);
argParserBuilder.addParameter(CelestialType.class);

ArgumentParser parser = argParserBuilder.build();
parser.parseArguments(Arguments.of("pluto", "dwarfplanet")));
```

ArgumentParser finds "providers" for the Body and CelestialType Java types, which are then later utilized to create object instances from the provided arguments.

`UniverseModule` might look like this:

```java
public class UniverseModule extends AbstractModule {

    private final Universe universe;

    public UniverseModule(Universe universe) {
        this.universe = universe;
    }

    @Override
    protected void configure() {
        bind(Universe.class).toInstance(universe);
        bind(Body.class).toProvider(new BodyProvider(universe));
        bind(CelestialType.class).toProvider(
				new EnumProvider<CelestialType>(CelestialType.class));
    }

}
```

The parameter injection framework has strong similarity to Google Guice's API.

### Parametric Commands

The parametric command framework provides an opinionated method of defining commands using classes:

```java
public class UniverseCommands {

    @Command(aliases = "settype", desc = "Set the type of an object")
    public void setType(Body body, CelestialType type) {
        body.setType(type);
    }

}
```

It makes use of the parameter injection framework.

### Fluent API

There is also a fluent API that combines the command framework with the parametric command framework.


## Examples

To see some example code, check out the [example projects](intake-example/src/main/java/com/sk89q/intake/example).

## Usage

There are two major versions of Intake:

* 3.x (available via Git tags)
* 4.x (in the `master` branch)

There was a major overhaul in 4.0 to decompule the IoC portion from the parametric binding. Previously they were an inseperable couple.

The documentation in the wiki is for 3.x. The examples in this README are for 4.x.

### Resolution

Currently, Intake is available in sk89q's Maven repository:

```xml
<repositories>
  <repository>
    <id>maven.sk89q.com</id>
    <url>http://maven.sk89q.com/repo/</url>
  </repository>
</repositories>
```

or for Gradle users:

```groovy
repositories {
    maven { url "http://maven.sk89q.com/repo/" }
}
```

Depending on whether you want to use 3.x (3.1.2 is recommended) or 4.x, the Maven group ID will vary:

* 3.1.2:
	* Group ID: `com.sk89q`
	* Artifact ID: `intake`
	* Version: `3.1.2`
* 4.0:
	* Group ID: `com.sk89q.intake`
	* Artifact ID: `intake`
	* Version: `4.0-SNAPSHOT`

**Note:** The API is subject to change in snapshot builds.

### Migration

If you are coming from the command framework that was used in WorldEdit since 2010, then there have been many changes.

If you are moving from 3.x to 4.x, then the changes have not been too major (except for registering bindings). Some classes were moved around or renamed.

## Documentation

If you are using 3.x, find work-in-progress documentation at https://github.com/sk89q/Intake/wiki

However, if you are using 4.x, you are better looking at examples found in the repository.

## Compiling

Use Gradle to compile Intake.

If you are on Linux or Mac OS X, run the following in your terminal:

    ./gradlew clean build

If you are on Windows, run the following in your command prompt:

    gradlew clean build

## Contributing

Intake is available under the GNU Lesser General Public License.

We happily accept contributions, especially through pull requests on GitHub.

## Links

* [Visit our website](http://www.enginehub.org/)
* [IRC channel](http://skq.me/irc/irc.esper.net/sk89q/) (#sk89q on irc.esper.net)