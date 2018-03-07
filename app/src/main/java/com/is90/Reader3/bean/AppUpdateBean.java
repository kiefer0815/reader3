package com.is90.Reader3.bean;

/**
 * Created by kiefer on 16/9/21.
 */
public class AppUpdateBean {
        private int appVersion;
        private String apkUrl;
        private String updateDesc;
        private boolean force;
        private boolean updateBackground;
        private String channel;
        private boolean cleanCache;
        private String config;
        private String bookUrl;
        private String bookCoverUrl;
        private String gifUrl;
        private String gifCoverUrl;
        private String domain;
        private int adType;

        public int getAdType() {
                return adType;
        }

        public void setAdType(int adType) {
                this.adType = adType;
        }

        public String getDomain() {
                return domain;
        }

        public void setDomain(String domain) {
                this.domain = domain;
        }

        public String getBookUrl() {
                return bookUrl;
        }

        public void setBookUrl(String bookUrl) {
                this.bookUrl = bookUrl;
        }

        public String getBookCoverUrl() {
                return bookCoverUrl;
        }

        public void setBookCoverUrl(String bookCoverUrl) {
                this.bookCoverUrl = bookCoverUrl;
        }

        public String getGifUrl() {
                return gifUrl;
        }

        public void setGifUrl(String gifUrl) {
                this.gifUrl = gifUrl;
        }

        public String getGifCoverUrl() {
                return gifCoverUrl;
        }

        public void setGifCoverUrl(String gifCoverUrl) {
                this.gifCoverUrl = gifCoverUrl;
        }

        public String getConfig() {
                return config;
        }

        public void setConfig(String config) {
                this.config = config;
        }

        public int getAppVersion() {
                return appVersion;
        }

        public void setAppVersion(int appVersion) {
                this.appVersion = appVersion;
        }

        public String getApkUrl() {
                return apkUrl;
        }

        public void setApkUrl(String apkUrl) {
                this.apkUrl = apkUrl;
        }

        public String getUpdateDesc() {
                return updateDesc;
        }

        public void setUpdateDesc(String updateDesc) {
                this.updateDesc = updateDesc;
        }

        public boolean isForce() {
                return force;
        }

        public void setForce(boolean force) {
                this.force = force;
        }

        public boolean isUpdateBackground() {
                return updateBackground;
        }

        public void setUpdateBackground(boolean updateBackground) {
                this.updateBackground = updateBackground;
        }

        public String getChannel() {
                return channel;
        }

        public void setChannel(String channel) {
                this.channel = channel;
        }

        public boolean isCleanCache() {
                return cleanCache;
        }

        public void setCleanCache(boolean cleanCache) {
                this.cleanCache = cleanCache;
        }
}
