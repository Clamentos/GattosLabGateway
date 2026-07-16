package io.github.clamentos.gattoslabgateway.mappers;

///
import io.github.clamentos.gattoslabgateway.exceptions.DatabindException;

///
public interface Mapper<T> {

    ///
    public T deserialize(final String data) throws DatabindException, UnsupportedOperationException;
    public String serialize(final T data) throws DatabindException, UnsupportedOperationException;

    ///
}
