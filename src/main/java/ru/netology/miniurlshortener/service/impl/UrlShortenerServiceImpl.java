package ru.netology.miniurlshortener.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.netology.miniurlshortener.dao.RandomShortenUrlDao;
import ru.netology.miniurlshortener.dao.ShortenUrlDao;
import ru.netology.miniurlshortener.model.ResolveResponse;
import ru.netology.miniurlshortener.model.ShortenDescription;
import ru.netology.miniurlshortener.model.ShortenResponse;
import ru.netology.miniurlshortener.model.ShortenUrl;
import ru.netology.miniurlshortener.service.UrlShortenerService;
import ru.netology.miniurlshortener.util.Base62Util;

import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Value("${service.config.short-url-base:https://base.ru/}")
    private final String baseUrl = "https://base.ru/";

    @Value("${service.config.token-length:7}")
    private final Integer tokenLength = 7;

    private final Logger log = LoggerFactory.getLogger(UrlShortenerServiceImpl.class);

    private final ShortenUrlDao shortenUrlDao;
    private final RandomShortenUrlDao randomShortenUrlDao;

    @Override
    public ShortenResponse shortenUrl(String url) {
        if (!UrlValidator.getInstance().isValid(url)) {
            log.error("Not valid url - {}", url);
            return new ShortenResponse(ShortenDescription.NOT_VALID_URL, url, null, null, null);
        }
        var urlId = UUID.nameUUIDFromBytes(url.getBytes()).toString();
        log.info("Adding new shortened url with id = {} - {}", urlId, url);
        var addResult = shortenUrlDao.addUrl(
                new ShortenUrl(urlId, url, urlIdToShortUrl(urlId), null)
        );
        log.info("Received url from data store");
        return new ShortenResponse(
                addResult.getIsNewCreated() ? ShortenDescription.CREATED : ShortenDescription.ALREADY_EXISTS,
                addResult.getShortenUrl()
        );
    }

    @Override
    public ResolveResponse resolveUrl(String url) {
        log.info("Resolving url from data store - {}", url);
        String urlId;
        try {
            urlId = shortUrlToUrlId(url);
        } catch (Exception ex) {
            log.error("Can't decode short url", ex);
            return new ResolveResponse(ShortenDescription.NOT_FOUND, url, null, null, null);
        }
        var shortenUrl = shortenUrlDao.getById(urlId);
        if (shortenUrl == null) {
            log.error("Url not found - {}", url);
            return new ResolveResponse(ShortenDescription.NOT_FOUND, url, null, null, null);
        }
        log.info("Url resolved");
        return new ResolveResponse(ShortenDescription.OK, shortenUrl);
    }

    @Override
    public ShortenResponse shortenUrlRandomToken(String url) {
        if (!UrlValidator.getInstance().isValid(url)) {
            log.error("Not valid url - {}", url);
            return new ShortenResponse(ShortenDescription.NOT_VALID_URL, url, null, null, null);
        }
        log.info("Adding new shortened url");
        for (long i = 0; i < 10_000_000; i++) {
            String shortToken = RandomStringUtils.randomAlphanumeric(tokenLength);
            var addResult = randomShortenUrlDao.addUrl(url, baseUrl + shortToken);
            if (addResult != null) {
                log.info("Received url from data store");
                return new ShortenResponse(
                        addResult.getIsNewCreated() ? ShortenDescription.CREATED : ShortenDescription.ALREADY_EXISTS,
                        addResult.getShortenUrl()
                );
            }
        }
        return new ShortenResponse(ShortenDescription.SOMETHING_WENT_WRONG, url, null, null, null);
    }

    @Override
    public ResolveResponse resolveUrlRandomToken(String url) {
        var shortenUrl = randomShortenUrlDao.getByShortUrl(url);
        if (shortenUrl == null) {
            log.error("Url not found - {}", url);
            return new ResolveResponse(ShortenDescription.NOT_FOUND, url, null, null, null);
        }
        log.info("Url resolved");
        return new ResolveResponse(ShortenDescription.OK, shortenUrl);
    }

    private String urlIdToShortUrl(String urlId) {
        return baseUrl + Base62Util.encodeBigDecimal(
                new BigInteger(urlId.replaceAll("-", ""), 16)
        );
    }

    private String shortUrlToUrlId(String url) throws URISyntaxException {
        String path = new URI(url).getPath();
        String shortUrl = path.substring(path.lastIndexOf('/') + 1);
        String digitsUrlRepresentation = Base62Util.decodeString(shortUrl).toString(16);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32 - digitsUrlRepresentation.length(); i++) {
            sb.insert(0, '0');
        }
        int startZerosNum = sb.length();
        for (int i = 0; i < digitsUrlRepresentation.length(); i++) {
            sb.append(digitsUrlRepresentation.charAt(i));
            int currentSbIndex = i + startZerosNum;
            if (currentSbIndex == 7 || currentSbIndex == 11 || currentSbIndex == 15 || currentSbIndex == 19) {
                sb.append('-');
            }
        }
        return sb.toString();
    }
}
