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
    private static final String SOURCE = "BlacklistIpEntry.<init>";

    ///
    private final byte[] start;
    private final byte[] end;

    ///
    public BlacklistIpEntry(final String start, final String end) throws IllegalArgumentException {

        if(start == null || start.isBlank()) throw new IllegalArgumentException(SOURCE + ":: Field 'start' cannot be null nor blank");
        if(end == null || end.isBlank()) throw new IllegalArgumentException(SOURCE + ":: Field 'end' cannot be null nor blank");

        try {

            this.start = InetAddress.ofLiteral(start).getAddress();
            this.end = InetAddress.ofLiteral(end).getAddress();
        }

        catch(final IllegalArgumentException exc) {

            throw new IllegalArgumentException(SOURCE + ":: Could not instantiate because", exc);
        }
    }

    ///
    // {"start": "<ipv4 / ipv6>", "end": "<ipv4 / ipv6>"}

    ///
}
