package ru.netology.miniurlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResolveResponse {
    private ShortenDescription description;
    private String requestUrl;
    private String resolvedUrl;
    private String urlId;
    private Long createdAt;

    public ResolveResponse(ShortenDescription description, ShortenUrl shortenUrl) {
        this.description = description;
        this.requestUrl = shortenUrl.getShortenUrl();
        this.resolvedUrl = shortenUrl.getLongUrl();
        this.urlId = shortenUrl.getUrlId();
        this.createdAt = shortenUrl.getCreatedAt();
    }
}
