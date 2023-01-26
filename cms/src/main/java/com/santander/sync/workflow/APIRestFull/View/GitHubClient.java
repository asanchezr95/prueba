package com.santander.sync.workflow.APIRestFull.View;

import com.santander.sync.workflow.APIRestFull.Model.GitHubAppAccessToken;
import com.santander.sync.workflow.APIRestFull.Model.GitHubAppInstallation;
import com.santander.sync.workflow.APIRestFull.Model.GitHubContent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * The interface Git hub client.
 */
@FeignClient (name = "GitHubClient", url = "https://api.github.com")
public interface GitHubClient {

    /**
     * Gets app installations.
     *
     * @param bearerToken the bearer token
     * @return the app installations
     */
    @GetMapping(path = "/app/installations")
    List< GitHubAppInstallation > getAppInstallations ( @RequestHeader("Authorization") String bearerToken );

    /**
     * Gets app installation access token.
     *
     * @param bearerToken    the bearer token
     * @param installationId the installation id
     * @return the app installation access token
     */
    @PostMapping(path = "/app/installations/{installationId}/access_tokens")
    GitHubAppAccessToken getAppInstallationAccessToken ( @RequestHeader("Authorization") String bearerToken,
                                                         @PathVariable("installationId") final int installationId );


    /**
     * Gets repository content.
     *
     * @param bearerAccessToken the bearer access token
     * @param organization      the organization
     * @param repository        the repository
     * @param path              the path
     * @param ref               the ref
     * @return the repository content
     */
    @GetMapping(path = "/repos/{organization}/{repository}/contents/{path}")
    GitHubContent getRepositoryContent ( @RequestHeader("Authorization") String bearerAccessToken,
                                         @PathVariable("organization") final String organization,
                                         @PathVariable("repository") final String repository,
                                         @PathVariable("path") final String path,
                                         @RequestParam(value = "ref") final String ref );
}