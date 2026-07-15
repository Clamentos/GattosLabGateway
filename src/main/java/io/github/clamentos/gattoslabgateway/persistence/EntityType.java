package io.github.clamentos.gattoslabgateway.persistence;

///
import java.nio.file.Path;

///..
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

///
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

///
public enum EntityType {

    ///
    LOGS(Path.of("./observability/logs/"), 24, 37),                                               // gattos_lab_gateway_logs_yyyy-mm-dd-HH_idx.log
    REQUEST_METRICS(Path.of("./observability/request_metrics/"), 35, 48),                         // gattos_lab_gateway_request_metrics_yyyy-mm-dd-HH_idx.log
    SYSTEM_METRICS(Path.of("./observability/system_metrics/"), 34, 47),                           // gattos_lab_gateway_system_metrics_yyyy-mm-dd-HH_idx.log
    DYNAMIC_PROPERTIES(Path.of("./observability/dynamic_properties/gattoslab.conf"), -1, -1);     // gattoslab.conf

    ///
    private final Path path;
    private final int dateStartIndex;
    private final int dateEndIndex;

    ///
}
