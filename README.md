Intake
======

Intake is a Java library for processing user commands.

* Register commands using annotations on methods in a class.
* Or use a one-command-per-class scheme.

This library is used in [WorldEdit](https://github.com/sk89q/worldedit),
[WorldGuard](https://github.com/sk89q/worldguard), and other projects;
however, the original command code used in WorldEdit 5.x and earlier
is not included in this project. Instead, this project contains
the command code used in WorldEdit 6.x and beyond, which bears strong
resemblance to the old code but a significant amount has been
rewritten.

It is strongly recommended that if you use this library in a plugin or mod
for Minecraft, the library should be shaded and the `com.sk89q.intake` package
relocated to something internal to your project (`myproject.internal.intake`).

Documentation is currently short as the project is a work-in-progress until
WorldEdit 6 is released. There may be various minor bugs and a lack of
unit tests until then.

**Note:** The API is subject to change in future versions and it currently
contains a lot of legacy code.

Dependencies
------------

Intake currently requires some version of Guava that is equal to or newer than
10.0.1.

Backwards Compatibility
-----------------------

There have been some changes to the library since the original version.

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

```
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