package com.nguyenduc.config;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

public class OAuth2CustomFilter extends OAuth2LoginAuthenticationFilter {
    public OAuth2CustomFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        super(clientRegistrationRepository, authorizedClientService);
    }

    public OAuth2CustomFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService, String filterProcessesUrl) {
        super(clientRegistrationRepository, authorizedClientService, filterProcessesUrl);
    }

    public OAuth2CustomFilter(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientRepository authorizedClientRepository, String filterProcessesUrl) {
        super(clientRegistrationRepository, authorizedClientRepository, filterProcessesUrl);
    }
}
