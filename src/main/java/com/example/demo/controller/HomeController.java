package com.example.demo.controller;

import com.example.demo.config.KeycloakClientConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final KeycloakClientConfig keycloakClient;
//    @Autowired
//    private OAuth2AuthorizedClientService authorizedClientService;
//
//    private OAuth2AuthorizedClient getAuthorizedClient(OAuth2AuthenticationToken authentication) {
//        return this.authorizedClientService.loadAuthorizedClient(
//                authentication.getAuthorizedClientRegistrationId(), authentication.getName());
//    }
//
//    @GetMapping({"/", "index"})
//    public String getIndex(Model model,
//                           @AuthenticationPrincipal OAuth2User principal,
//                           OAuth2AuthenticationToken authentication) {
//
//        OAuth2AuthorizedClient authorizedClient = this.getAuthorizedClient(authentication);
//
//        //authentication.getPrincipal().getAttributes().forEach((k, v) -> System.out.println("key: " + k + " value:" + v));
//        model.addAttribute("userName", authentication.getPrincipal().getAttribute("preferred_username"));
//
//        //authorizedClient.getAccessToken().getScopes().stream().forEach(System.out::println);
//        //System.out.println("authentication." + authorizedClient.getClientRegistration());
//        model.addAttribute("clientName", authorizedClient.getClientRegistration().getClientId());
//        model.addAttribute("scopes", authorizedClient.getAccessToken().getScopes());
//        //Keycloak keycloak = keycloakClient.keycloak();
//        String a = "a";
//
//        return "Index";
//    }

    @GetMapping("/test")
    public String test() {
        return "Khang";
    }

    @GetMapping("/user")
    public ResponseEntity<Object> getUsers() {
        return ResponseEntity.ok().body("Hi user!");
    }
}
