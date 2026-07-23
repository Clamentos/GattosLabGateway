package io.github.clamentos.gattoslabgateway.observability;

///
import io.github.clamentos.gattoslabgateway.utils.GenericUtils;

///..
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

///
public final class ObservabilityFile<T> {

    ///
    private final Path filePath;
    private final Lock lock;

    ///
    public ObservabilityFile(final String filePath) {

        this.filePath = Path.of(filePath);
        lock = new ReentrantLock();
    }

    ///
    public BufferedReader createReaderAndLock() throws IOException {

        final BufferedReader reader = Files.newBufferedReader(filePath);
        lock.lock();

        return reader;
    }

    ///..
    public void unlock() {

        lock.unlock();
    }

    ///..
    public void write(final List<T> entities) throws IOException {

        int failureCounter = 0;
        IOException lastFailure = null;

        lock.lock();

        try(final BufferedWriter writer = GenericUtils.openFileAsAppend(filePath)) {

            for(final T entity : entities) {

                try {

                    writer.write(entity.toString());
                    writer.write("\n");
                }

                catch(final IOException exc) {

                    failureCounter++;
                    lastFailure = exc;
                }
            }
        }

        lock.unlock();
        if(failureCounter > 0) throw new IOException(failureCounter + " errors because", lastFailure);
    }

    ///..
    public void clear() throws IOException {

        Files.write(filePath, new byte[0]);
    }

    ///
}
