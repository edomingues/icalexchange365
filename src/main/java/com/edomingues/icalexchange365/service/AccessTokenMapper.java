package com.edomingues.icalexchange365.service;

import com.edomingues.icalexchange365.model.AccessToken;
import com.github.scribejava.core.model.OAuth2AccessToken;

import java.util.Date;
import java.time.Instant;

class AccessTokenMapper {

    static AccessToken createAccessTokenFromOAuth2(String userId, OAuth2AccessToken oAuth2AccessToken) {
        Date expirationTime = Date.from(Instant.now().plusSeconds(oAuth2AccessToken.getExpiresIn()));
        return new AccessToken(userId, oAuth2AccessToken.getAccessToken(), expirationTime, oAuth2AccessToken.getRefreshToken());
    }

}
