package be.yvanmazy.pgpsplitter.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;

@FunctionalInterface
interface OutConsumer {

    void accept(final @NotNull OutputStream out) throws IOException;

}