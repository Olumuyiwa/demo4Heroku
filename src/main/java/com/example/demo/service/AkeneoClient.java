package com.example.demo.service;

import com.boohoo.esbdefused.canonical.pim.*;
import com.example.demo.config.AkeneoAPIUrls;
import com.example.demo.model.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;


@Slf4j
@Getter
public class AkeneoClient {

    private static final String AUTH_PREFIX = "Bearer ";
    private static final String GRANT_TYPE = "password";
    private static final int HTTP_TIMEOUT_VALUE = 6000;
    private static final int HTTP_MAXIMUM_RETRYABLE_ATTEMPT = 3;
    private static final int HTTP_BACKOFF_DELAY = 5000;
    private static final int HTTP_BACKOFF_MULTIPLIER = 2;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final String username;
    @ToString.Exclude
    private final String password;
    private AccessToken accessToken;
    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders headers;

    public AkeneoClient(String baseUrl, String clientId, String secret, String username, String password) {
        this.baseUrl = baseUrl;
        this.username = username;
        this.password = password;
        this.headers = createAuthorizationHeader(clientId, secret);

//		this.accessToken = fetchToken();
//		this.restTemplate = new RestTemplateBuilder(rt -> rt.getInterceptors().add((request, body, execution) -> {
//			if (accessToken.willExpireSoon()) {
//				accessToken = fetchToken();
//			}
//			request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//			request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			request.getHeaders().add(HttpHeaders.AUTHORIZATION, accessToken.getToken());
//			return execution.execute(request, body);
//		})).requestFactory(() -> new HttpComponentsClientHttpRequestFactory()).build();
    }


    public HttpHeaders createAuthorizationHeader(String clientId, String secret) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.AUTHORIZATION, "Basic "
                + Base64.getEncoder().encodeToString((clientId + ":" + secret).getBytes()));
        requestHeaders.setBasicAuth(clientId, secret);
        return requestHeaders;
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public AccessToken fetchToken() {
        log.info("Token_generated_afresh because accessToken  {} ", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        final AuthorizationBody authorizationBody = new AuthorizationBody(getUsername(), getPassword(), GRANT_TYPE);
        final String url = getBaseUrl() + AkeneoAPIUrls.AUTH_PATH;
        ResponseEntity<JsonNode> responseBody = restTemplate.postForEntity(url, new HttpEntity<>(authorizationBody, headers), JsonNode.class);

        if (responseBody.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to refresh token as response body is empty. Url: '%s'", url));
        } else {
            final String accessTokenValue = AUTH_PREFIX + responseBody.getBody().get("access_token").asText();
            final LocalDateTime expiryTime = LocalDateTime.now().plusSeconds(responseBody.getBody().get("expires_in").asLong());
            log.info("Refreshed token. Next expiry time: {}", expiryTime);
            return AccessToken.builder().expiryTime(expiryTime).token(accessTokenValue).build();
        }
    }

    public String refreshAccessToken() {
        if (accessToken == null || accessToken.willExpireSoon()) {
            log.info("Token_generated_afresh because accessToken =" + accessToken);
            accessToken = fetchToken();

        } else {
            log.info("Token_reused");
        }
        return accessToken.getToken();
    }

    private HttpHeaders createHttpHeader() {
        String token = refreshAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", token);
        return headers;
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<String> fetchReferenceDataNames() {
        final String url = String.format(AkeneoAPIUrls.REFERENCE_ENTITIES_PATH, getBaseUrl());
        log.info("fetchReferenceDataNames url is {}", url);
        return fetchReferenceDataNames(url);
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<String> fetchReferenceDataNames(String url) {

        List<String> referenceDataNames = new ArrayList<>();
        //	ResponseEntity<JsonNode> responseEntity = restTemplate.getForEntity(url, JsonNode.class);
        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'reference data' as response body is empty. Url: '%s'", url));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseEntity.getBody();
            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                if (item.has("code")) {
                    referenceDataNames.add(item.get("code").asText());
                }
            }
            if (responseBody.get("_links").has("next")) {
                final String nextUrl = responseBody.get("_links").get("next").get("href").asText();
                referenceDataNames.addAll(fetchReferenceDataNames(nextUrl));
            }
            log.debug("Found {} reference data names", referenceDataNames.size());
            return referenceDataNames;
        }
    }

    public List<ReferenceEntity> fetchReferenceEntities(String referenceEntityCode) {
        final String url = String.format(AkeneoAPIUrls.REFERENCE_ENTITY_PATH, getBaseUrl(), referenceEntityCode);
        return fetchReferenceEntities(url, referenceEntityCode);
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<ReferenceEntity> fetchReferenceEntities(String url, String referenceEntityCode) {
        List<ReferenceEntity> referenceEntities = new ArrayList<>();
        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'reference entity values' as response body is empty. Url: '%s'", url));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseEntity.getBody();
            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                ReferenceEntity referenceEntity = mapper.convertValue(item, ReferenceEntity.class);
                referenceEntity.setReferenceDataName(referenceEntityCode); //TODO should not be needed
                referenceEntities.add(referenceEntity);
            }
            if (responseBody.get("_links").has("next")) {
                final String nextUrl = responseBody.get("_links").get("next").get("href").asText();
                referenceEntities.addAll(fetchReferenceEntities(nextUrl, referenceEntityCode));
            }
            log.debug("Found {} reference entity values", referenceEntities.size());
            return referenceEntities;
        }
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<ReferenceEntityAttribute> fetchReferenceEntityAttributes(String referenceEntityCode) {
        final String url = String.format(AkeneoAPIUrls.REFERENCE_ENTITY_ATTRIBUTE_PATH, getBaseUrl(), referenceEntityCode);
        List<ReferenceEntityAttribute> referenceEntities = new ArrayList<>();
        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'reference entity attributes' as response body is empty. Url: '%s'", url));
        } else {
            final ArrayNode items = (ArrayNode) responseEntity.getBody();

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                ReferenceEntityAttribute referenceEntityAttribute = mapper.convertValue(item, ReferenceEntityAttribute.class);
                referenceEntityAttribute.setReferenceDataName(referenceEntityCode);
                referenceEntities.add(referenceEntityAttribute);
            }
            log.debug("Found {} reference entity attributes", referenceEntities.size());
            return referenceEntities;
        }
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<ReferenceEntityAttributeOption> fetchReferenceEntityAttributeOptions(String referenceEntityCode, String referenceEntityAttributeCode) {
        final String url = String.format(AkeneoAPIUrls.REFERENCE_ENTITY_ATTRIBUTE_OPTIONS_PATH, getBaseUrl(), referenceEntityCode, referenceEntityAttributeCode);
        List<ReferenceEntityAttributeOption> referenceEntityAttributeOptions = new ArrayList<>();

        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'reference entity attribute options' as response body is empty. Url: '%s'", url));
        } else {
            final ArrayNode items = (ArrayNode) responseEntity.getBody();

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                ReferenceEntityAttributeOption referenceEntityAttribute = mapper.convertValue(item, ReferenceEntityAttributeOption.class);
                referenceEntityAttribute.setAttributeCode(referenceEntityAttributeCode);
                referenceEntityAttribute.setReferenceDataName(referenceEntityCode);
                referenceEntityAttributeOptions.add(referenceEntityAttribute);
            }
            log.debug("Found {} reference entity attribute options", referenceEntityAttributeOptions.size());
            return referenceEntityAttributeOptions;
        }
    }

    public List<Attribute> fetchAttributes() {
        final String url = String.format(AkeneoAPIUrls.ATTRIBUTES_PATH, getBaseUrl());
        return fetchAttributes(url);
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<Attribute> fetchAttributes(String url) {
        List<Attribute> attributes = new ArrayList<>();
        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'attributes' as response body is empty. Url: '%s'", url));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseEntity.getBody();
            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                attributes.add(mapper.convertValue(item, Attribute.class));
            }
            if (responseBody.get("_links").has("next")) {
                final String nextUrl = responseBody.get("_links").get("next").get("href").asText();
                attributes.addAll(fetchAttributes(nextUrl));
            }
            log.debug("Found {} attributes", attributes.size());
            return attributes;
        }
    }

    public List<AttributeOption> fetchAttributeOptions(String attributeCode) {
        final String url = String.format(AkeneoAPIUrls.ATTRIBUTE_OPTIONS_PATH, getBaseUrl(), attributeCode);
        return fetchAttributeOption(url);
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<AttributeOption> fetchAttributeOption(String url) {
        List<AttributeOption> attributeOptions = new ArrayList<>();
        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'attribute options' as response body is empty. Url: '%s'", url));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseEntity.getBody();
            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                attributeOptions.add(mapper.convertValue(item, AttributeOption.class));
            }
            if (responseBody.get("_links").has("next")) {
                final String nextUrl = responseBody.get("_links").get("next").get("href").asText();
                attributeOptions.addAll(fetchAttributeOption(nextUrl));
            }
            log.debug("Found {} attribute options", attributeOptions.size());
            return attributeOptions;
        }
    }

    public List<Family> fetchFamilies() {
        final String url = String.format(AkeneoAPIUrls.FAMILIES_PATH, getBaseUrl());
        return fetchFamilies(url);
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<Family> fetchFamilies(String url) {
        List<Family> families = new ArrayList<>();

        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'families' as response body is empty. Url: '%s'", url));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseEntity.getBody();
            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                families.add(mapper.convertValue(item, Family.class));
            }
            if (responseBody.get("_links").has("next")) {
                final String nextUrl = responseBody.get("_links").get("next").get("href").asText();
                families.addAll(fetchFamilies(nextUrl));
            }
            log.debug("Found {} families", families.size());
            return families;
        }
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<FamilyVariant> fetchFamilyVariants(String familyCode) {
        final String url = String.format(AkeneoAPIUrls.FAMILY_VARIANTS_PATH, getBaseUrl(), familyCode);
        List<FamilyVariant> familyVariants = new ArrayList<>();

        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        if (responseEntity.getBody() == null) {
            throw new IllegalArgumentException(String.format("Unable to fetch 'family variants' as response body is empty. Url: '%s'", url));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseEntity.getBody();
            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                familyVariants.add(mapper.convertValue(item, FamilyVariant.class));
            }
            log.debug("Found {} family variants", familyVariants.size());
            return familyVariants;
        }
    }

    public List<ObjectNode> searchForProducts(String searchIdentifier) throws URISyntaxException, MalformedURLException {
        final String search = String.format("{\"parent\":[{\"operator\":\"=\",\"value\":\"%s\"}]}", searchIdentifier);
        final String url = String.format(AkeneoAPIUrls.SEARCH_PRODUCTS_PATH, getBaseUrl());
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.addParameter("search", search);

        return searchForProduct(uriBuilder.build());
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public List<ObjectNode> searchForProduct(URI uri) throws URISyntaxException, MalformedURLException {
        List<ObjectNode> productItems = new ArrayList<>();
        String url = String.valueOf(uri.toURL());
        ResponseEntity<?> responseEntity = callAkeneoAPI("", url, "GET", JsonNode.class);
        JsonNode responseItem = (JsonNode) responseEntity.getBody();
        if (responseItem == null) {
            throw new IllegalArgumentException(String.format("Search on 'product' response body is empty. Url: '%s'", uri));
        } else {
            final ObjectNode responseBody = (ObjectNode) responseItem;

            final ArrayNode items = (ArrayNode) responseBody.get("_embedded").get("items");

            for (int i = 0; i < items.size(); i++) {
                ObjectNode item = (ObjectNode) items.get(i);
                productItems.add(item);
            }
            if (responseBody.get("_links").has("next")) {
                final String nextUrl = responseBody.get("_links").get("next").get("href").asText();
                productItems.addAll(searchForProduct(new URIBuilder(nextUrl).build()));
            }
            log.debug("Found {} products", productItems.size());
            return productItems;
        }
    }

    @Retryable(value = RuntimeException.class, maxAttempts = HTTP_MAXIMUM_RETRYABLE_ATTEMPT, backoff = @Backoff(delay = HTTP_BACKOFF_DELAY, multiplier = HTTP_BACKOFF_MULTIPLIER))
    public void updateProduct(String identifier, JsonNode body) {
        final String url = getBaseUrl() + AkeneoAPIUrls.PRODUCTS_PATH + "/" + identifier;
        ResponseEntity<?> responseEntity = callAkeneoAPI(body, url, "PATCH", Void.class);
    }

    private HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(HTTP_TIMEOUT_VALUE);
        requestFactory.setReadTimeout(HTTP_TIMEOUT_VALUE);
        return requestFactory;
    }

    private ResponseEntity<?> callAkeneoAPI(Object requestBody, String url, String httpMethod, Class classType) {
        ResponseEntity<?> response;
        HttpHeaders headers = createHttpHeader();
        HttpEntity entity = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = getHttpComponentsClientHttpRequestFactory();
        restTemplate.setRequestFactory(requestFactory);
        try {
            if (httpMethod.equals("GET")) {
                entity = new HttpEntity<>(headers);
                response = restTemplate.exchange(url, HttpMethod.GET, entity, classType, 1);

            } else if (httpMethod.equals("POST")) {
                entity = new HttpEntity<>(requestBody, headers);
                response = restTemplate.exchange(url, HttpMethod.POST, entity, classType);
            } else if (httpMethod.equals("PATCH")) {
                log.info("HttpMethod.PATCH called ");

                entity = new HttpEntity<>(requestBody, headers);
                response = restTemplate.exchange(url, HttpMethod.PATCH, entity, classType);
            } else {
                throw new Exception("The request failed because the HTTP method is not allowed");
            }
        } catch (Exception ee) {
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            String responseString = ee.toString();
            if (ee instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) ee;
                httpStatus = ex.getStatusCode();
                responseString = ex.getResponseBodyAsString();
            }
            response = new ResponseEntity<>(
                    responseString,
                    headers,
                    httpStatus
            );
        }
        return response;
    }


    public ResponseEntity fetchProductModelInAkeneo(String styleCode) {
        String url = getBaseUrl() + AkeneoAPIUrls.PRODUCT_MODEL_PATH
                + "/" + styleCode;
        log.info("delete|\nThe accessToken is  {}\n The url is this {}", accessToken, url);

        ResponseEntity response = callAkeneoAPI("", url, "GET", String.class);
        return response;
    }

    public ResponseEntity createNewProductModelInAkeneo(String requestPayload) {
        ResponseEntity response = null;
        String accessToken = refreshAccessToken();
        String url = getBaseUrl() + AkeneoAPIUrls.PRODUCTS_WIZARD_WITH_PRODUCT_PATH;
        response = callAkeneoAPI(requestPayload, url, "POST", String.class);
        return response;
    }

    public ResponseEntity updateProductVariantInAkeneo(String requestBody, String sku) {
        ResponseEntity response = null;
        String accessToken = refreshAccessToken();
        log.info("The accessToken is  {} ", accessToken);
        String url = getBaseUrl() + AkeneoAPIUrls.PRODUCTS_PATH + "/" + sku;
        response = callAkeneoAPI(requestBody, url, "PATCH", String.class);
        return response;

    }

    public ResponseEntity updateProductModelInAkeneo(String requestBody, String styleCode) {
        ResponseEntity response = null;
        String accessToken = refreshAccessToken();
        log.info("The accessToken is  {} ", accessToken);
        log.info("The request to updateProductModelInAkeneo for stylecode {} is  {} ", styleCode, requestBody);
        String url = getBaseUrl() + AkeneoAPIUrls.PRODUCTS_WIZARD_WITH_PRODUCT_PATH + "/" + styleCode;
        response = callAkeneoAPI(requestBody, url, "PATCH", String.class);
        return response;
    }

    public ResponseEntity updateProductModelTaxonomyInAkeneo(String requestBody, String styleCode) {
        ResponseEntity response = null;
        String url = getBaseUrl() + AkeneoAPIUrls.PRODUCTS_WIZARD_WITH_PRODUCT_PATH + "/" + styleCode;
        String accessToken = refreshAccessToken();
        log.info("The request to updateProductModelInAkeneo for stylecode {} is  {} ", styleCode, requestBody);
        log.info("The accessToken is  {} . The url is {}", accessToken, url);
        response = callAkeneoAPI(requestBody, url, "PATCH", String.class);
        return response;

    }

    public ResponseEntity linkAssetInAkeneo(String assetMediaFileCode, String styleCode)
            throws JsonProcessingException {
        String accessToken = refreshAccessToken();
        log.info("The accessToken is  {} ", accessToken);
        String url = getBaseUrl() + AkeneoAPIUrls.IMAGE_LINKING_PATH + styleCode;

        Values values = new Values();
        Media media = new Media();
        media.setData(assetMediaFileCode);
        media.setLocale(null);
        media.setChannel(null);
        List<Media> mediaArrayList = new ArrayList<Media>();
        mediaArrayList.add(media);
        values.setMedia(mediaArrayList);

        AssetLink assetOrImageLinkingRootClass = new AssetLink();
        assetOrImageLinkingRootClass.setCode(styleCode);
        assetOrImageLinkingRootClass.setValues(values);

        String assetOrImageLinkingRootClassString = null;
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        assetOrImageLinkingRootClassString = objectMapper.writeValueAsString(assetOrImageLinkingRootClass);
        log.info("\nThe request to link asset is  {}. \nThe link asset the endpoint is {}.", assetOrImageLinkingRootClassString, url);
        ResponseEntity response = callAkeneoAPI(assetOrImageLinkingRootClassString, url, "PATCH", String.class);
        return response;
    }


    public ResponseEntity uploadAFileToTheAssetMediaEndpointInAkeneo(String imageReferenceAsFileName, InputStream inputStreamOfOrderAppAsset) throws IOException {
        ResponseEntity response;
        HttpHeaders headers = createHttpHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String url = getBaseUrl() + AkeneoAPIUrls.IMAGE_UPLOAD_PATH;
        MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
        ContentDisposition contentDisposition = ContentDisposition
                .builder("form-data")
                .name("file")
                .filename(imageReferenceAsFileName)
                .build();
        RestTemplate restTemplate = new RestTemplate();
        fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
        HttpEntity<byte[]> fileEntity = new HttpEntity<>(IOUtils.toByteArray(inputStreamOfOrderAppAsset), fileMap);
        log.info("Uploading asset from image file {} from the input stream {} = ", imageReferenceAsFileName, inputStreamOfOrderAppAsset);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileEntity);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class);
        inputStreamOfOrderAppAsset.close();
        return response;
    }

}
