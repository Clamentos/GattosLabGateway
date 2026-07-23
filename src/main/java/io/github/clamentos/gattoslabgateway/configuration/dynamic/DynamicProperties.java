package io.github.clamentos.gattoslabgateway.configuration.dynamic;

///
import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.configuration.Constants;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicProperty;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;
import io.github.clamentos.gattoslabgateway.scheduling.BatchScheduler;

///..
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

///
public final class DynamicProperties {

    ///
    private static final String DYNAMIC_PROPERTY_COMMENT = Character.toString(Constants.DYNAMIC_PROPERTY_COMMENT);

    ///.
    private final Path dynamicPropertiesPath;

    ///..
    private final Logger logger;
    private final DynamicPropertyMapper mapper;

    ///..
    private final Map<DynamicPropertyType, DynamicProperty> dynamicPropertyMap;

    ///
    public DynamicProperties(final ApplicationProperties applicationProperties, final BatchScheduler batchScheduler) throws IllegalArgumentException {

        dynamicPropertiesPath = Path.of(applicationProperties.getDynamicPropertiesPath());

        logger = new Logger();
        mapper = new DynamicPropertyMapper();

        dynamicPropertyMap = new ConcurrentHashMap<>();

        batchScheduler.schedule(this::refresh, "DynamicProperties.refresh", applicationProperties.getDynamicPropertiesRefreshSchedule());
    }

    ///
    public DynamicProperty get(final DynamicPropertyType type) throws ClassCastException {

        return dynamicPropertyMap.get(type);
    }

    ///.
    private void refresh() {

        try(final BufferedReader reader = Files.newBufferedReader(dynamicPropertiesPath)) {

            final List<DynamicProperty> properties = new ArrayList<>();
            String line;

            while((line = reader.readLine()) != null) {

                if(!line.isBlank() && !line.startsWith(DYNAMIC_PROPERTY_COMMENT)) properties.add(mapper.deserialize(line));
            }

            final int originalHashCode = dynamicPropertyMap.hashCode();

            dynamicPropertyMap.clear();
            for(final DynamicProperty property : properties) dynamicPropertyMap.put(property.getType(), property);
            if(dynamicPropertyMap.hashCode() != originalHashCode) logger.info("Dynamic properties have been updated");
        }

        catch(final IOException | RuntimeException exc) {

            logger.error("Could not refresh dynamic properties because", exc);
        }
    }

    ///
}
