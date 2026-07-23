package io.github.clamentos.gattoslabgateway.lifecycle;

///
import io.github.clamentos.gattoslabgateway.observability.logging.Logger;

///..
import java.io.Closeable;
import java.util.Collection;

///
public class ShutdownHook implements Runnable {

    ///
    private final Logger logger;

    ///..
    private final Collection<Closeable> closables;

    ///
    public ShutdownHook(final Collection<Closeable> closables) {

        logger = new Logger();
        this.closables = closables;
    }

    ///
    @Override
    public void run() {

        logger.info("Begin shutdown...");
        for(final Closeable closable : closables) this.tryClose(closable);
        logger.info("End shutdown");
    }

    ///.
    private void tryClose(final Closeable closeable) {

        final String closeableClassName = closeable.getClass().getSimpleName();

        try {

            closeable.close();
            logger.info("Closed '" + closeableClassName + "'");
        }

        catch(final Exception exc) {

            logger.error("Could not close '" + closeableClassName + "' because", exc);
        }
    }

    ///
}
