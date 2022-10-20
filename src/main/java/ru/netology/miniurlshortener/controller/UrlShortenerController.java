package ru.netology.miniurlshortener.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.miniurlshortener.model.ResolveResponse;
import ru.netology.miniurlshortener.model.ShortenDescription;
import ru.netology.miniurlshortener.model.ShortenResponse;
import ru.netology.miniurlshortener.model.UrlRequest;
import ru.netology.miniurlshortener.service.UrlShortenerService;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @PostMapping("/v1/shorten")
    public ResponseEntity<ShortenResponse> shortenUrl(@RequestBody UrlRequest urlRequest) {
        var serviceResult = urlShortenerService.shortenUrl(urlRequest.getUrl());
        return new ResponseEntity<>(
                serviceResult,
                mapDescriptionToCode(serviceResult.getDescription())
        );

    }

    @PostMapping("/v1/resolve")
    public ResponseEntity<ResolveResponse> resolveShortUrl(@RequestBody UrlRequest urlRequest) {
        var serviceResult = urlShortenerService.resolveUrl(urlRequest.getUrl());
        return new ResponseEntity<>(
                serviceResult,
                mapDescriptionToCode(serviceResult.getDescription())
        );
    }

    @PostMapping("/v2/shorten")
    public ResponseEntity<ShortenResponse> shortenUrlRandomToken(@RequestBody UrlRequest urlRequest) {
        var serviceResult = urlShortenerService.shortenUrlRandomToken(urlRequest.getUrl());
        return new ResponseEntity<>(
                serviceResult,
                mapDescriptionToCode(serviceResult.getDescription())
        );

    }

    @PostMapping("/v2/resolve")
    public ResponseEntity<ResolveResponse> resolveUrlRandomToken(@RequestBody UrlRequest urlRequest) {
        var serviceResult = urlShortenerService.resolveUrlRandomToken(urlRequest.getUrl());
        return new ResponseEntity<>(
                serviceResult,
                mapDescriptionToCode(serviceResult.getDescription())
        );
    }

    private HttpStatus mapDescriptionToCode(ShortenDescription description) {
        switch (description) {
            case OK:
            case ALREADY_EXISTS:
                return HttpStatus.OK;
            case CREATED:
                return HttpStatus.CREATED;
            case NOT_VALID_URL:
                return HttpStatus.BAD_REQUEST;
            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;
            default:
                return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
