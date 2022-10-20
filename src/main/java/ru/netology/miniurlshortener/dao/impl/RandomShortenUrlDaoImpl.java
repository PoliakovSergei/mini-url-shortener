package ru.netology.miniurlshortener.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.netology.miniurlshortener.dao.RandomShortenUrlDao;
import ru.netology.miniurlshortener.model.AddShortenUrlResult;
import ru.netology.miniurlshortener.model.ShortenUrl;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RandomShortenUrlDaoImpl implements RandomShortenUrlDao {
    private final Logger log = LoggerFactory.getLogger(ShortenUrlDaoImpl.class);

    private final Map<String, ShortenUrl> shortUrlToObjectMap = new HashMap<>();
    private final Map<String, ShortenUrl> longUrlToObjectMap = new HashMap<>();

    @Override
    public ShortenUrl getByShortUrl(String url) {
        return shortUrlToObjectMap.get(url);
    }

    @Override
    public AddShortenUrlResult addUrl(String url, String token) {
        log.info("Trying to add new shortened url");
        var result = longUrlToObjectMap.get(url);
        if (result != null) {
            log.warn("Url already exists");
            return new AddShortenUrlResult(result, false);
        }
        if (shortUrlToObjectMap.get(token) == null) {
            synchronized (this) {
                if (shortUrlToObjectMap.get(token) == null) {
                    result = longUrlToObjectMap.get(url);
                    if (result != null) {
                        log.warn("Url already exists");
                        return new AddShortenUrlResult(result, false);
                    }
                    var shortenUrl = new ShortenUrl(
                            UUID.randomUUID().toString(),
                            url,
                            token,
                            System.currentTimeMillis()
                    );
                    longUrlToObjectMap.put(url, shortenUrl);
                    shortUrlToObjectMap.put(token, shortenUrl);
                    return new AddShortenUrlResult(shortenUrl, true);
                }
            }
        }
        return null;
    }
}
