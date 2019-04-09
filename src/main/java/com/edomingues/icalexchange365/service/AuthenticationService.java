package com.edomingues.icalexchange365.service;

import com.edomingues.icalexchange365.exception.NoAccessTokenException;
import com.edomingues.icalexchange365.model.AccessToken;
import com.edomingues.icalexchange365.model.AuthorizationState;
import com.edomingues.icalexchange365.repository.AccessTokenRepository;
import com.edomingues.icalexchange365.repository.AuthorizationStateRepository;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class AuthenticationService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Autowired
    private AuthorizationStateRepository authorizationStateRepository;

    private OAuth20Service oAuth20Service;

    @Autowired
    public AuthenticationService(@Value("${oauth.redirect.url}") String oauthRedirectUrl, @Value("${scopes}") String scopes, @Value("${client.id}") String client_id, @Value("${client.secret}") String clientSecret) {
        try (OAuth20Service service = new ServiceBuilder(client_id)
                .callback(oauthRedirectUrl)
                .scope(scopes)
                .apiKey(client_id)
                .apiSecret(clientSecret)
                .debugStream(System.out)
                .debug()
                .build(MicrosoftAzureAD20Api.instance())
        ) {
            oAuth20Service = service;
        } catch (IOException | IllegalArgumentException ex) {
            try {
                throw ex;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAuthorized(String userId) throws InterruptedException, ExecutionException, IOException {
        try {
            return this.getAccessToken(userId) != null;
        } catch(NoAccessTokenException e) {
            return false;
        }
    }

    public String getAuthorizationUrl(String userId) {

        AuthorizationState authorizationState = new AuthorizationState(userId, UUID.randomUUID().toString());

        this.authorizationStateRepository.save(authorizationState);

        return this.oAuth20Service.getAuthorizationUrl(Map.of("state",authorizationState.state));
    }

    public void getAccessTokenFromCode(String userId, String code) throws InterruptedException, ExecutionException, IOException {
        OAuth2AccessToken oAuth2AccessToken = this.oAuth20Service.getAccessToken(code);

        System.out.println("access token expires in: " + oAuth2AccessToken.getExpiresIn());

        accessTokenRepository.save(AccessTokenMapper.createAccessTokenFromOAuth2(userId, oAuth2AccessToken));
    }

    public void getAccessTokenFromCodeAndState(String code, String state) throws InterruptedException, ExecutionException, IOException {
        AuthorizationState authorizationState = this.authorizationStateRepository.load(state);

        if(authorizationState != null) {

            OAuth2AccessToken oAuth2AccessToken = this.oAuth20Service.getAccessToken(code);

            System.out.println("access token expires in: " + oAuth2AccessToken.getExpiresIn());

            accessTokenRepository.save(AccessTokenMapper.createAccessTokenFromOAuth2(authorizationState.userId, oAuth2AccessToken));
        }
        else {
            System.err.println("authorization state not found in repository");
        }

    }

    public String getAccessToken(String userId) throws InterruptedException, ExecutionException, IOException {
            AccessToken accessToken = accessTokenRepository.load(userId);

            if (accessToken == null) {
                System.err.println("no access token in repository");
                throw new NoAccessTokenException("no access token found");
            }

            if (accessToken.isExpired()) {
                System.out.println("access token expired, refreshing it");

                OAuth2AccessToken newToken = this.refreshAccessToken(accessToken);

                System.out.println("access token expires in: " + newToken.getExpiresIn());

                accessToken = AccessTokenMapper.createAccessTokenFromOAuth2(accessToken.userId, newToken);
                accessTokenRepository.save(accessToken);
            }

            return accessToken.accessToken;
    }

    private OAuth2AccessToken refreshAccessToken(AccessToken accessToken) throws InterruptedException, ExecutionException, IOException {
        return this.oAuth20Service.refreshAccessToken(accessToken.refreshToken);
    }

    public void deleteAccessToken(String userId) {
        this.accessTokenRepository.deleteById(userId);
    }

}
