package com.example.demo.controller;

import com.example.demo.model.AccessToken;
import com.example.demo.service.PimClientService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController  {
    @GetMapping("/welcome")
    public String getWelcome(){

/*
*
*
    base-url: 'https://boohoo-staging.cloud.akeneo.com'
    client-id: '4_9gapgezkqecko0gk8o80s4kc80cocw88c8wksg8oc8sk0ss4c'
    secret: '5rtac72h978c4w4cko0k8k0wg0oskksw0wwo08k4o4wkos08w8'
    username: 'apiuser_5749'
    password: 'f555ce2f3'
* */


        return "welcome";
    }

    public static void main(String[] args) {
        PimClientService pimClientService = new
                PimClientService
                ( "https://boohoo-staging.cloud.akeneo.com","4_9gapgezkqecko0gk8o80s4kc80cocw88c8wksg8oc8sk0ss4c",
                        "5rtac72h978c4w4cko0k8k0wg0oskksw0wwo08k4o4wkos08w8","apiuser_5749","f555ce2f3" );
//        HttpHeaders httpHeaders =  pimClientService.getHeaders();
//        AccessToken accessToken = pimClientService.fetchToken();
    }
}
