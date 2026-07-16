package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.routing;

///
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyEntity;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;

///..
import lombok.EqualsAndHashCode;
import lombok.Getter;

///
@EqualsAndHashCode(callSuper = true)
@Getter

///
public final class RoutingDynamicProperty extends DynamicPropertyEntity {

    ///
    private final String source;
    private final String destination;

    ///
    public RoutingDynamicProperty(final DynamicPropertyType type, final boolean enabled, final String source, final String destination) {

        super(type, enabled);

        this.source = source;
        this.destination = destination;
    }

    ///
    // {"type": "ROUTE", "enabled":true, "source": "...", "destination", "..."}

    ///
}
