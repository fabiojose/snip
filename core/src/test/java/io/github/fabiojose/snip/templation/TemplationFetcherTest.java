package io.github.fabiojose.snip.templation;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import io.github.fabiojose.snip.support.ByPassTLSValidation;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@WireMockTest(httpPort = 10008, httpsEnabled = true, httpsPort = 10443)
public class TemplationFetcherTest {

    @Test
    void should_fetch_remote_http_url() throws IOException, URISyntaxException {

        // setup
        var zipFile = Paths.get("src/test/resources/java11-quarkus-maven.zip");

        stubFor(head(UrlPattern.ANY).willReturn(ok()));

        stubFor(get("/http/fetch")
            .willReturn(ok()
                .withBody(Files.readAllBytes(zipFile))));

        var fetcher = TemplationFetcher.create(URI.create("http://localhost:10008/http/fetch"));

        // act
        var actual = fetcher.fetch();

        // assert
        assertTrue(Files.exists(actual));

        // cleanup
        FileUtils.deleteDirectory(actual.toFile());
    }

    @Test
    void should_fetch_remote_https_url() throws IOException, URISyntaxException {

        // setup
        ByPassTLSValidation.setup();

        var zipFile = Paths.get("src/test/resources/java11-quarkus-maven.zip");

        stubFor(head(UrlPattern.ANY).willReturn(ok()));

        stubFor(get("/http/fetch")
            .willReturn(ok()
                .withBody(Files.readAllBytes(zipFile))));

        var fetcher = TemplationFetcher.create(URI.create("https://localhost:10443/http/fetch"));

        // act
        var actual = fetcher.fetch();

        // assert
        assertTrue(Files.exists(actual));

        // cleanup
        FileUtils.deleteDirectory(actual.toFile());
    }

    @Test
    void should_fetch_remote_github() {

        // TODO: Use mockito to mock github url

    }
}
