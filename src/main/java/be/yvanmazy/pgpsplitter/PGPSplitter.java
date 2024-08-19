package be.yvanmazy.pgpsplitter;

import org.bouncycastle.openpgp.*;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PGPSplitter implements AutoCloseable {

    private final InputStream inputStream;

    public PGPSplitter(final @NotNull InputStream inputStream) throws IOException {
        this.inputStream = PGPUtil.getDecoderStream(Objects.requireNonNull(inputStream, "inputStream must not be null"));
    }

    @Contract("-> new")
    public @NotNull Stream<PGPPublicKeyRing> streamPublicKeys() throws PGPException, IOException {
        return this.streamPublicKeys(this.buildDefaultCalculator());
    }

    @Contract("_ -> new")
    public @NotNull Stream<PGPPublicKeyRing> streamPublicKeys(final @NotNull KeyFingerPrintCalculator calculator) throws PGPException, IOException {
        return StreamSupport.stream(this.toPublicKeyCollection(calculator).spliterator(), false);
    }

    @Contract("-> new")
    public @NotNull PGPPublicKeyRingCollection toPublicKeyCollection() throws PGPException, IOException {
        return this.toPublicKeyCollection(this.buildDefaultCalculator());
    }

    @Contract("_ -> new")
    public @NotNull PGPPublicKeyRingCollection toPublicKeyCollection(final @NotNull KeyFingerPrintCalculator calculator) throws PGPException, IOException {
        Objects.requireNonNull(calculator, "Key fingerprint calculator must not be null");
        return new PGPPublicKeyRingCollection(this.inputStream, calculator);
    }

    @Contract("-> new")
    public @NotNull Stream<PGPSecretKeyRing> streamSecretKeys() throws PGPException, IOException {
        return this.streamSecretKeys(this.buildDefaultCalculator());
    }

    @Contract("_ -> new")
    public @NotNull Stream<PGPSecretKeyRing> streamSecretKeys(final @NotNull KeyFingerPrintCalculator calculator) throws PGPException, IOException {
        return StreamSupport.stream(this.toSecretKeyCollection(calculator).spliterator(), false);
    }

    @Contract("-> new")
    public @NotNull PGPSecretKeyRingCollection toSecretKeyCollection() throws PGPException, IOException {
        return this.toSecretKeyCollection(this.buildDefaultCalculator());
    }

    @Contract("_ -> new")
    public @NotNull PGPSecretKeyRingCollection toSecretKeyCollection(final @NotNull KeyFingerPrintCalculator calculator) throws PGPException, IOException {
        Objects.requireNonNull(calculator, "Key fingerprint calculator must not be null");
        return new PGPSecretKeyRingCollection(this.inputStream, calculator);
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

    private KeyFingerPrintCalculator buildDefaultCalculator() {
        return new JcaKeyFingerprintCalculator();
    }

}