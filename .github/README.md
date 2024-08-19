# 🔐 PGPSplitter

Tiny tool to read every PGP key in a single block.

This tool should not be particularly used in complex applications because BouncyCastle offers a broad enough API.
It was designed to facilitate this specific operation while keeping a clean structure.

This is a tool for personal use mainly, but you have every right to use it and contribute to it.

## 🖥️ Use as a CLI
Download the jar from [Releases](https://github.com/YvanMazy/PGPSplitter/releases).

```
--file -f <file path> : Split keys from file
--url -u <url> : Split keys from url
You can add --private after all parameters if the input concerns private keys.

Examples:
java -jar PGPSplitter.jar -f keys.asc
java -jar PGPSplitter.jar -u https://github.com/YvanMazy.gpg
java -jar PGPSplitter.jar -f my-keys.asc --private
```

## 📚 Use as a library

<br>**Last version**: [![Release](https://jitpack.io/v/YvanMazy/PGPSplitter.svg)](https://jitpack.io/#YvanMazy/PGPSplitter)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.YvanMazy:PGPSplitter:VERSION'
}
```

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.YvanMazy</groupId>
        <artifactId>PGPSplitter</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

### Examples

Display user ids from url:
```java
try (final PGPSplitter splitter = new PGPSplitter(URI.create("https://github.com/YvanMazy.gpg").toURL().openStream())) {
    splitter.streamPublicKeys()
            .map(PGPPublicKeyRing::getPublicKey)
            .map(PGPPublicKey::getUserIDs)
            .filter(Iterator::hasNext)
            .map(Iterator::next)
            .forEach(System.out::println);
} catch (final IOException | PGPException exception) {
    LOGGER.error("Failed to split keys", exception);
}
```
Write every key in a separate file:
```java
try (final PGPSplitter splitter = new PGPSplitter(Files.newInputStream(Path.of("all-keys.asc")))) {
    for (final PGPPublicKeyRing ring : splitter.toPublicKeyCollection()) {
        PGPFileUtil.write(ring, Path.of(PGPFileUtil.getFormattedName(ring.getPublicKey()) + ".asc"));
    }
} catch (final IOException | PGPException exception) {
    LOGGER.error("Failed to split keys", exception);
}
```