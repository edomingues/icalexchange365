package com.edomingues.icalexchange365.repository;

import com.edomingues.icalexchange365.model.AuthorizationState;

public interface AuthorizationStateRepository {

    public AuthorizationState load(String state);

    public void save(AuthorizationState authorizationState);
}
