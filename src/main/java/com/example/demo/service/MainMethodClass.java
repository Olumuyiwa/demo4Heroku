package com.example.demo.service;

import com.example.demo.model.AccessToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;

@Slf4j
public class MainMethodClass {
    public static void main(String[] args) throws Exception {
        MainMethodClass m = new MainMethodClass();

        AkeneoClient akeneoClient = new
                AkeneoClient
                ("https://boohoo-staging.cloud.akeneo.com", "4_9gapgezkqecko0gk8o80s4kc80cocw88c8wksg8oc8sk0ss4c",
                        "5rtac72h978c4w4cko0k8k0wg0oskksw0wwo08k4o4wkos08w8", "apiuser_5749", "f555ce2f3");
        AccessToken accessToken = akeneoClient.fetchToken();
        System.out.println("accessToken = " + accessToken);
    }


//        String refreshToken = akeneoClient.refreshAccessToken();
//        System.out.println("refreshToken = " + refreshToken);
//        ResponseEntity getProductModelInAkeneo_Response = akeneoClient.getProductModelInAkeneo("BQQ00007-105-34");
//    System.out.println("getProductModelInAkeneo_Response = " + getProductModelInAkeneo_Response);
//
//String productModelPayload ="{\n" +
//        "   \"familyCode\":\"womens_dresses\",\n" +
//        "   \"brandCode\":\"boohoo\",\n" +
//        "   \"productTeamCode\":\"bhdenim\",\n" +
//        "   \"productGroupCode\":\"bhdenimdresses\",\n" +
//        "   \"name\":\"Israel-test-2 1 1\",\n" +
//        "   \"colourCodes\":[\n" +
//        "      \"110\",\n" +
//        "      \"115\"\n" +
//        "   ],\n" +
//        "   \"sizeCodes\":[\n" +
//        "      \"22\",\n" +
//        "      \"24\",\n" +
//        "      \"14\",\n" +
//        "      \"16\",\n" +
//        "      \"18\",\n" +
//        "      \"20\"\n" +
//        "   ],\n" +
//        "   \"styleCode\":\"maxidress\"\n" +
//        "}";
//        ResponseEntity createNewProductModelInAkeneo_Response = akeneoClient.createNewProductModelInAkeneo(productModelPayload);
//        System.out.println("createNewProductModelInAkeneo_Response = " + createNewProductModelInAkeneo_Response);
//        String requestBody="{\n" +
//                "    \"values\": {\n" +
//                "        \"ean13\": [\n" +
//                "            {\n" +
//                "                \"locale\": null,\n" +
//                "                \"scope\": null,\n" +
//                "                \"data\": \"1234567890121\"\n" +
//                "            }\n" +
//                "        ]\n" +
//                "    }\n" +
//        "}";
//        String sku="BQQ00007-105-34";
//        ResponseEntity updateProductVariantInAkeneo =
//                akeneoClient.updateProductVariantInAkeneo(requestBody,sku);
//
//        System.out.println("updateProductVariantInAkeneo = " + updateProductVariantInAkeneo);
//        String styleCode="BQQ00003";
//        String requestBody4updateProductModelInAkeneo="{\"values\": {\"occasion\": [{\"locale\": null,\"scope\": null,\"data\": \"workwear\"}],\"suppliersku\": [{\"locale\": null,\"scope\": null,\"data\": \"IF62386270 1 1\"}],\"notes\": [{\"locale\": null,\"scope\": null,\"data\": \"casual\"}],\"gender\": [{\"locale\": null,\"scope\": null,\"data\": \"male\"}],\"trend\": [{\"locale\": null,\"scope\": null,\"data\": \"americana\"}],\"origin\": [{\"locale\": null,\"scope\": null,\"data\": \"boughtdesign\"}],\"risk_prop65\": [{\"locale\": null,\"scope\": null,\"data\": false}],\"originator_orderapp\": [{\"locale\": null,\"scope\": null,\"data\": \"daniel.ruxton\"}],\"sustainabilitydetail\": [{\"locale\": null,\"scope\": null,\"data\": \"organiccotton\"}],\"risk_fur\": [{\"locale\": null,\"scope\": null,\"data\": false}],\"length_skirts\": [{\"locale\": null,\"scope\": null,\"data\": \"maxi\"}],\"season\": [{\"locale\": null,\"scope\": null,\"data\": \"aw21\"}],\"stockpath\": [{\"locale\": null,\"scope\": null,\"data\": \"fitted\"}],\"enduse\": [{\"locale\": null,\"scope\": null,\"data\": \"10castops\"}],\"phase\": [{\"locale\": null,\"scope\": null,\"data\": \"fashion\"}],\"hangingstorage\": [{\"locale\": null,\"scope\": null,\"data\": true}],\"fabrication\": [{\"locale\": null,\"scope\": null,\"data\": \"leather\"}],\"design_clothing\": [{\"locale\": null,\"scope\": null,\"data\": \"abstract\"}],\"newness\": [{\"locale\": null,\"scope\": null,\"data\": \"basics\"}],\"detail_clothing\": [{\"locale\": null,\"scope\": null,\"data\": \"3d\"}],\"range_womens\": [{\"locale\": null,\"scope\": null,\"data\": \"main\"}],\"buyer_orderapp\": [{\"locale\": null,\"scope\": null,\"data\": \"Daniel Ruxton\"}],\"countryoforigin\": [{\"locale\": null,\"scope\": null,\"data\": \"TR\"}],\"neckline_womens\": [{\"locale\": null,\"scope\": null,\"data\": \"buttonup\"}],\"sleevelength\": [{\"locale\": null,\"scope\": null,\"data\": \"longsleeve\"}]}}";
//        ResponseEntity updateProductModelInAkeneo =
//                akeneoClient.updateProductModelInAkeneo(requestBody4updateProductModelInAkeneo,styleCode);
//        System.out.println("updateProductModelInAkeneo = " + updateProductModelInAkeneo);
//        String requestBody4updateProductModelTaxonomyInAkeneo ="{\"values\": {\"occasion\": [{\"locale\": null,\"scope\": null,\"data\": \"workwear\"}],\"suppliersku\": [{\"locale\": null,\"scope\": null,\"data\": \"IF62386270 1 1\"}],\"notes\": [{\"locale\": null,\"scope\": null,\"data\": \"casual\"}],\"gender\": [{\"locale\": null,\"scope\": null,\"data\": \"male\"}],\"trend\": [{\"locale\": null,\"scope\": null,\"data\": \"americana\"}],\"origin\": [{\"locale\": null,\"scope\": null,\"data\": \"boughtdesign\"}],\"risk_prop65\": [{\"locale\": null,\"scope\": null,\"data\": false}],\"originator_orderapp\": [{\"locale\": null,\"scope\": null,\"data\": \"daniel.ruxton\"}],\"sustainabilitydetail\": [{\"locale\": null,\"scope\": null,\"data\": \"organiccotton\"}],\"risk_fur\": [{\"locale\": null,\"scope\": null,\"data\": false}],\"length_skirts\": [{\"locale\": null,\"scope\": null,\"data\": \"maxi\"}],\"season\": [{\"locale\": null,\"scope\": null,\"data\": \"aw21\"}],\"stockpath\": [{\"locale\": null,\"scope\": null,\"data\": \"fitted\"}],\"enduse\": [{\"locale\": null,\"scope\": null,\"data\": \"10castops\"}],\"phase\": [{\"locale\": null,\"scope\": null,\"data\": \"fashion\"}],\"hangingstorage\": [{\"locale\": null,\"scope\": null,\"data\": true}],\"fabrication\": [{\"locale\": null,\"scope\": null,\"data\": \"leather\"}],\"design_clothing\": [{\"locale\": null,\"scope\": null,\"data\": \"abstract\"}],\"newness\": [{\"locale\": null,\"scope\": null,\"data\": \"basics\"}],\"detail_clothing\": [{\"locale\": null,\"scope\": null,\"data\": \"3d\"}],\"range_womens\": [{\"locale\": null,\"scope\": null,\"data\": \"main\"}],\"buyer_orderapp\": [{\"locale\": null,\"scope\": null,\"data\": \"Daniel Ruxton\"}],\"countryoforigin\": [{\"locale\": null,\"scope\": null,\"data\": \"TR\"}],\"neckline_womens\": [{\"locale\": null,\"scope\": null,\"data\": \"buttonup\"}],\"sleevelength\": [{\"locale\": null,\"scope\": null,\"data\": \"longsleeve\"}]}}";
//        ResponseEntity updateProductModelTaxonomyInAkeneo =
//                akeneoClient.updateProductModelTaxonomyInAkeneo(requestBody4updateProductModelTaxonomyInAkeneo,styleCode);
//        System.out.println("updateProductModelTaxonomyInAkeneo = " + updateProductModelTaxonomyInAkeneo);
//
//        String imageReferenceAsFileName="justafile.png";
//
//        InputStream inputStreamOfOrderAppAsset =  akeneoClientTest.convertFile2InputStream("justafile.png");
//
//        ResponseEntity uploadAFileToTheAssetMediaEndpointInAkeneo =
//                akeneoClient.uploadAFileToTheAssetMediaEndpointInAkeneo(imageReferenceAsFileName,  inputStreamOfOrderAppAsset);
//        System.out.println("uploadAFileToTheAssetMediaEndpointInAkeneo = " + uploadAFileToTheAssetMediaEndpointInAkeneo);
//
//       String assetMediaFileCodeAsAnObject = uploadAFileToTheAssetMediaEndpointInAkeneo.getHeaders().getFirst("Asset-media-file-code").toString();
//       String styleCodeForLinkAsset="BQQ00003";
//        ResponseEntity linkAssetInAkeneo =
//                akeneoClient.linkAssetInAkeneo(assetMediaFileCodeAsAnObject,styleCodeForLinkAsset);
//        System.out.println("linkAssetInAkeneo = " + linkAssetInAkeneo);
//        List<String> fetchReferenceDataNames = akeneoClient.fetchReferenceDataNames();
//        System.out.println("fetchReferenceDataNames = " + fetchReferenceDataNames);
//        List<ReferenceEntity> fetchReferenceEntities = akeneoClient.fetchReferenceEntities("mens_clothing_sizes");
//        System.out.println("fetchReferenceEntities = " + fetchReferenceEntities);
//        List<ReferenceEntityAttribute> fetchReferenceEntityAttributes = akeneoClient.fetchReferenceEntityAttributes("mens_clothing_sizes");
//        System.out.println("fetchReferenceEntityAttributes = " + fetchReferenceEntityAttributes);
//        List<ReferenceEntityAttributeOption> fetchReferenceEntityAttributeOptions =
//                akeneoClient.fetchReferenceEntityAttributeOptions("mens_clothing_sizes", "seasonality");
//        System.out.println("fetchReferenceEntityAttributeOptions = " + fetchReferenceEntityAttributeOptions);
//        List<Attribute> fetchAttributes = akeneoClient.fetchAttributes();
//        System.out.println("fetchAttributes = " + fetchAttributes);
//        List<AttributeOption> fetchAttributeOptions = akeneoClient.fetchAttributeOptions("allyearround");
//        System.out.println("fetchAttributeOptions = " + fetchAttributeOptions);
//        List<Family> fetchFamilies = akeneoClient.fetchFamilies();
//        System.out.println("fetchFamilies = " + fetchFamilies);
//        List<FamilyVariant> fetchFamilyVariants = akeneoClient.fetchFamilyVariants("childrens_babywear_by_colour_and_size");
//        System.out.println("fetchFamilyVariants = " + fetchFamilyVariants);
//
//        akeneoClientTest.useJsonNode();
//    }

    //    public void createAFile( String simpleFilePathName) throws Exception{
//        Path textFilePath = Paths.get(simpleFilePathName);
//        Files.createFile(textFilePath);
//    }
    public InputStream convertFile2InputStream(String simpleFilePathName) throws Exception {

        File initialFile = new File(simpleFilePathName);
        return FileUtils.openInputStream(initialFile);

    }

    public void useJsonNode() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File("jsonfile.txt"));
        System.out.println("root = " + root);
        String username = root.path("username").asText();
        Long length = root.path("length").asLong();
        System.out.println("username : " + username);
        System.out.println("length : " + length);
    }
}