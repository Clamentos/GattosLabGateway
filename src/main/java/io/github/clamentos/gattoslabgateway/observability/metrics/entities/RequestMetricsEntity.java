package io.github.clamentos.gattoslabgateway.observability.metrics.entities;

///
import lombok.Getter;
import lombok.Setter;

///
@Getter
@Setter

///
public final class RequestMetricsEntity {

    ///
    private long timestamp;
    private long id;
    private int latency;
    private String path;
    private String userAgent;
    private boolean isUnknown;
    private short httpStatus;

    ///
}
