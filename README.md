Intake
======

Intake is a Java library for parsing user commands.

Commands can be registered via two ways:

* Methods can be annotated with `@Command`.
* A class can implement `CommandCallable`.

Several commands can be registered on an implementation of `Dispatcher`, which
can parse input and execute the correct command.

Sub-commands are supported. This is because all `Dispatcher`s are also
`CommandCallable`s, so you can add a dispatcher to another dispatcher to another
dispatcher!

In addition, Intake supports completion of arguments, although currently the
annotation method of command registration does not support the completion of
parameters in a command. You can complete sub-commands, however.

The API supports a rich amount of metadata about each command, allowing the
inspection of registered commands, their parameters, their permissions, and
their usage instructions.

History
-------

This library is taken from [WorldEdit](https://github.com/sk89q/worldedit) 6.x.
You may be familiar with the older command framework in WorldEdit — while this
library is based off of that framework, it has been taken from a newer version of
WorldEdit, which at the time of writing, has not been released.

Usage
-----

Please be aware that the library is currently a work in progress. While it was
merged into WorldEdit's `main` branch, it was done after more than a year
since it was originally written. There may be some small bugs here and
and there is currently a dire lack of unit tests.

There is currently some legacy code as well, which tends to be stable but very
poorly documetned.

It is strongly recommended that if you use this library in a plugin or mod
for Minecraft, the library should be shaded and the `com.sk89q.intake` package
relocated to something internal to your project (i.e. `myproject.internal.intake`).

Intake currently requires some version of Google Guava that is equal to or
newer than 10.0.1. Guava is not bundled with the library.

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

No release of Intake has been made. Only snapshot builds are available.

**Note:** The API is subject to change in snapshot builds.

Backwards Compatibility
-----------------------

An effort was made to make transitioning from the older command framework easier
with fairly minimal changes required to the code. If you are planning to convert
from the old command framework to this version, be aware of these changes:

* The `CommandPermissions` annotation was renamed to `Require`.
* The `WrappedCommandException` class was renamed to `InvocationCommandException`.
* A considerable amount of code was split up and decoupled. Now, `SimpleDispatcher`
  is the main class for registering commands and the "annotated commands" support
  is separate.
* The old `CommandManager` has been replaced with a newer `ParametricBuilder`
  class that is more powerful, allowing for injection of arbitrary types
  (i.e. `yourMethod(String name, int age)` instead of just
  `yourMethod(Player player, CommandContext args)`). There is some
  backwards compatibility if `LegacyCommandsHelper` is registered
  on the instance of `ParametricBuilder`.
* `Injector` was removed. Pass instances to `ParametricBuilder`.

Examples
--------

Define some commands:

```java
public class MyCommands {
    @Command(aliases = "age", desc = "Set age")
    @Require("example.age")
    public void setAge(User user, @Optional @Default("20") int age) {
        user.setAge(age);
        user.message("Your age was set to: " + age);
    }
    
    @Command(aliases = "love", desc = "Broadcast your love")
    @Require("example.love")
    public void broadcastLove(User user, String name) {
        Platform.broadcast(user.getName() + " loves " + name + "!");
    }
}
```

We may want to be able to check permisisons:

```java
public class ActorAuthorizer implements Authorizer {
    @Override
    public boolean testPermission(CommandLocals locals, String permission) {
        User sender = locals.get(User.class);
        if (sender == null) {
            throw new RuntimeException("Uh oh! A user didn't use this command.");
        } else {
            return sender.testPermission(permission);
        }
    }
}
```

Create a default argument completer for parameters:

```java
public class MyCompleter implements CommandCompleter {
    @Override
    public List<String> getSuggestions(String arguments, CommandLocals locals) {
        List<String> suggestions = new ArrayList<String>();
        Collection<User> users = Platform.getConnectedUsers();
        for (User user : users) {
            if (user.getName().toLowerCase().startsWith(arguments.toLowerCase().trim())) {
                suggestions.add(user.getName());
            }
        }
        return suggestions;
    }
}
```

Perhaps use some custom types of parameters:

```java
public class MyBinding extends BindingHelper {
    @BindingMatch(type = MyObject.class,
                  behavior = BindingBehavior.CONSUMES,
                  consumedCount = 1,
                  provideModifiers = true)
    public MyObject getString(ArgumentStack context, Annotation[] modifiers) {
        return Platform.parseAsMyObject(context.next());
    }
}
```

Perhaps catch exceptions thrown by commands:

```java
public class MyExceptionConverter extends ExceptionConverterHelper {
    @ExceptionMatch
    public void convert(NumberFormatException e) throws CommandException {
        throw new CommandException("Number expected! Instead, I got something else.");
    }
}
```

Then build the dispatcher:

```java
ParametricBuilder builder = new ParametricBuilder();
builder.setAuthorizer(new MyAuthorizer());
builder.setDefaultCompleter(new MyCompleter());
builder.addBinding(new MyBinding());
builder.addExceptionConverter(new MyExceptionConverter());

// Add support for commands that were created for the older
// command framework in WorldEdit
builder.addInvokeListener(new LegacyCommandsHandler());

Dispatcher dispatcher = new CommandGraph()
        .builder(builder)
            .commands()
                .registerMethods(new MyCommands())
                .registerMethods(new MoreCommands())
                .group("debug", "dbug") // Subcommands
                    .describeAs("Debugging commands")
                    .registerMethods(new DebuggingCommands())
                    .parent()
                .graph()
        .getDispatcher();
```

Execute a command:

```java
CommandLocals locals = new CommandLocals();
locals.put(User.class, caller);
String[] parentCommands = new String[0]; // No parent commands

// Note: Prefix characters (/, ., etc.) must be removed
dispatcher.call("love bob", locals, String[] parentCommands);
```

Inspect a command:

```java
CommandMapping mapping = dispatcher.get("love");
String desc = mapping.getDescription().getShortDescription();
```

Compiling
---------

Use Maven 3 to compile Intake.

    mvn clean package

Contributing
------------

Intake is available under the GNU Lesser General Public License.

We happily accept contributions, especially through pull requests on GitHub.

Links
-----

* [Visit our website](http://www.enginehub.org/)
* [IRC channel](http://skq.me/irc/irc.esper.net/sk89q/) (#sk89q on irc.esper.net)


----------
