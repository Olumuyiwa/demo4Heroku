package com.example.demo.service;

import com.boohoo.esbdefused.canonical.pim.*;
import com.example.demo.model.Family;
import com.example.demo.model.FamilyVariant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@Slf4j
@Getter
public class PimClientService {

    private AkeneoClient akeneoClient;

    public PimClientService(String baseUrl, String clientId, String secret, String username, String password) {
        akeneoClient = new AkeneoClient(baseUrl, clientId, secret, username, password);
    }


    public String refreshAccessToken() {
        return akeneoClient.refreshAccessToken();
    }

    public List<String> fetchReferenceDataNames() {
        return akeneoClient.fetchReferenceDataNames();
    }

    public List<String> fetchReferenceDataNames(String url) {
        return akeneoClient.fetchReferenceDataNames(url);

    }

    public List<ReferenceEntity> fetchReferenceEntities(String referenceEntityCode) {
        return akeneoClient.fetchReferenceEntities(referenceEntityCode);
    }

    public List<ReferenceEntity> fetchReferenceEntities(String url, String referenceEntityCode) {
        return akeneoClient.fetchReferenceEntities(url, referenceEntityCode);
    }

    public List<ReferenceEntityAttribute> fetchReferenceEntityAttributes(String referenceEntityCode) {
        return akeneoClient.fetchReferenceEntityAttributes(referenceEntityCode);
    }

    public List<ReferenceEntityAttributeOption> fetchReferenceEntityAttributeOptions(String referenceEntityCode, String referenceEntityAttributeCode) {
        return akeneoClient.fetchReferenceEntityAttributeOptions(referenceEntityCode, referenceEntityAttributeCode);
    }

    public List<Attribute> fetchAttributes() {
        return akeneoClient.fetchAttributes();

    }

    public List<Attribute> fetchAttributes(String url) {
        return akeneoClient.fetchAttributes(url);
    }

    public List<AttributeOption> fetchAttributeOptions(String attributeCode) {
        return akeneoClient.fetchAttributeOptions(attributeCode);
    }

    public List<AttributeOption> fetchAttributeOption(String url) {
        return akeneoClient.fetchAttributeOptions(url);
    }

    public List<Family> fetchFamilies() {
        return akeneoClient.fetchFamilies();

    }

    public List<Family> fetchFamilies(String url) {
        return akeneoClient.fetchFamilies(url);
    }

    public List<FamilyVariant> fetchFamilyVariants(String familyCode) {
        return akeneoClient.fetchFamilyVariants(familyCode);
    }

    public List<ObjectNode> searchForProducts(String searchIdentifier) throws URISyntaxException, MalformedURLException {
        return akeneoClient.searchForProducts(searchIdentifier);
    }

    public List<ObjectNode> searchForProduct(URI uri) throws URISyntaxException, MalformedURLException {
        return akeneoClient.searchForProduct(uri);
    }

    public void updateProduct(String identifier, JsonNode body) {
        akeneoClient.updateProduct(identifier, body);
    }


    public ResponseEntity fetchProductModelInAkeneo(String styleCode) {

        return akeneoClient.fetchProductModelInAkeneo(styleCode);
    }

    public ResponseEntity createNewProductModelInAkeneo(String requestPayload) {
        return akeneoClient.createNewProductModelInAkeneo(requestPayload);
    }

    public ResponseEntity updateProductVariantInAkeneo(String requestBody, String sku) {
        return akeneoClient.updateProductVariantInAkeneo(requestBody, sku);

    }

    public ResponseEntity updateProductModelInAkeneo(String requestBody, String styleCode) {
        return akeneoClient.updateProductModelInAkeneo(requestBody, styleCode);

    }

    public ResponseEntity updateProductModelTaxonomyInAkeneo(String requestBody, String styleCode) {
        return akeneoClient.updateProductModelTaxonomyInAkeneo(requestBody, styleCode);


    }

    public ResponseEntity linkAssetInAkeneo(String assetMediaFileCode, String styleCode)
            throws JsonProcessingException {
        return akeneoClient.linkAssetInAkeneo(assetMediaFileCode, styleCode);


    }


    public ResponseEntity uploadAFileToTheAssetMediaEndpointInAkeneo(String imageReferenceAsFileName, InputStream inputStreamOfOrderAppAsset) throws IOException {
        return akeneoClient.uploadAFileToTheAssetMediaEndpointInAkeneo(imageReferenceAsFileName, inputStreamOfOrderAppAsset);


    }

}
