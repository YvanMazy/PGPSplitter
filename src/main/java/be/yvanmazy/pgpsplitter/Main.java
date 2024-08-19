package be.yvanmazy.pgpsplitter;

import be.yvanmazy.pgpsplitter.util.PGPFileUtil;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyRing;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class Main {

    public static void main(final String[] args) throws IOException, PGPException {
        if (args.length < 2) {
            System.out.println("PGPSplitter help:");
            System.out.println();
            System.out.println("--file -f <file path> : Split keys from file");
            System.out.println("--url -u <url> : Split keys from url");
            System.out.println("You can add --private after all parameters if the input concerns private keys.");
            System.out.println();
            System.out.println("Examples:");
            System.out.println("java -jar PGPSplitter.jar -f keys.asc");
            System.out.println("java -jar PGPSplitter.jar -u https://github.com/YvanMazy.gpg");
            System.out.println("java -jar PGPSplitter.jar -f my-keys.asc --private");
            return;
        }

        final String type = args[0];
        final String input = args[1];
        final boolean isPrivate = args.length >= 3 && args[2].equalsIgnoreCase("--private");

        try (final PGPSplitter splitter = new PGPSplitter(fetchInputStream(type, input))) {
            if (isPrivate) {
                write(splitter.toSecretKeyCollection(), ring -> {
                    final String name = PGPFileUtil.getFormattedName(ring.getSecretKey());
                    System.out.println("Saving the secret key '" + name + "'...");
                    return name;
                });
            } else {
                write(splitter.toPublicKeyCollection(), ring -> {
                    final String name = PGPFileUtil.getFormattedName(ring.getPublicKey());
                    System.out.println("Saving the public key '" + name + "'...");
                    return name;
                });
            }
        }
    }

    private static <T extends PGPKeyRing> void write(final Iterable<T> iterable, final Function<T, String> function) throws IOException {
        for (final T value : iterable) {
            PGPFileUtil.write(value, Path.of(function.apply(value) + ".asc"));
        }
    }

    private static InputStream fetchInputStream(final String type, final String input) throws IOException {
        if (type.equalsIgnoreCase("--file") || type.equalsIgnoreCase("-f")) {
            return Files.newInputStream(Path.of(input));
        } else if (type.equalsIgnoreCase("--url") || type.equalsIgnoreCase("-u")) {
            return URI.create(input).toURL().openStream();
        }
        throw new IllegalArgumentException("Invalid input type: " + type);
    }

}