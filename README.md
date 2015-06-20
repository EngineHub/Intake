# Intake

Intake is a IoC-oriented command parsing library.

Consider the following command:

```
/planet settype pluto dwarfplanet
```

Consider a theoretical `Planet` class and `CelestialType` enum in the project. The command above presumably would execute the following code:

```java
pluto.setType(CelestialType.DWARF_PLANET);
```

Rather than write argument parsing code in the routine for that command, it'd be simpler to simply request a `Planet` and a `CelestialType` from the user, so it'd be possible to write the command like so:

```java
void setType(Planet planet, CelestialType type) {
	planet.setType(type);
}
```

The purpose of Intake is to make that possible.

## Overview

Intake contains several components:

* A set of generic command library (free of any IoC) with support for command introspection, nested commands, and argument completion.
* The IoC portion that allows the registration of bindings and provider. The IoC portion has an API very similar to the Google Guice library.
* A "parametric binding" component that applies the IoC component to classes with methods that that have been annotated with `@Command` and generates objects that work with the generic command library.
* A barebones fluent API that works with the command library and the parametric binding component.

### Command Library

The command library contains some crucial interfaces:

* `CommandCallable` represents a command that can be called. There is a `call(...)` method that accepts arguments.
* `Dispatcher` represents a group of commands, which is used for nested commands.

It is possible to only use this part of Intake:

```java
public class SetTypeCommand implements CommandCallable {
    @Override
    public boolean call(String arguments, Namespace namespace, List<String> parentCommands) throws CommandException, InvocationCommandException, AuthorizationException {
        String[] split = arguments.split(" ");
        if (split.length != 2) {
            throw new InvalidUsageException("Not enough arguments!", this);
        }

        String planetName = split[0];
        String typeName = split[1];
        
        // ...etc
        
        return true;
    }

    // ...
}
```

### IoC Component

The IoC component primarily parses arguments into Java objects:

```java
Injector injector = Intake.createInjector();
injector.install(new PrimitivesModule());
injector.install(new DomainObjectsModule());

Builder argParserBuilder = new Builder(injector);
argParserBuilder.addParameter(Planet.class);
argParserBuilder.addParameter(CelestialType.class);

ArgumentParser parser = argParserBuilder.build();
parser.parseArguments(Arguments.of("pluto", "dwarfplanet")));
```

`DomainObjectsModule` might look like this:

```java
public class DomainObjectsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Planet.class).toProvider(new PlanetResolver());
        bind(CelestialType.class).toProvider(new EnumResolver<CelestialType>(CelestialType.class);
    }

}
```

### Parametric Commands

Define some commands:

```java
public class PlanetCommands {
    @Command(aliases = "settype", desc = "Set the type of an object")
	public void setType(Planet planet, CelestialType type) {
		planet.setType(type);
	}
}
```

Then build the commands with the fluent API:

```java
Injector injector = Intake.createInjector();
injector.install(new PrimitivesModule());
injector.install(new DomainObjectsModule());

ParametricBuilder builder = new ParametricBuilder(injector);

Dispatcher dispatcher = new CommandGraph()
        .builder(builder)
            .commands()
                .group("planet") // Subcommands
                    .registerMethods(new PlanetCommands())
                    .parent()
                .graph()
        .getDispatcher();
```

Execute a command:

```java
Namespace namespace = new Namespace();

// Note: Prefix characters (/, ., etc.) must be removed
dispatcher.call("planet settype pluto dwarfplanet", namespace, Collections.emptyList());
```

## Usage

There are two major versions of Intake:

* 3.x (available via Git tags)
* 4.x (in the `master` branch)

There was a major overhaul in 4.0 to decompule the IoC portion from the parametric binding. Previously they were an inseperable couple.

The documentation in the wiki is for 3.x. The examples in this README are for 4.x.

### Maven

Currently, Intake is available in sk89q's Maven repository:

```xml
<repositories>
  <repository>
    <id>maven.sk89q.com</id>
    <url>http://maven.sk89q.com/repo/</url>
  </repository>
</repositories>
```

As a dependency,

```xml
<dependencies>
  <dependency>
    <groupId>com.sk89q</groupId>
    <artifactId>intake</artifactId>
    <version>{version here}</version>
  </dependency>
</dependencies>
```

If you plan to use 3.x, use 3.1.2 for the version.

**Note:** The API is subject to change in snapshot builds.

### Migration

If you are coming from the command framework that was used in WorldEdit since 2010, then there have been many changes.

If you are moving from 3.x to 4.x, then the changes have not been too major (except for registering bindings). Some classes were moved around or renamed.

## Documentation

Documentation is currently a work-in-progress.

Find it here: https://github.com/sk89q/Intake/wiki

The documentation in the wiki is for 3.x. The examples in this README are for 4.x.

## Compiling

Use Maven 3 to compile Intake.

    mvn clean package

## Contributing

Intake is available under the GNU Lesser General Public License.

We happily accept contributions, especially through pull requests on GitHub.

## Links

* [Visit our website](http://www.enginehub.org/)
* [IRC channel](http://skq.me/irc/irc.esper.net/sk89q/) (#sk89q on irc.esper.net)