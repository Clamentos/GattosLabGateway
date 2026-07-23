package io.github.clamentos.gattoslabgateway.configuration.environments;

///
public enum Environment {

    ///
    DEV,
    PROD;

    ///
    public static Environment getDefault() {

        return Environment.DEV;
    }

    ///
}
