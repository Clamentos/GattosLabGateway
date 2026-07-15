package io.github.clamentos.gattoslabgateway.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public final class RequestExchangerHandler implements HttpHandler {

    private final Set<String> allowedHeaders;
    private final Set<String> allowedHeadersLower;

    private final Map<URI, URI> pathMappings;
    private final HttpClient httpClient; // config: https://docs.oracle.com/en/java/javase/26/docs/api/java.net.http/module-summary.html

    private final List<String> chunkedTransferHeader;

    public RequestExchangerHandler() {

        allowedHeaders = Set.of(

            "Accept",
            "Accept-Charset",
            "Accept-Encoding",
            "Access-Control-Request-Method",
            "Authorization",
            "Pragma",
            "Cache-Control",
            "Content-Encoding",
            "Content-Type",
            "Cookie",
            "Date",
            "Forwarded",
            "If-Modified-Since",
            "If-Unmodified-Since",
            "Origin",
            "Transfer-Encoding",
            "User-Agent"
        );

        allowedHeadersLower = allowedHeaders.stream().map(String::toLowerCase).collect(Collectors.toSet());

        pathMappings = new ConcurrentHashMap<>();
        httpClient = HttpClient.newHttpClient();

        chunkedTransferHeader = List.of("chunked");

        pathMappings.put(URI.create("/"), URI.create("http://localhost:8081/"));
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {

        // If here, the request passed all filters.
        final URI destination = pathMappings.get(exchange.getRequestURI());

        if(destination != null) {

            final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()

                .uri(destination)
                .method(exchange.getRequestMethod(), BodyPublishers.ofInputStream(exchange::getRequestBody))
            ;

            for(final Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {

                if(allowedHeaders.contains(header.getKey()) || allowedHeadersLower.contains(header.getKey())) {

                    for(final String headerValue : header.getValue()) requestBuilder.header(header.getKey(), headerValue);
                }
            }

            try {

                final HttpResponse<InputStream> response = httpClient.send(requestBuilder.build(), BodyHandlers.ofInputStream());

                exchange.getResponseHeaders().putAll(response.headers().map());
                //exchange.getResponseHeaders().put("Transfer-Encoding", chunkedTransferHeader);
                final long size = Long.parseLong(response.headers().map().getOrDefault("Content-Length", response.headers().map().get("content-length")).get(0));
                exchange.sendResponseHeaders(response.statusCode(), size);
                response.body().transferTo(exchange.getResponseBody());
                exchange.getResponseBody().flush();
            }

            catch(final Exception exc) {

                if(exc instanceof InterruptedException) Thread.currentThread().interrupt();
                // ...
            }
        }

        else {

            // error...
        }
    }
}
