package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.blacklist;

///
import java.net.InetAddress;

///..
import lombok.EqualsAndHashCode;
import lombok.Getter;

///
@EqualsAndHashCode
@Getter

///
public final class BlacklistIpEntry {

    ///
    private static final String PREFIX = "BlacklistIpEntry.<init> :: ";

    ///
    private final byte[] start;
    private final byte[] end;

    ///
    public BlacklistIpEntry(final String start, final String end) throws IllegalArgumentException {

        if(start == null) throw new IllegalArgumentException(PREFIX + "Field 'start' cannot be null");
        if(end == null) throw new IllegalArgumentException(PREFIX + "Field 'end' cannot be null");

        final String trimmedStart = start.trim();
        final String trimmedEnd = end.trim();

        if(trimmedStart.isBlank()) throw new IllegalArgumentException(PREFIX + "Field 'start' cannot be blank");
        if(trimmedEnd.isBlank()) throw new IllegalArgumentException(PREFIX + "Field 'end' cannot be blank");

        try {

            this.start = InetAddress.ofLiteral(trimmedStart).getAddress();
            this.end = InetAddress.ofLiteral(trimmedEnd).getAddress();
        }

        catch(final IllegalArgumentException exc) {

            throw new IllegalArgumentException(PREFIX + "Could not instantiate because", exc);
        }
    }

    ///
}
