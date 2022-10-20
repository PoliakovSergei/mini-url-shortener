package ru.netology.miniurlshortener.dao;

import ru.netology.miniurlshortener.model.AddShortenUrlResult;
import ru.netology.miniurlshortener.model.ShortenUrl;

public interface ShortenUrlDao {
    ShortenUrl getById(String urlId);

    AddShortenUrlResult addUrl(ShortenUrl shortenUrl);
}
