package ru.netology.miniurlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShortenUrl {
    private String urlId;
    private String longUrl;
    private String shortenUrl;
    private Long createdAt;
}
