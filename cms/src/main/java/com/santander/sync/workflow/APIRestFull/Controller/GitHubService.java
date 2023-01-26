package com.santander.sync.workflow.APIRestFull.Controller;

import com.santander.sync.workflow.APIRestFull.Model.GitHubAppAccessToken;
import com.santander.sync.workflow.APIRestFull.Model.GitHubAppInstallation;
import com.santander.sync.workflow.APIRestFull.Model.GitHubContent;
import com.santander.sync.workflow.APIRestFull.Model.GitHubContentWithToken;
import com.santander.sync.workflow.APIRestFull.View.GitHubClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.ws.rs.WebApplicationException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Git hub service.
 */
@Service
public class GitHubService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubService.class);

    private final GitHubJWTService gitHubJWTService;
    private final GitHubClient     gitHubClient;

    private static final String ERROR_FETCHING_YAML_API = "Couldn't fetch YAML file for API: '";


    /**
     * Instantiates a new Git hub service.
     *
     */
    public GitHubService(@Autowired final GitHubClient gitHubClient,
                         @Autowired final GitHubJWTService gitHubJWTService) {
        this.gitHubClient = gitHubClient;
        this.gitHubJWTService = gitHubJWTService;
    }

    /**
     * Gets git hub content.
     *
     * @param organization the organization
     * @param repository   the repository
     * @param path         the path
     * @param branch       the branch
     * @return the git hub content
     */
    public
    GitHubContentWithToken getGitHubContent (  final String organization, final String repository,
                                              final String path, final String branch ) {

        String githubJWT = generateGithubAppJWT();
        LOGGER.info("GitHub JWT obtained");
        String bearerToken = this.credentialsToBearerToken(githubJWT);

        final GitHubContentWithToken gitHubContentWithToken;

        try {

            List< GitHubAppInstallation > gitHubAppInstallationsList = this.gitHubClient.getAppInstallations ( bearerToken );

            gitHubAppInstallationsList = gitHubAppInstallationsList.stream()
                    .filter(gitHubAppInstallation ->
                                    gitHubAppInstallation.getAccount().getLogin().equals(organization))
                    .collect(Collectors.toList());

            if (gitHubAppInstallationsList.isEmpty()) {

                throw new WebApplicationException ( "Couldn't fetch YAML file for API");
            }

            final int installationId = gitHubAppInstallationsList.get(0).getId();

            GitHubAppAccessToken gitHubAppAccessToken = this.gitHubClient.getAppInstallationAccessToken (
                    bearerToken, installationId );

            final GitHubContent gitHubContent = this.gitHubClient.getRepositoryContent (
                    this.credentialsToBearerToken(gitHubAppAccessToken.getToken()),
                    organization, repository, path, branch );

            gitHubContentWithToken = GitHubContentWithToken.GitHubContentWithTokenBuilder.aGitHubContentWithToken()
                    .gitHubContent(gitHubContent)
                    .accessToken(this.credentialsToBearerToken(gitHubAppAccessToken.getToken()))
                    .build();

        } catch (Exception ex) {
            /**   if (ex instanceof FeignException.FeignServerException) {
             throw new WebApplicationException (ERROR_FETCHING_YAML_API + apiName +
             "'. Error retrieving YAML via GitHub App. Github Server Error");
             }**/
            throw new WebApplicationException(ERROR_FETCHING_YAML_API +
                                              "'. Error retrieving YAML via GitHub App");
        }

        return gitHubContentWithToken;

    }

    private String generateGithubAppJWT() {
        try {
            return this.gitHubJWTService.generateJWT();
        } catch (Exception ex) {
            throw new WebApplicationException(ERROR_FETCHING_YAML_API +
                                              "'. Error generating Github JWT");
        }
    }

    private String credentialsToBearerToken(final String accessToken) {
        return String.format("Bearer %s", accessToken);
    }
}