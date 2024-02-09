package in.toralabs.library.util;

import java.util.List;

public class MarketingModel {
    private String title;
    private String mediaType;
    private String mediaFileName;
    private String templateName;
    private String language;
    private List<String> hintsList;
    private boolean hasContactButtonPresent;
    private boolean hasWebButtonPresent;
    private boolean hasFooterPresent;
    private String contactButtonText;
    private String webButtonText;
    private String footerText;
    private String description;

    private String marketingUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<String> getHintsList() {
        return hintsList;
    }

    public void setHintsList(List<String> hintsList) {
        this.hintsList = hintsList;
    }

    public String getMediaFileName() {
        return mediaFileName;
    }

    public void setMediaFileName(String mediaFileName) {
        this.mediaFileName = mediaFileName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getContactButtonText() {
        return contactButtonText;
    }

    public void setContactButtonText(String contactButtonText) {
        this.contactButtonText = contactButtonText;
    }

    public String getWebButtonText() {
        return webButtonText;
    }

    public void setWebButtonText(String webButtonText) {
        this.webButtonText = webButtonText;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasContactButtonPresent() {
        return hasContactButtonPresent;
    }

    public void setHasContactButtonPresent(boolean hasContactButtonPresent) {
        this.hasContactButtonPresent = hasContactButtonPresent;
    }

    public boolean isHasWebButtonPresent() {
        return hasWebButtonPresent;
    }

    public void setHasWebButtonPresent(boolean hasWebButtonPresent) {
        this.hasWebButtonPresent = hasWebButtonPresent;
    }

    public boolean isHasFooterPresent() {
        return hasFooterPresent;
    }

    public void setHasFooterPresent(boolean hasFooterPresent) {
        this.hasFooterPresent = hasFooterPresent;
    }

    public String getMarketingUrl() {
        return marketingUrl;
    }

    public void setMarketingUrl(String marketingUrl) {
        this.marketingUrl = marketingUrl;
    }
}
