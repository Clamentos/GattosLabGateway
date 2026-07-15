package io.github.clamentos.gattoslabgateway.utils;

///
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

///..
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

///
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j

///
public final class GenericUtils {

    ///
    public static String composeFingerprint(final InetAddress ip, final String userAgent) {

        return ip.getHostAddress() + " >> " + userAgent;
    }

    ///..
    public static boolean silentSleep(final long amount) {

        try {

            Thread.sleep(amount);
            return false;
        }

        catch(final InterruptedException _) {

            Thread.currentThread().interrupt();
            Thread.interrupted();

            return true;
        }
    }

    ///..
    public static Thread spawnVirtualThread(final String name, final Runnable task) {

        return Thread.ofVirtual().name(name).start(task);
    }

    ///..
    public static Thread createVirtualThread(final String name, final Runnable task) {

        return Thread.ofVirtual().name(name).unstarted(task);
    }

    ///..
    public static List<String> fastSplit(final String input, final char delimiter) {

        final List<String> splits = new ArrayList<>();
        final StringBuilder stringBuilder = new StringBuilder();
        final int length = input.length();

        char currentChar = 0;

        for(int i = 0; i < length; i++) {

            currentChar = input.charAt(i);

            if(currentChar != delimiter) {

                stringBuilder.append(currentChar);
            }

            else {

                splits.add(stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        }

        splits.add(stringBuilder.toString());
        return splits;
    }

    ///..
    public static String fastReplace(final String input, final char toReplace, final char replacement) {

        final int length = input.length();
        final StringBuilder stringBuilder = new StringBuilder(length);

        char currentChar = 0;

        for(int i = 0; i < length; i++) {

            currentChar = input.charAt(i);
            stringBuilder.append(currentChar != toReplace ? currentChar : replacement);
        }

        return stringBuilder.toString();
    }

    ///
}
