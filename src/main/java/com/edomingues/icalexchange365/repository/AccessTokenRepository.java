package com.edomingues.icalexchange365.repository;

import com.edomingues.icalexchange365.model.AccessToken;

public interface AccessTokenRepository {
    void save(AccessToken accessToken);

    AccessToken load(String userId);

    void deleteById(String userId);
}
