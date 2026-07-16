package io.github.clamentos.gattoslabgateway.persistence;

///
import io.github.clamentos.gattoslabgateway.exceptions.DatabindException;
import io.github.clamentos.gattoslabgateway.mappers.Mapper;

///..
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

///..
import lombok.extern.slf4j.Slf4j;

///
@Slf4j

///
public final class FileDatabase {

    ///
    public <T> List<T> fetchByEntityType(final EntityType entityType, final Mapper<T> mapper) throws DatabindException, IOException {

        final List<Path> filesToSearch = new ArrayList<>();

        try(final Stream<Path> files = Files.list(entityType.getPath())) {

            filesToSearch.addAll(files.toList());
        }

        catch(final FileNotFoundException exc) {

            log.warn("Could not find file", exc);
            return List.of();
        }

        final List<T> entities = new ArrayList<>();

        for(final Path file : filesToSearch) {

            try(final BufferedReader reader = Files.newBufferedReader(file)) {

                String line;
                while((line = reader.readLine()) != null) entities.add(mapper.deserialize(line));
            }

            catch(final FileNotFoundException exc) {

                log.warn("Could not find file", exc);
                return List.of();
            }
        }

        return entities;
    }

    ///
}
