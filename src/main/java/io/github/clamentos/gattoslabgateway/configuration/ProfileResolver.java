package io.github.clamentos.gattoslabgateway.configuration;

///
import io.github.clamentos.gattoslabgateway.configuration.environments.DevProperties;
import io.github.clamentos.gattoslabgateway.configuration.environments.Environment;
import io.github.clamentos.gattoslabgateway.configuration.environments.ProdProperties;
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;

///..
import lombok.Getter;

///
@Getter

///
public final class ProfileResolver {

    ///
    private final ApplicationProperties applicationProperties;

    ///
    public ProfileResolver(final String profile) throws IllegalArgumentException {

        final Logger logger = new Logger();

        if(profile != null && !profile.isEmpty()) {

            final Environment environment = Environment.valueOf(profile);

            logger.info("Using profile '" + environment + "'");
            applicationProperties = environment == Environment.PROD ? new ProdProperties() : new DevProperties();
        }

        else {

            logger.info("No profile provided, defaulting to '" + Environment.getDefault() + "'");
            applicationProperties = new DevProperties();
        }
    }

    ///
}
