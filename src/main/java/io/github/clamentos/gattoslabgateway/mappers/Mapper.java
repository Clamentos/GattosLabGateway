package io.github.clamentos.gattoslabgateway.mappers;

///
import io.github.clamentos.gattoslabgateway.exceptions.DatabindException;

///
public interface Mapper<T> {

    ///
    T deserialize(final String data) throws DatabindException;
    String serialize(final T data) throws DatabindException;

    ///
}
