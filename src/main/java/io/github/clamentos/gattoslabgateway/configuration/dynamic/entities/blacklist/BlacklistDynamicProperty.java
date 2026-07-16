package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.blacklist;

///
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyEntity;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;

///
import java.util.List;
import java.util.Set;

///..
import lombok.EqualsAndHashCode;
import lombok.Getter;

///
@EqualsAndHashCode(callSuper = true)
@Getter

///
public final class BlacklistDynamicProperty extends DynamicPropertyEntity {

    ///
    private final List<BlacklistIpEntry> ipv4s;
    private final List<BlacklistIpEntry> ipv6s;
    private final Set<String> userAgentContains;

    ///
    public BlacklistDynamicProperty(

        final DynamicPropertyType type,
        final boolean enabled,
        final List<BlacklistIpEntry> ipv4s,
        final List<BlacklistIpEntry> ipv6s,
        final Set<String> userAgentContains

    ) throws IllegalArgumentException {

        super(type, enabled);

        this.ipv4s = ipv4s != null ? ipv4s : List.of();
        this.ipv6s = ipv6s != null ? ipv6s : List.of();

        if(userAgentContains != null) {

            for(final String pattern : userAgentContains) {

                if(pattern == null || pattern.isBlank()) {

                    throw new IllegalArgumentException("BlacklistDynamicProperty.<init>:: Field 'userAgentContains' cannot contain null or blank elements");
                }
            }

            this.userAgentContains = userAgentContains;
        }

        else this.userAgentContains = Set.of();
    }

    ///
    // {"type": "BLACKLIST", "enabled": true, "ipv4s": [], "ipv6s": [], "userAgentContains": []}

    ///
}
