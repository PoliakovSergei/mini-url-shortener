package ru.netology.miniurlshortener.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddShortenUrlResult {
    private final ShortenUrl shortenUrl;
    private final Boolean isNewCreated;
}
