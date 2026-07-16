package io.github.clamentos.gattoslabgateway.lifecycle;

///
import java.io.Closeable;
import java.util.List;

///..
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

///
@AllArgsConstructor
@Slf4j

///
public class ShutdownHook implements Runnable {

    ///
    private final List<Closeable> closables;

    ///
    @Override
    public void run() {

        log.info("Begin shutdown...");
        for(final Closeable closable : closables) this.tryClose(closable);
        log.info("End shutdown");
    }

    ///.
    private void tryClose(final Closeable closeable) {

        try {

            closeable.close();
            log.info("Closed {}", closeable.getClass().getSimpleName());
        }

        catch(final Exception exc) {

            log.error("Could not close {} because", closeable.getClass().getSimpleName(), exc);
        }
    }

    ///
}
