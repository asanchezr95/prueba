package com.santander.sync.workflow.APIRestFull.Model;

/**
 * The type Git hub content with token.
 */
public class GitHubContentWithToken {

    private GitHubContent gitHubContent;
    private String accessToken;

    /**
     * Gets git hub content.
     *
     * @return the git hub content
     */
    public GitHubContent getGitHubContent() {
        return gitHubContent;
    }

    /**
     * Gets access token.
     *
     * @return the access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * The type Git hub content with token builder.
     */
    public static final class GitHubContentWithTokenBuilder {
        private GitHubContent gitHubContent;
        private String accessToken;

        private GitHubContentWithTokenBuilder() {
        }

        /**
         * A git hub content with token git hub content with token builder.
         *
         * @return the git hub content with token builder
         */
        public static GitHubContentWithTokenBuilder aGitHubContentWithToken() {
            return new GitHubContentWithTokenBuilder();
        }

        /**
         * Git hub content git hub content with token builder.
         *
         * @param gitHubContent the git hub content
         * @return the git hub content with token builder
         */
        public GitHubContentWithTokenBuilder gitHubContent(GitHubContent gitHubContent) {
            this.gitHubContent = gitHubContent;
            return this;
        }

        /**
         * Access token git hub content with token builder.
         *
         * @param accessToken the access token
         * @return the git hub content with token builder
         */
        public GitHubContentWithTokenBuilder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        /**
         * Build git hub content with token.
         *
         * @return the git hub content with token
         */
        public GitHubContentWithToken build() {
            GitHubContentWithToken gitHubContentWithToken = new GitHubContentWithToken();
            gitHubContentWithToken.gitHubContent = this.gitHubContent;
            gitHubContentWithToken.accessToken = this.accessToken;
            return gitHubContentWithToken;
        }
    }
}
