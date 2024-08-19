package be.yvanmazy.pgpsplitter.util;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPKeyRing;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public final class PGPFileUtil {

    private PGPFileUtil() throws IllegalAccessException {
        throw new IllegalAccessException("You cannot instantiate a utility class");
    }

    public static void write(final @NotNull PGPKeyRing keyRing, final @NotNull Path path) throws IOException {
        write(keyRing, Files.newOutputStream(path));
    }

    public static void write(final @NotNull PGPKeyRing keyRing, final @NotNull OutputStream out) throws IOException {
        write(out, keyRing::encode);
    }

    public static void write(final @NotNull PGPPublicKey key, final @NotNull Path path) throws IOException {
        write(key, Files.newOutputStream(path));
    }

    public static void write(final @NotNull PGPPublicKey key, final @NotNull OutputStream out) throws IOException {
        write(out, key::encode);
    }

    public static void write(final @NotNull PGPSecretKey key, final @NotNull Path path) throws IOException {
        write(key, Files.newOutputStream(path));
    }

    public static void write(final @NotNull PGPSecretKey key, final @NotNull OutputStream out) throws IOException {
        write(out, key::encode);
    }

    private static void write(final @NotNull OutputStream out, final @NotNull OutConsumer consumer) throws IOException {
        try (final OutputStream stream = ArmoredOutputStream.builder().clearHeaders().build(out)) {
            consumer.accept(stream);
        }
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull String getFormattedName(final @NotNull PGPPublicKey key) {
        return getFormattedNamePrefix(key.getUserIDs(), key.getKeyID()) + "_public";
    }

    @Contract(pure = true, value = "_ -> new")
    public static @NotNull String getFormattedName(final @NotNull PGPSecretKey key) {
        return getFormattedNamePrefix(key.getUserIDs(), key.getKeyID()) + "_SECRET";
    }

    private static String getFormattedNamePrefix(final Iterator<String> ids, final long keyId) {
        final String userId;
        if (ids.hasNext()) {
            userId = ids.next().replaceAll(" <.*>", "").replaceAll("\\W", "");
        } else {
            userId = "unknown";
        }

        final String keyIdHex = String.format("%016X", keyId).substring(8);
        return userId + "_0x" + keyIdHex;
    }

}