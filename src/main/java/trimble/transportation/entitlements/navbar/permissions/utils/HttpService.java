package trimble.transportation.entitlements.navbar.permissions.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import trimble.transportation.entitlements.navbar.permissions.constants.NavbarPermissionsConstants;
import trimble.transportation.entitlements.navbar.permissions.dto.exception.ExceptionResponse;
import trimble.transportation.entitlements.navbar.permissions.dto.httpservice.ResponseEntity;
import trimble.transportation.entitlements.navbar.permissions.dto.httpservice.ResponseEntityList;
import trimble.transportation.entitlements.navbar.permissions.dto.httpservice.ResponseEntityMap;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.GeneralizedException;
import trimble.transportation.entitlements.navbar.permissions.utils.exception.HttpClientException;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class HttpService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configOverride(FileDescriptor.class).setIsIgnoredType(true);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @SneakyThrows
    public HttpService(HttpClient.Version version) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(60 * 1000L))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .version(version)
                .build();
    }

    public <T> ResponseEntity<T> getEntity(String url, Map<String, String> headers, Class<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntity(response, responseType);
    }

    public <T> ResponseEntityList<T> getEntityList(String url, Map<String, String> headers, Class<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntityList(response, responseType);
    }

    public <K, V> ResponseEntityMap<K, V> getEntityMap(String url, Map<String, String> headers, Class<K> keyType, Class<V> valueType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntityMap(response, keyType, valueType);
    }

    public <T> ResponseEntity<T> postEntity(String url, Map<String, String> headers, Object requestBody, Class<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(convertRequest(requestBody)))
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntity(response, responseType);
    }

    public ResponseEntity<byte[]> postEntityByteArray(String url, Map<String, String> headers, Object requestBody) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(convertRequest(requestBody)))
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<byte[]> response = send(request, HttpResponse.BodyHandlers.ofByteArray());
        return new ResponseEntity<>(getGenericEntity(response), response.headers());
    }

    public <T> ResponseEntityList<T> postEntityList(String url, Map<String, String> headers, List<? extends Object> requestBodyList, Class<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(convertRequest(requestBodyList)))
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntityList(response, responseType);
    }

    public <K, V> ResponseEntityMap<K, V> postEntityMap(String url, Map<String, String> headers, Map<? extends Object, ? extends Object> requestBodyMap, Class<K> keyType, Class<V> valueType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(convertRequest(requestBodyMap)))
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntityMap(response, keyType, valueType);
    }

    public <T> ResponseEntity<T> putEntity(String url, Map<String, String> headers, Object requestBody, Class<T> responseType) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(convertRequest(requestBody)))
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
        return buildResponseEntity(response, responseType);
    }

    public void deleteEntity(String url, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(url));
        HttpRequest request = setHeaders(headers, builder).build();
        send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String convertRequest(Object requestBody) {
        try {
            if (Objects.nonNull(requestBody)) {
                if (requestBody instanceof String)
                    return requestBody.toString();
                return objectMapper.writeValueAsString(requestBody);
            }
            throw new HttpClientException("400", "Request body is null");
        } catch (JsonProcessingException e) {
            throw new HttpClientException("500", e, "Invalid request body");
        }
    }

    private <T> List<T> readListValue(String responseBody, Class<T> responseType) {
        try {
            return isNotBlank(responseBody) ?
                    objectMapper.readValue(responseBody, objectMapper.getTypeFactory().constructCollectionType(List.class, responseType))
                    : Collections.emptyList();
        } catch (JsonProcessingException e) {
            throw new HttpClientException("500", e, NavbarPermissionsConstants.INVALID_RESPONSE_TYPE);
        }
    }

    private <K, V> Map<K, V> readMapValue(String responseBody, Class<K> key, Class<V> value) {
        try {
            return isNotBlank(responseBody) ?
                    objectMapper.readValue(responseBody, objectMapper.getTypeFactory().constructMapType(Map.class, key, value))
                    : Collections.emptyMap();
        } catch (JsonProcessingException e) {
            throw new HttpClientException("500", e, NavbarPermissionsConstants.INVALID_RESPONSE_TYPE);
        }
    }

    private <T> T readValue(String responseBody, Class<T> responseType) {
        try {
            if (isNotBlank(responseBody) && responseType.equals(String.class)) {
                return (T) responseBody;
            }
            return isNotBlank(responseBody) ?
                    objectMapper.readValue(responseBody, responseType)
                    : null;
        } catch (JsonProcessingException e) {
            throw new HttpClientException("500", e, NavbarPermissionsConstants.INVALID_RESPONSE_TYPE);
        }
    }

    private <Y> HttpResponse<Y> send(HttpRequest httpRequest, HttpResponse.BodyHandler<Y> bodyHandler) {
        String requestMessage = getRequest(httpRequest);
        try {
            log.debug("Version: " + httpClient.version());
            Map<String, List<String>> headers = httpRequest.headers().map().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            log.debug("Request headers: " + headers);
            log.debug("Request: Sending " + requestMessage);
            return httpClient.send(httpRequest, bodyHandler);
        } catch (IOException | InterruptedException e) {
            log.debug(ExceptionUtils.getMessage(e));
            log.debug("Exception: " + requestMessage);
            throw new HttpClientException("Unable to send httpRequest: " + getRequest(httpRequest), e, "500");
        }
    }

    private HttpRequest.Builder setHeaders(Map<String, String> headers, HttpRequest.Builder builder) {
        if (Objects.nonNull(headers) && headers.size() > 0 && Objects.nonNull(builder)) {
            headers.entrySet()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(set -> Objects.nonNull(set.getKey()))
                    .forEach(set -> builder.setHeader(set.getKey(), set.getValue()));
        }
        return builder;
    }

    private <T> ResponseEntity<T> buildResponseEntity(HttpResponse<String> response, Class<T> responseType) {
        String entityAsString = getGenericEntity(response);
        T entity = readValue(entityAsString, responseType);
        return new ResponseEntity<>(entity, response.headers());
    }

    private <T> ResponseEntityList<T> buildResponseEntityList(HttpResponse<String> response, Class<T> responseType) {
        String entityAsString = getGenericEntity(response);
        List<T> entityList = readListValue(entityAsString, responseType);
        return new ResponseEntityList<>(entityList, response.headers());
    }

    private <K, V> ResponseEntityMap<K, V> buildResponseEntityMap(HttpResponse<String> response, Class<K> keyType, Class<V> valueType) {
        String entityAsString = getGenericEntity(response);
        Map<K, V> entityMap = readMapValue(entityAsString, keyType, valueType);
        return new ResponseEntityMap<>(entityMap, response.headers());
    }

    @SneakyThrows
    private <Y> Y getGenericEntity(HttpResponse<Y> response) {
        int statusCode = response.statusCode();
        String message = getResponse(statusCode, response);
        log.debug("Response: " + message);
        if (statusCode >= 100 && statusCode < 200) {
            throw new HttpClientException(message, String.valueOf(statusCode));
        } else if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        } else if (statusCode >= 300 && statusCode < 400) {
            throw new HttpClientException(message, String.valueOf(statusCode));
        } else if (statusCode == 417) {
            message = String.valueOf(response.body());
            if (response.body() instanceof byte[]) {
                message = new String((byte[]) response.body(), StandardCharsets.UTF_8);
            }
            var briefMessage = objectMapper.readValue(message, ExceptionResponse.class);
            throw new GeneralizedException(briefMessage.getMessage());
        } else if (statusCode >= 400 && statusCode < 500) {
            throw new HttpClientException(message, String.valueOf(statusCode));
        } else if (statusCode >= 500 && statusCode < 600) {
            throw new HttpClientException(message, String.valueOf(statusCode));
        } else {
            throw new HttpClientException("Invalid Status code received", String.valueOf(statusCode));
        }
    }

    private <Y> String getResponse(int statusCode, HttpResponse<Y> httpResponse) {
        String message = String.valueOf(httpResponse.body());
        if (httpResponse.body() instanceof byte[]) {
            message = new String((byte[]) httpResponse.body(), StandardCharsets.UTF_8);
        }
        StringBuilder sb = new StringBuilder();
        HttpRequest httpRequest = httpResponse.request();
        sb.append(getRequest(httpRequest));
        sb.append(" responded with ");
        sb.append(" status code of ");
        sb.append(statusCode);
        if (StringUtils.isBlank(message)) {
            sb.append(" with an empty body");
        } else {
            sb.append(" with non-empty body of ");
            sb.append(message);
        }
        Map<String, List<String>> headers = httpResponse.headers().map().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        log.debug("Response headers: " + headers);
        return sb.toString();
    }

    private String getRequest(HttpRequest httpRequest) {
        StringBuilder sb = new StringBuilder();
        String method = httpRequest.method();
        String uri = httpRequest.uri().toString();
        sb.append(method.toUpperCase());
        sb.append(" call to ");
        sb.append(uri);
        return sb.toString();
    }
}
