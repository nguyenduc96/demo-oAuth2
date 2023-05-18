package com.nguyenduc.controller;

import com.nguyenduc.config.CustomOauth2User;
import com.nguyenduc.model.User;
import com.nguyenduc.oauth_const.Provider;
import com.nguyenduc.repository.UserRepository;
import com.nguyenduc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@SuppressWarnings("all")
public class LoginController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserService userService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private static String authorizationRequestBaseUri
            = "/oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls
            = new HashMap<>();

    @GetMapping("")
    public String login(Model model) {
//        Iterable<ClientRegistration> clientRegistrations = null;
//        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
//                .as(Iterable.class);
//        if (type != ResolvableType.NONE &&
//                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
//            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
//        }

//        clientRegistrations.forEach(registration ->
//                oauth2AuthenticationUrls.put(registration.getClientName(),
//                        authorizationRequestBaseUri + "/" + registration.getRegistrationId()));
//        model.addAttribute("urls", oauth2AuthenticationUrls);

        return "index";
    }

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/error")
    public String error() {
        return "fail";
    }


    @GetMapping("/login-success")
    public String login(Model model, OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());

        String userInfoEndpointUri = client.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();


        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
//            HttpClient httpClient = HttpClient.newHttpClient();


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken()
                    .getTokenValue());
//            httpClient.executor();
            HttpEntity entity = new HttpEntity("", headers);
            ResponseEntity<Map> response = restTemplate
                    .exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();

//            HttpResponse<String> httpResponse = httpClient.send(headers, HttpResponse.BodyHandlers.ofString());

            // Tu convert
            String email = userAttributes.get("email").toString();
            String name = userAttributes.get("name").toString();
            User user = new User();
            user.setEmail(email);
            user.setName(name);
            if (client.getClientRegistration().getRegistrationId().toString().equals("facebook")){
                user.setProvider(Provider.FACEBOOK);
            } else {
                user.setProvider(Provider.GOOGLE);
            }
            user.setEnabled(true);
            List<User> users = repo.findAll();
            List<User> emailNew = users.stream().filter(user1 -> !user.getEmail().equals(user1.getEmail())).collect(Collectors.toList());
            if (emailNew.size() != 0) {
                repo.save(user);
            }
            model.addAttribute("name", userAttributes.get("name").toString());
        }

        return "loginsucess";
    }

}
