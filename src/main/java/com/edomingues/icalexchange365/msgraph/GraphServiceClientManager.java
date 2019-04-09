package com.edomingues.icalexchange365.msgraph;


import com.edomingues.icalexchange365.service.AuthenticationService;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.core.DefaultClientConfig;
import com.microsoft.graph.core.IClientConfig;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
public class GraphServiceClientManager {

    @Autowired
    private AuthenticationService authenticationService;

    public synchronized IGraphServiceClient getGraphServiceClient(String userId) {
        return graphServiceClient(userId);
    }

    private IGraphServiceClient graphServiceClient(String userId) {
        IClientConfig clientConfig= DefaultClientConfig.createWithAuthenticationProvider(
                request -> {
                    try {
                        request.addHeader("Authorization", "Bearer " + authenticationService.getAccessToken(userId));
                    } catch (ClientException | NullPointerException | InterruptedException | ExecutionException | IOException e) {
                        e.printStackTrace();
                    }
                });
        clientConfig.getLogger().setLoggingLevel(LoggerLevel.ERROR);
        return GraphServiceClient.fromConfig(clientConfig);
    }

}
