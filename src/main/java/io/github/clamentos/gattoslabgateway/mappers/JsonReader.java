package io.github.clamentos.gattoslabgateway.mappers;

///
import io.github.clamentos.gattoslabgateway.exceptions.DatabindException;

///
public final class JsonReader {

    ///
    private final String data;
    private final int length;

    ///..
    private int index;

    ///
    public JsonReader(final String data) {

        this.data = data;
        length = data.length();
    }

    ///
    public boolean hasRemaining() {

        return index < length;
    }

    ///
    public void readStartObject() throws DatabindException {

        this.consumeTargetCharacter('{');
    }

    ///..
    public void readEndObject() throws DatabindException {

        this.consumeTargetCharacter('}');
    }

    ///..
    public void readStartArray() throws DatabindException {

        this.consumeTargetCharacter('[');
    }

    ///..
    public void readEndArray() throws DatabindException {

        this.consumeTargetCharacter(']');
    }

    ///..
    public String readKey() throws DatabindException {

        final StringBuilder builder = new StringBuilder();
        char currentChar;

        this.consumeTargetCharacter('"');

        while(index < length) {

            currentChar = data.charAt(index);

            if(currentChar != '"') {

                builder.append(currentChar);
            }

            else {

                index++;
                return builder.toString();
            }
        }

        throw new DatabindException("...");
    }

    ///..
    public String readString() throws DatabindException {

        this.consumeTargetCharacter(':');
        final String value = this.readKey();
        this.consumeTargetCharacter(',');

        return value;
    }

    ///..
    public boolean readBoolean() throws DatabindException {

        final StringBuilder builder = new StringBuilder();
        char currentChar;

        this.consumeTargetCharacter(':');

        while(index < length) {

            currentChar = data.charAt(index);

            if(currentChar == ',') {

                index++;
                return Boolean.parseBoolean(builder.toString());
            }

            else if(!Character.isWhitespace(currentChar)) {

                builder.append(currentChar);
            }
        }

        throw new DatabindException("...");
    }

    ///..
    public String findStringValue(final String key) throws DatabindException {

        final int indexOfKey = data.indexOf("\"" + key + "\"");
        if(indexOfKey == -1) throw new DatabindException("...");

        index = indexOfKey;
        if(!key.equals(this.readKey())) throw new DatabindException("...");

        return this.readString();
    }

    ///.
    private void consumeTargetCharacter(final char targetCharacter) throws DatabindException {

        while(index < length) {

            if(data.charAt(index++) == targetCharacter) return;
        }

        throw new DatabindException("...");
    }

    ///
}
