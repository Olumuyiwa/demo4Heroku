package com.example.demo;

import com.boohoo.esbdefused.test.tools.Env;
import org.springframework.web.client.RestTemplate;

    public class AkeneoStubClientRewrite {
        private static final String HTTP = "http://";
        private static final String STUB_HOST = Env.stubHost();
        private static final String URL;
        private final RestTemplate restTemplate = new RestTemplate();

        public AkeneoStubClientRewrite() {
        }

        public String getPatchedProduct(String productCode) {
            String url = URL + "/cache/rest/v1/products/{productCode}";
            System.out.println("akeneostubclient url =" + url);

            Object response = this.restTemplate.getForObject(url, String.class, new Object[]{productCode});
            System.out.println("akeneostubclient response =" + response);
            return (String)this.restTemplate.getForObject(url, String.class, new Object[]{productCode});
        }

        public String getPatchedAsset(String assetCode) {
            String url = URL + "/cache/rest/v1/asset-families/studio_images/assets/{assetCode}";
//                               "cache/rest/v1/asset-families/studio_images/assets";
            return (String)this.restTemplate.getForObject(url, String.class, new Object[]{assetCode});
        }

        public String getPatchedProductModel(String productModelCode) {
            String url = URL + "/cache/rest/v1/product-models/{productModelCode}";
            return (String)this.restTemplate.getForObject(url, String.class, new Object[]{productModelCode});
        }
        static {
            URL = "http://" + STUB_HOST + ":8138/api";
        }
    }
