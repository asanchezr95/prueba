package com.santander.sync.workflow.APIRestFull.Model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * The type Git hub app access token.
 */
@JsonNaming ( PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GitHubAppAccessToken {

    private String token;
    private String expiresAt;

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets expires at.
     *
     * @return the expires at
     */
    public String getExpiresAt() {
        return expiresAt;
    }

    /**
     * The type Git hub app access token builder.
     */
    public static final class GitHubAppAccessTokenBuilder {
        private String token;
        private String expiresAt;

        private GitHubAppAccessTokenBuilder() {
        }

        /**
         * A git hub app access token git hub app access token builder.
         *
         * @return the git hub app access token builder
         */
        public static GitHubAppAccessTokenBuilder aGitHubAppAccessToken() {
            return new GitHubAppAccessTokenBuilder();
        }

        /**
         * Token git hub app access token builder.
         *
         * @param token the token
         * @return the git hub app access token builder
         */
        public GitHubAppAccessTokenBuilder token(String token) {
            this.token = token;
            return this;
        }

        /**
         * Expires at git hub app access token builder.
         *
         * @param expiresAt the expires at
         * @return the git hub app access token builder
         */
        public GitHubAppAccessTokenBuilder expiresAt(String expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        /**
         * Build git hub app access token.
         *
         * @return the git hub app access token
         */
        public GitHubAppAccessToken build() {
            GitHubAppAccessToken gitHubAppAccessToken = new GitHubAppAccessToken();
            gitHubAppAccessToken.expiresAt = this.expiresAt;
            gitHubAppAccessToken.token = this.token;
            return gitHubAppAccessToken;
        }
    }
}