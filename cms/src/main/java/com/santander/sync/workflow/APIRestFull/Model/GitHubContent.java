package com.santander.sync.workflow.APIRestFull.Model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * The type Git hub content.
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GitHubContent {

    private String name;
    private String path;
    private String url;
    private String downloadUrl;
    private String encoding;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
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
     * Gets download url.
     *
     * @return the download url
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * Gets encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * The type Git hub content builder.
     */
    public static final class GitHubContentBuilder {
        private String name;
        private String path;
        private String url;
        private String downloadUrl;
        private String encoding;

        private GitHubContentBuilder() {
        }

        /**
         * A git hub content git hub content builder.
         *
         * @return the git hub content builder
         */
        public static GitHubContentBuilder aGitHubContent() {
            return new GitHubContentBuilder();
        }

        /**
         * Name git hub content builder.
         *
         * @param name the name
         * @return the git hub content builder
         */
        public GitHubContentBuilder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Path git hub content builder.
         *
         * @param path the path
         * @return the git hub content builder
         */
        public GitHubContentBuilder path(String path) {
            this.path = path;
            return this;
        }

        /**
         * Url git hub content builder.
         *
         * @param url the url
         * @return the git hub content builder
         */
        public GitHubContentBuilder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * Download url git hub content builder.
         *
         * @param downloadUrl the download url
         * @return the git hub content builder
         */
        public GitHubContentBuilder downloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        /**
         * Encoding git hub content builder.
         *
         * @param encoding the encoding
         * @return the git hub content builder
         */
        public GitHubContentBuilder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * Build git hub content.
         *
         * @return the git hub content
         */
        public GitHubContent build() {
            GitHubContent gitHubContent = new GitHubContent();
            gitHubContent.path = this.path;
            gitHubContent.encoding = this.encoding;
            gitHubContent.downloadUrl = this.downloadUrl;
            gitHubContent.name = this.name;
            gitHubContent.url = this.url;
            return gitHubContent;
        }
    }
}
