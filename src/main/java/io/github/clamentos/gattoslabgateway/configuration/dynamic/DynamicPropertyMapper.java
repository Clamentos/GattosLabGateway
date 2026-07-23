package io.github.clamentos.gattoslabgateway.configuration.dynamic;

///
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.blacklist.BlacklistDynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.maintenance.MaintenancePageDynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.routing.RouteDynamicProperty;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.util.List;

///
public final class DynamicPropertyMapper {

    ///
    private static final String PREFIX = "DynamicPropertyMapper.deserialize :: ";

    ///
    public DynamicProperty deserialize(final String data) throws IllegalArgumentException {

        final List<String> splits = GenericUtils.fastSplit(data, Constants.FIELD_SEPARATOR);

        if(splits.size() < 2) throw new IllegalArgumentException(PREFIX + "Properties must have at least 2 components");
        if(splits.get(0) == null) throw new IllegalArgumentException(PREFIX + "Field 'type' cannot be null");
        if(splits.get(1) == null) throw new IllegalArgumentException(PREFIX + "Field 'enabled' cannot be null");

        final String trimmedType = splits.get(0).trim();
        final String trimmedEnabled = splits.get(1).trim();

        if(trimmedType.isBlank()) throw new IllegalArgumentException(PREFIX + "Field 'type' cannot be blank");
        if(trimmedEnabled.isBlank()) throw new IllegalArgumentException(PREFIX + "Field 'enabled' cannot be blank");

        try {

            final DynamicPropertyType type = DynamicPropertyType.valueOf(splits.get(0));
            final boolean isEnabled = Boolean.parseBoolean(splits.get(1));

            switch(type) {

                case BLACKLIST: return new BlacklistDynamicProperty(splits, isEnabled);
                case MAINTENANCE_PAGE: return new MaintenancePageDynamicProperty(splits, isEnabled);
                case ROUTE: return new RouteDynamicProperty(splits, isEnabled);

                default: throw new IllegalArgumentException(PREFIX + "Unknown property type '" + type + "'");
            }
        }

        catch(final RuntimeException exc) {

            throw new IllegalArgumentException(PREFIX + "Could not deserialize because", exc);
        }
    }

    ///
}
