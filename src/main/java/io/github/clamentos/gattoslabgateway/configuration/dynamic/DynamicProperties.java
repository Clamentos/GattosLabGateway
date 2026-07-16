package io.github.clamentos.gattoslabgateway.configuration.dynamic;

///
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyEntity;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.mappers.DynamicPropertyEntityMapper;
import io.github.clamentos.gattoslabgateway.exceptions.DatabindException;
import io.github.clamentos.gattoslabgateway.persistence.EntityType;
import io.github.clamentos.gattoslabgateway.persistence.FileDatabase;
import io.github.clamentos.gattoslabgateway.scheduling.BatchScheduler;

///..
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

///..
import lombok.extern.slf4j.Slf4j;

///
@Slf4j

///
public final class DynamicProperties {

    ///
    private final FileDatabase fileDatabase;

    ///..
    private final Map<DynamicPropertyType, DynamicPropertyEntity> dynamicPropertyMap;

    ///..
    private final DynamicPropertyEntityMapper mapper;

    ///
    public DynamicProperties(final ApplicationProperties applicationProperties, final BatchScheduler batchScheduler, final FileDatabase fileDatabase)
    throws IllegalArgumentException {

        this.fileDatabase = fileDatabase;
        dynamicPropertyMap = new ConcurrentHashMap<>();
        mapper = new DynamicPropertyEntityMapper();

        batchScheduler.schedule(this::refresh, "DynamicProperties::refresh", applicationProperties.getDynamicPropertiesRefreshSchedule());
    }

    ///
    public DynamicPropertyEntity get(final DynamicPropertyType type) throws ClassCastException {

        return dynamicPropertyMap.get(type);
    }

    ///.
    private void refresh() {

        try {

            final List<DynamicPropertyEntity> properties = fileDatabase.fetchByEntityType(EntityType.DYNAMIC_PROPERTIES, mapper);
            final int originalHashCode = dynamicPropertyMap.hashCode();

            dynamicPropertyMap.clear();
            for(final DynamicPropertyEntity property : properties) dynamicPropertyMap.put(property.getType(), property);

            if(dynamicPropertyMap.hashCode() != originalHashCode) log.info("Dynamic properties have changed");
        }

        catch(final DatabindException | IOException | RuntimeException exc) {

            log.error("Could not refresh the dynamic properties because", exc);
        }
    }

    ///
}
