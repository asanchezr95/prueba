package com.santander.sync.workflow.APIRestFull.Model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * The type Git hub app installation.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GitHubAppInstallation {

    private int           id;
    private int           appId;
    private GitHubAccount account;

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets app id.
     *
     * @return the app id
     */
    public int getAppId() {
        return appId;
    }

    /**
     * Gets account.
     *
     * @return the account
     */
    public GitHubAccount getAccount() {
        return account;
    }

    /**
     * The type Git hub app installation builder.
     */
    public static final class GitHubAppInstallationBuilder {
        private int id;
        private int appId;
        private GitHubAccount account;

        private GitHubAppInstallationBuilder() {
        }

        /**
         * A git hub app installation git hub app installation builder.
         *
         * @return the git hub app installation builder
         */
        public static GitHubAppInstallationBuilder aGitHubAppInstallation() {
            return new GitHubAppInstallationBuilder();
        }

        /**
         * Id git hub app installation builder.
         *
         * @param id the id
         * @return the git hub app installation builder
         */
        public GitHubAppInstallationBuilder id(int id) {
            this.id = id;
            return this;
        }

        /**
         * App id git hub app installation builder.
         *
         * @param appId the app id
         * @return the git hub app installation builder
         */
        public GitHubAppInstallationBuilder appId(int appId) {
            this.appId = appId;
            return this;
        }

        /**
         * Account git hub app installation builder.
         *
         * @param account the account
         * @return the git hub app installation builder
         */
        public GitHubAppInstallationBuilder account(GitHubAccount account) {
            this.account = account;
            return this;
        }

        /**
         * Build git hub app installation.
         *
         * @return the git hub app installation
         */
        public GitHubAppInstallation build() {
            GitHubAppInstallation gitHubAppInstallation = new GitHubAppInstallation();
            gitHubAppInstallation.id = this.id;
            gitHubAppInstallation.account = this.account;
            gitHubAppInstallation.appId = this.appId;
            return gitHubAppInstallation;
        }
    }
}