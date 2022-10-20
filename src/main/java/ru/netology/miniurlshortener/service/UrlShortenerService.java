package ru.netology.miniurlshortener.service;

import ru.netology.miniurlshortener.model.ResolveResponse;
import ru.netology.miniurlshortener.model.ShortenResponse;

public interface UrlShortenerService {
    ShortenResponse shortenUrl(String url);
    ResolveResponse resolveUrl(String url);

    ShortenResponse shortenUrlRandomToken(String url);
    ResolveResponse resolveUrlRandomToken(String url);
}
