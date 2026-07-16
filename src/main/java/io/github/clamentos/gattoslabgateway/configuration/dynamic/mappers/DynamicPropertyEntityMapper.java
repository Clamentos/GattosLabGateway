package io.github.clamentos.gattoslabgateway.configuration.dynamic.mappers;

///
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyEntity;
import io.github.clamentos.gattoslabgateway.configuration.dynamic.entities.DynamicPropertyType;
import io.github.clamentos.gattoslabgateway.exceptions.DatabindException;
import io.github.clamentos.gattoslabgateway.mappers.JsonReader;
import io.github.clamentos.gattoslabgateway.mappers.Mapper;

///
public final class DynamicPropertyEntityMapper implements Mapper<DynamicPropertyEntity> {

    ///
    @Override
    public DynamicPropertyEntity deserialize(final String data) throws DatabindException {

        // {"type": "BLACKLIST", "enabled": true, "ipv4s": [], "ipv6s": [], "userAgentContains": []}
        final JsonReader jsonReader = new JsonReader(data);
        final DynamicPropertyType dynamicPropertyType = DynamicPropertyType.valueOf(jsonReader.findStringValue("type"));
        boolean enabled;

        jsonReader.readStartObject();

        while(jsonReader.hasRemaining()) {

            switch(jsonReader.readKey()) {

                case "enabled": enabled = jsonReader.readBoolean(); break;
                case "ipv4s": break;
                case "ipv6s": break;
                case "userAgentContains": break;

                default: throw new DatabindException("...");
            }
        }

        jsonReader.readEndObject();
        return null;
    }

    ///..
    @Override
    public String serialize(final DynamicPropertyEntity data) throws DatabindException {

        throw new UnsupportedOperationException("Dynamic properties are read-only");
    }

    ///
}
