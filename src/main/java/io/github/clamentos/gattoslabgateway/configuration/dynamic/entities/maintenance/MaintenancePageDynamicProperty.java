package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.maintenance;

///
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;

///..
import java.util.List;

///
public final class MaintenancePageDynamicProperty extends DynamicProperty {

    ///
    public MaintenancePageDynamicProperty(final List<String> components, final boolean isEnabled) {

        if(components.size() != 2) throw new IllegalArgumentException("MaintenancePageDynamicProperty.<init> :: Not enough components");
        super(DynamicPropertyType.MAINTENANCE_PAGE, isEnabled);
    }

    ///
}
