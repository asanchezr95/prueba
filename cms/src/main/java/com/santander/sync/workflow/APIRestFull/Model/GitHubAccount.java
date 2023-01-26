package com.santander.sync.workflow.APIRestFull.Model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * The type Git hub account.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GitHubAccount {

    private String login;
    private int id;
    private String type;
    private String url;
    private String htmlUrl;

    /**
     * Gets login.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets html url.
     *
     * @return the html url
     */
    public String getHtmlUrl() {
        return htmlUrl;
    }

    /**
     * The type Git hub account builder.
     */
    public static final class GitHubAccountBuilder {
        private String login;
        private int id;
        private String type;
        private String url;
        private String htmlUrl;

        private GitHubAccountBuilder() {
        }

        /**
         * A git hub account git hub account builder.
         *
         * @return the git hub account builder
         */
        public static GitHubAccountBuilder aGitHubAccount() {
            return new GitHubAccountBuilder();
        }

        /**
         * Login git hub account builder.
         *
         * @param login the login
         * @return the git hub account builder
         */
        public GitHubAccountBuilder login(String login) {
            this.login = login;
            return this;
        }

        /**
         * Id git hub account builder.
         *
         * @param id the id
         * @return the git hub account builder
         */
        public GitHubAccountBuilder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * Type git hub account builder.
         *
         * @param type the type
         * @return the git hub account builder
         */
        public GitHubAccountBuilder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * Url git hub account builder.
         *
         * @param url the url
         * @return the git hub account builder
         */
        public GitHubAccountBuilder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Html url git hub account builder.
         *
         * @param htmlUrl the html url
         * @return the git hub account builder
         */
        public GitHubAccountBuilder htmlUrl(String htmlUrl) {
            this.htmlUrl = htmlUrl;
            return this;
        }

        /**
         * Build git hub account.
         *
         * @return the git hub account
         */
        public GitHubAccount build() {
            GitHubAccount gitHubAccount = new GitHubAccount();
            gitHubAccount.login = this.login;
            gitHubAccount.url = this.url;
            gitHubAccount.id = this.id;
            gitHubAccount.htmlUrl = this.htmlUrl;
            gitHubAccount.type = this.type;
            return gitHubAccount;
        }
    }
}
