package io.github.clamentos.gattoslabgateway;

import io.github.clamentos.gattoslabgateway.configuration.ApplicationProperties;
import io.github.clamentos.gattoslabgateway.configuration.ProfileResolver;
import io.github.clamentos.gattoslabgateway.exchange.RequestExchanger;
import io.github.clamentos.gattoslabgateway.lifecycle.ShutdownHook;
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

import java.util.List;

public class Application {

    public static void main(final String[] args) throws Exception {

        final ApplicationProperties applicationProperties = new ProfileResolver(args.length > 0 ? args[0] : null).getApplicationProperties();
        final RequestExchanger requestExchanger = new RequestExchanger(applicationProperties);
        final ShutdownHook shutdownHook = new ShutdownHook(List.of(requestExchanger));
        Runtime.getRuntime().addShutdownHook(GenericUtils.createVirtualThread("gattos-lab-gateway-shutdown-hook", shutdownHook));
    }
}
