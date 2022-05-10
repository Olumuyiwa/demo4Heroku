package com.example.demo.config;

public final class AkeneoAPIUrls {

    public static final String PRODUCTS_PATH = "/api/rest/v1/products";
    public static final String PRODUCT_MODEL_PATH = "/api/rest/v1/product-models";
    public static final String PRODUCTS_WIZARD_WITH_PRODUCT_PATH = "/api/rest/product-wizard/v1/products";
    public static final String IMAGE_LINKING_PATH = "/api/rest/v1/asset-families/orderapp_images/assets/";
    public static final String IMAGE_UPLOAD_PATH = "/api/rest/v1/asset-media-files";
    public static final String FAMILY_VARIANTS_PATH = "%s/api/rest/v1/families/%s/variants";
    public static final String SEARCH_PRODUCTS_PATH = "%s/api/rest/v1/products?pagination_type=search_after&limit=100";
    public static final String AUTH_PATH = "/api/oauth/v1/token?throwExceptionOnFailure=false";
    public static final String REFERENCE_ENTITIES_PATH = "%s/api/rest/v1/reference-entities?limit=100";
    public static final String REFERENCE_ENTITY_PATH = "%s/api/rest/v1/reference-entities/%s/records?limit=100";
    public static final String ATTRIBUTE_OPTIONS_PATH = "%s/api/rest/v1/attributes/%s/options?limit=100";
    public static final String ATTRIBUTES_PATH = "%s/api/rest/v1/attributes?limit=100";
    public static final String LOOKUP_COLOUR_BY_CODE_PATH = "%s/api/rest/product-wizard/v1/colours/%s";
    public static final String FAMILIES_PATH = "%s/api/rest/v1/families?limit=100";
    public static final String SEARCH_PRODUCTS_BY_ATTRIBUTE_PATH = "/api/rest/v1/products?pagination_type=search_after&limit=100&search={\"%s\":[{\"operator\":\"IN\",\"value\":%s}]}";
    public static final String REFERENCE_ENTITY_ATTRIBUTE_PATH = "%s/api/rest/v1/reference-entities/%s/attributes?limit=100";
    public static final String REFERENCE_ENTITY_ATTRIBUTE_OPTIONS_PATH = "%s/api/rest/v1/reference-entities/%s/attributes/%s/options?limit=100";
}
