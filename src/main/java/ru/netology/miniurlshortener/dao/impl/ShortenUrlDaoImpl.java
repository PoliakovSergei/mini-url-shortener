package ru.netology.miniurlshortener.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.netology.miniurlshortener.dao.ShortenUrlDao;
import ru.netology.miniurlshortener.model.AddShortenUrlResult;
import ru.netology.miniurlshortener.model.ShortenUrl;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShortenUrlDaoImpl implements ShortenUrlDao {

    private final Logger log = LoggerFactory.getLogger(ShortenUrlDaoImpl.class);

    private final Map<String, ShortenUrl> urlMap = new HashMap<>();

    @Override
    public ShortenUrl getById(String urlId) {
        log.info("Getting shortened url from map by id - {}", urlId);
        return urlMap.get(urlId);
    }

    @Override
    public AddShortenUrlResult addUrl(ShortenUrl shortenUrl) {
        log.info("Trying to add new shortened url");
        var result = urlMap.get(shortenUrl.getUrlId());
        if (result != null) {
            log.warn("Url with id - {} already exists", shortenUrl.getUrlId());
            return new AddShortenUrlResult(result, false);
        }
        synchronized (this) {
            result = urlMap.get(shortenUrl.getUrlId());
            if (result != null) {
                log.warn("Url with id - {} already exists", shortenUrl.getUrlId());
                return new AddShortenUrlResult(result, false);
            }
            shortenUrl.setCreatedAt(System.currentTimeMillis());
            urlMap.put(shortenUrl.getUrlId(), shortenUrl);
            log.info("New shortened url added");
            return new AddShortenUrlResult(shortenUrl, true);
        }
    }
}
