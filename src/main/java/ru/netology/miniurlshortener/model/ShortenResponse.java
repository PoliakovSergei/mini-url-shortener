package ru.netology.miniurlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenResponse {
    private ShortenDescription description;
    private String requestUrl;
    private String shortenUrl;
    private String urlId;
    private Long createdAt;

    public ShortenResponse(ShortenDescription description, ShortenUrl shortenUrl) {
        this.description = description;
        this.requestUrl = shortenUrl.getLongUrl();
        this.shortenUrl = shortenUrl.getShortenUrl();
        this.urlId = shortenUrl.getUrlId();
        this.createdAt = shortenUrl.getCreatedAt();
    }
}
