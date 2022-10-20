package ru.netology.miniurlshortener.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.netology.miniurlshortener.dao.RandomShortenUrlDao;
import ru.netology.miniurlshortener.dao.ShortenUrlDao;
import ru.netology.miniurlshortener.model.AddShortenUrlResult;
import ru.netology.miniurlshortener.model.ShortenDescription;
import ru.netology.miniurlshortener.model.ShortenUrl;
import ru.netology.miniurlshortener.service.impl.UrlShortenerServiceImpl;

import java.util.UUID;

public class UrlShortenerServiceTest {

    private final ShortenUrlDao shortenUrlDao = Mockito.mock(ShortenUrlDao.class);
    private final RandomShortenUrlDao randomShortenUrlDao = Mockito.mock(RandomShortenUrlDao.class);

    private final UrlShortenerServiceImpl urlShortenerService = new UrlShortenerServiceImpl(
            shortenUrlDao,
            randomShortenUrlDao
    );

    @Test
    public void shortenUrlShouldReturnNotValidCodeForNotValidUrl() {
        // given
        var notValidUrl = "hlg/t.uuu";
        // when
        var actual = urlShortenerService.shortenUrl(notValidUrl);
        // then
        Assertions.assertEquals(ShortenDescription.NOT_VALID_URL, actual.getDescription());
        Assertions.assertEquals(notValidUrl, actual.getRequestUrl());
        Assertions.assertNull(actual.getShortenUrl());
        Assertions.assertNull(actual.getUrlId());

    }

    @Test
    public void shortenUrlShouldCreateNew() {
        // given
        var validUrl = "http://test.com";
        Mockito.when(shortenUrlDao.addUrl(Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(i.getArgument(0), true));
        // when
        var actual = urlShortenerService.shortenUrl(validUrl);
        // then
        Assertions.assertEquals(ShortenDescription.CREATED, actual.getDescription());
        Assertions.assertEquals(validUrl, actual.getRequestUrl());

    }

    @Test
    public void shortenUrlShouldTellThatAboutAlreadyExisted() {
        // given
        var validUrl = "http://test.com";
        Mockito.when(shortenUrlDao.addUrl(Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(i.getArgument(0), false));
        // when
        var actual = urlShortenerService.shortenUrl(validUrl);
        // then
        Assertions.assertEquals(ShortenDescription.ALREADY_EXISTS, actual.getDescription());
        Assertions.assertEquals(validUrl, actual.getRequestUrl());
    }

    @Test
    public void shortenUrlMultipleExecutionShouldLeadToSameResult() {
        // given
        var validUrl = "http://test.com";
        Mockito.when(shortenUrlDao.addUrl(Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(i.getArgument(0), false));
        // when
        var actual = urlShortenerService.shortenUrl(validUrl);
        var actual1 = urlShortenerService.shortenUrl(validUrl);
        // then
        Assertions.assertEquals(ShortenDescription.ALREADY_EXISTS, actual.getDescription());
        Assertions.assertEquals(ShortenDescription.ALREADY_EXISTS, actual1.getDescription());
        Assertions.assertEquals(actual.getUrlId(), actual1.getUrlId());
        Assertions.assertEquals(actual.getCreatedAt(), actual1.getCreatedAt());
        Assertions.assertEquals(actual.getRequestUrl(), actual1.getRequestUrl());
        Assertions.assertEquals(actual.getShortenUrl(), actual1.getShortenUrl());
    }

    @Test
    public void resolveUrlShouldLeadToNotFoundDescriptionWithInvalidInputString() {
        // given
        var invalidShort = "ZGJiNzIwZDctODhmZi0zZWIwLWFmYjctNTcyMTA0MDcyjRh";
        // when
        var actual = urlShortenerService.resolveUrl(invalidShort);
        // then
        Assertions.assertEquals(ShortenDescription.NOT_FOUND, actual.getDescription());
        Assertions.assertEquals(invalidShort, actual.getRequestUrl());
        Assertions.assertNull(actual.getResolvedUrl());
        Assertions.assertNull(actual.getUrlId());
    }

    @Test
    public void resolveUrlShouldLeadToNotFoundDescriptionWhenNoUrlFounded() {
        // given
        var invalidShort = "ZGJiNzIwZDctODhmZi0zZWIwLWFmYjctNTcyMTA0MDcyZjRh";
        // when
        var actual = urlShortenerService.resolveUrl(invalidShort);
        // then
        Assertions.assertEquals(ShortenDescription.NOT_FOUND, actual.getDescription());
        Assertions.assertEquals(invalidShort, actual.getRequestUrl());
        Assertions.assertNull(actual.getResolvedUrl());
        Assertions.assertNull(actual.getUrlId());
    }

    @Test
    public void resolveUrlShouldReturnOkWhenItFoundUrl() {
        // given
        var validUrl = "http://test.com";
        Mockito.when(shortenUrlDao.addUrl(Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(i.getArgument(0), false));
        var createdUrl = urlShortenerService.shortenUrl(validUrl);
        Mockito.when(shortenUrlDao.getById(createdUrl.getUrlId()))
                .thenReturn(
                        new ShortenUrl(
                                createdUrl.getUrlId(),
                                createdUrl.getRequestUrl(),
                                createdUrl.getShortenUrl(),
                                createdUrl.getCreatedAt()
                        )
                );
        // when
        var actual = urlShortenerService.resolveUrl(createdUrl.getShortenUrl());
        // then
        Assertions.assertEquals(ShortenDescription.OK, actual.getDescription());
        Assertions.assertEquals(createdUrl.getShortenUrl(), actual.getRequestUrl());
        Assertions.assertEquals(createdUrl.getRequestUrl(), actual.getResolvedUrl());
        Assertions.assertEquals(createdUrl.getUrlId(), actual.getUrlId());
    }

    @Test
    public void shortenUrlRandomShouldReturnNotValidCodeForNotValidUrl() {
        // given
        var notValidUrl = "hlg/t.uuu";
        // when
        var actual = urlShortenerService.shortenUrlRandomToken(notValidUrl);
        // then
        Assertions.assertEquals(ShortenDescription.NOT_VALID_URL, actual.getDescription());
        Assertions.assertEquals(notValidUrl, actual.getRequestUrl());
        Assertions.assertNull(actual.getShortenUrl());
        Assertions.assertNull(actual.getUrlId());

    }

    @Test
    public void shortenUrlRandomShouldCreateNew() {
        // given
        var validUrl = "http://test.com";
        var uuid = UUID.randomUUID().toString();
        var createdAt = System.currentTimeMillis();
        Mockito.when(randomShortenUrlDao.addUrl(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(
                        new ShortenUrl(uuid, i.getArgument(0), i.getArgument(1), createdAt),
                        true
                ));
        // when
        var actual = urlShortenerService.shortenUrlRandomToken(validUrl);
        // then
        Assertions.assertEquals(ShortenDescription.CREATED, actual.getDescription());
        Assertions.assertEquals(validUrl, actual.getRequestUrl());

    }

    @Test
    public void shortenUrlRandomShouldTellThatAboutAlreadyExisted() {
        // given
        var validUrl = "http://test.com";
        var uuid = UUID.randomUUID().toString();
        var createdAt = System.currentTimeMillis();
        Mockito.when(randomShortenUrlDao.addUrl(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(
                        new ShortenUrl(uuid, i.getArgument(0), i.getArgument(1), createdAt),
                        false
                ));
        // when
        var actual = urlShortenerService.shortenUrlRandomToken(validUrl);
        // then
        Assertions.assertEquals(ShortenDescription.ALREADY_EXISTS, actual.getDescription());
        Assertions.assertEquals(validUrl, actual.getRequestUrl());
    }

    @Test
    public void resolveUrlRandomShouldLeadToNotFoundDescriptionWithInvalidInputString() {
        // given
        var invalidShort = "ZGJiNzIwZDctODhmZi0zZWIwLWFmYjctNTcyMTA0MDcyjRh";
        // when
        var actual = urlShortenerService.resolveUrlRandomToken(invalidShort);
        // then
        Assertions.assertEquals(ShortenDescription.NOT_FOUND, actual.getDescription());
        Assertions.assertEquals(invalidShort, actual.getRequestUrl());
        Assertions.assertNull(actual.getResolvedUrl());
        Assertions.assertNull(actual.getUrlId());
    }

    @Test
    public void resolveUrlRandomShouldLeadToNotFoundDescriptionWhenNoUrlFounded() {
        // given
        var invalidShort = "ZGJiNzIwZDctODhmZi0zZWIwLWFmYjctNTcyMTA0MDcyZjRh";
        // when
        var actual = urlShortenerService.resolveUrlRandomToken(invalidShort);
        // then
        Assertions.assertEquals(ShortenDescription.NOT_FOUND, actual.getDescription());
        Assertions.assertEquals(invalidShort, actual.getRequestUrl());
        Assertions.assertNull(actual.getResolvedUrl());
        Assertions.assertNull(actual.getUrlId());
    }

    @Test
    public void resolveUrlRandomShouldReturnOkWhenItFoundUrl() {
        // given
        var validUrl = "http://test.com";
        var uuid = UUID.randomUUID().toString();
        var createdAt = System.currentTimeMillis();
        Mockito.when(randomShortenUrlDao.addUrl(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> new AddShortenUrlResult(
                        new ShortenUrl(uuid, i.getArgument(0), i.getArgument(1), createdAt),
                        false
                ));
        var createdUrl = urlShortenerService.shortenUrlRandomToken(validUrl);
        Mockito.when(randomShortenUrlDao.getByShortUrl(createdUrl.getShortenUrl()))
                .thenReturn(
                        new ShortenUrl(
                                createdUrl.getUrlId(),
                                createdUrl.getRequestUrl(),
                                createdUrl.getShortenUrl(),
                                createdUrl.getCreatedAt()
                        )
                );
        // when
        var actual = urlShortenerService.resolveUrlRandomToken(createdUrl.getShortenUrl());
        // then
        Assertions.assertEquals(ShortenDescription.OK, actual.getDescription());
        Assertions.assertEquals(createdUrl.getShortenUrl(), actual.getRequestUrl());
        Assertions.assertEquals(createdUrl.getRequestUrl(), actual.getResolvedUrl());
        Assertions.assertEquals(createdUrl.getUrlId(), actual.getUrlId());
    }

}
