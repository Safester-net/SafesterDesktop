package net.safester.application;

import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class CryptoBootstrap {

    private CryptoBootstrap() {
    }

    public static void ensureBcInstalled() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}
