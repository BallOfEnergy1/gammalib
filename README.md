# GammaLib
A library mod for use in my mods, finally shared to the world!

## Dependencies

To make full use of this library, [UniMixins](https://github.com/LegacyModdingMC/UniMixins/) (>0.2.1) and [GTNHLib](https://github.com/GTNewHorizons/GTNHLib) (>0.9.6) must be installed.

## Features

- Fast atomic classes using dynamic versioning to allow for faster libraries only found on later versions (using JVMDG and MR Jars) when using LWJGL3ify.
- Shadows several commonly used classes for concurrency and other utilities (JCTools, ByteBuddy + ByteBuddyAgent, and `sqlite-jdbc`).
- Generic ASM manager allowing for fast, efficient, and targeted ASM operations such as field access transformations, superclass transformations, method redirections, and more.
- Bytecode utilities such as class hierarchy tools, ASM annotation injection, bytecode stack reconstruction (experimental), and others.
- Several concurrency utilities, including a general interface for concurrent classes that wish to have inter-mod compatibility.
- `Unsafe` access system, allowing for dynamic `Unsafe` usage depending on system support/Java version.
- Watchdog system for detecting deadlocks in game threads and throwing upon detection with useful thread information.
- System for testing mods in high-load environments by providing a command to spawn arbitrary amounts of fake players.
- `PlayerJoinTime` handler to find the time at which a player joined a server (because Mojang didn't ever do it...).
- And more!

## Using this Library
You can shadow this library, just make sure that it won't cause issues if other mods also shadow/require this library (or if the library jar itself is installed alonside your mod that shadows it).

Jitpack must be added to your Gradle repositories:
```gradle
repositories {
  maven { url "https://jitpack.io" }
}
```

Gradle:
```gradle
dependencies {
  implementation('io.github.BallOfEnergy1:gammalib:version:dev') // Must be installed alongside your mod
}
```
OR
```gradle
dependencies {
  shadowImplementation('io.github.BallOfEnergy1:gammalib:version:dev') // Shadowed into your mod (no additional jar; *must* check licensing)
}
```

## Shadowed Dependencies
 - [ByteBuddy](https://github.com/raphw/byte-buddy) ([Maven](https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy)): Licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)
   - [ByteBuddy Agent](https://github.com/raphw/byte-buddy) ([Maven](https://mvnrepository.com/artifact/net.bytebuddy/byte-buddy-agent)): Licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)
 - [JCTools](https://github.com/JCTools/JCTools) ([Maven](https://mvnrepository.com/artifact/org.jctools/jctools-core)): Licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)
 - [JDBC](https://github.com/xerial/sqlite-jdbc) ([Maven](https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc)): Licensed under [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## License
I do not really care how you use it, as long as it follows the license. I would like to be credited, though, as a lot of work went into this.
License can be found at `LICENSE.md` in the root directory.
