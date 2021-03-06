package pl.alpheratzteam.obfuscator.api.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Unix on 01.09.2019.
 */
public final class RandomUtil {

    private static Random random = ThreadLocalRandom.current();

    private RandomUtil() {
    }

    public static Random getRandom() {
        return random;
    }

    public static int nextInt() {
        return random.nextInt();
    }

    public static int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public static boolean nextBoolean() {
        return random.nextInt() == 0;
    }
}