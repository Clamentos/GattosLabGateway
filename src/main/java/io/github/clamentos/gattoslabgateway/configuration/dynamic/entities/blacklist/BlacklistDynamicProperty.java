package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.blacklist;

///
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

///..
import lombok.EqualsAndHashCode;
import lombok.Getter;

///
@EqualsAndHashCode(callSuper = true)
@Getter

///
public final class BlacklistDynamicProperty extends DynamicProperty {

    ///
    private static final String PREFIX = "BlacklistDynamicProperty.<init> :: ";
    private static final String RANGE = "IP ranges must have 2 components";

    ///.
    private final Set<BlacklistIpEntry> ipv4s;
    private final Set<BlacklistIpEntry> ipv6s;
    private final Set<String> userAgentContains;

    ///
    public BlacklistDynamicProperty(final List<String> components, final boolean isEnabled) throws IllegalArgumentException {

        // type     | en | ipv4s                          | ipv6s           | userAgentContains
        // BLACKLIST|true|1.2.3.4-1.2.3.9, 2.3.4.5-2.3.4.8|::1-::6, ::9-::35|null, "", "bot", "robot", "crawler",

        if(components.size() != 5) throw new IllegalArgumentException(PREFIX + "Not enough components");
        super(DynamicPropertyType.BLACKLIST, isEnabled);

        ipv4s = new HashSet<>();

        for(final String ipv4Range : GenericUtils.fastSplit(components.get(2), Constants.DYNAMIC_PROPERTY_ARRAY_SEPARATOR)) {

            final List<String> range = GenericUtils.fastSplit(ipv4Range, Constants.DYNAMIC_PROPERTY_RANGE_SEPARATOR);

            if(range.size() != 2) throw new IllegalArgumentException(PREFIX + RANGE);
            ipv4s.add(new BlacklistIpEntry(range.get(0), range.get(1)));
        }

        ipv6s = new HashSet<>();

        for(final String ipv6Range : GenericUtils.fastSplit(components.get(3), Constants.DYNAMIC_PROPERTY_ARRAY_SEPARATOR)) {

            final List<String> range = GenericUtils.fastSplit(ipv6Range, Constants.DYNAMIC_PROPERTY_RANGE_SEPARATOR);

            if(range.size() != 2) throw new IllegalArgumentException(PREFIX + RANGE);
            ipv6s.add(new BlacklistIpEntry(range.get(0), range.get(1)));
        }

        userAgentContains = GenericUtils

            .fastSplit(components.get(4), Constants.DYNAMIC_PROPERTY_ARRAY_SEPARATOR)
            .stream()
            .map(this::parseUserAgentContains)
            .collect(Collectors.toSet())
        ;
    }

    ///
    private String parseUserAgentContains(final String value) throws IllegalArgumentException {

        if(value == null) return null;
        final String trimmed = value.trim();

        if(trimmed.equals("") || trimmed.isBlank()) return "";
        if(trimmed.equals("null")) return null;

        if(trimmed.charAt(0) != '"' || trimmed.charAt(trimmed.length() - 1) != '"') {

            throw new IllegalArgumentException(PREFIX + "Non special elements of field \"userAgentContains\", must be quoted");
        }

        return GenericUtils.fastRemove(trimmed.substring(0, trimmed.length() - 1).substring(1), '\\');
    }

    ///
}
