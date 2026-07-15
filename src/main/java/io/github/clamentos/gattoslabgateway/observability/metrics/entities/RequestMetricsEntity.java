package io.github.clamentos.gattoslabgateway.observability.metrics.entities;

///
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

///
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

///
public final class RequestMetricsEntity {

    ///
    private long timestamp;
    private int latency;
    private String path;
    private String userAgent;
    private boolean isUnknown;
    private int httpStatus;

    ///
}
