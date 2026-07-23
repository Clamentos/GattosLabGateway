package io.github.clamentos.gattoslabgateway.configuration.dynamic.entities;

///
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

///
@AllArgsConstructor
@EqualsAndHashCode
@Getter

///
public abstract class DynamicProperty {

    ///
    private final DynamicPropertyType type;
    private final boolean isEnabled;

    ///
}
