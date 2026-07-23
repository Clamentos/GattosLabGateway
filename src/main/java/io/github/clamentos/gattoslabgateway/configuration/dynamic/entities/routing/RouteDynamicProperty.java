package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.routing;

///
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;

///..
import java.util.List;

///..
import lombok.EqualsAndHashCode;
import lombok.Getter;

///
@EqualsAndHashCode(callSuper = true)
@Getter

///
public final class RouteDynamicProperty extends DynamicProperty {

    ///
    private static final String PREFIX = "RouteDynamicProperty.<init> :: ";

    ///.
    private final String source;
    private final String destination;

    ///
    public RouteDynamicProperty(final List<String> components, final boolean isEnabled) throws IllegalArgumentException {

        if(components.size() != 4) throw new IllegalArgumentException(PREFIX + "Not enough components");
        if(components.get(2) == null) throw new IllegalArgumentException(PREFIX + "Field 'source' cannot be null");
        if(components.get(3) == null) throw new IllegalArgumentException(PREFIX + "Field 'destination' cannot be null");

        final String trimmedSource = components.get(2).trim();
        final String trimmedDestination = components.get(3).trim();

        if(trimmedSource.isBlank()) throw new IllegalArgumentException(PREFIX + "Field 'source' cannot be blank");
        if(trimmedDestination.isBlank()) throw new IllegalArgumentException(PREFIX + "Field 'destination' cannot be blank");

        super(DynamicPropertyType.ROUTE, isEnabled);

        source = trimmedSource;
        destination = trimmedDestination;
    }

    ///
}
