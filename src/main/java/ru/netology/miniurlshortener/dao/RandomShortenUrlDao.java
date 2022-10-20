package ru.netology.miniurlshortener.dao;

import ru.netology.miniurlshortener.model.AddShortenUrlResult;
import ru.netology.miniurlshortener.model.ShortenUrl;

public interface RandomShortenUrlDao {
    ShortenUrl getByShortUrl(String url);

    AddShortenUrlResult addUrl(String url, String token);
}
