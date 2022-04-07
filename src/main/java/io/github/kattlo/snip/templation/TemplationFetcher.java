package io.github.kattlo.snip.templation;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;

/**
 * @author fabiojose
 */
@Slf4j
public class TemplationFetcher {

    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String SNIP_TMP_DIR = TMP_DIR + FileSystems.getDefault().getSeparator()  + "snip";

    private static final Pattern LOCAL_TEMPLATE =
        Pattern.compile("^file:/.+$");

    private static final Pattern REMOTE_TEMPLATE = Pattern.compile("^https?:/.+$");

    private URI templation;
    private Path localhost;
    private URL remote;

    private TemplationFetcher() {}

    String getGithubAPIBaseURL() {
        return System.getProperty("snip.github.api.baseurl", "https://api.github.com/");
    }

    public Path fetch() throws IOException, URISyntaxException {

        Path target = null;

        if(null!= this.remote){
            log.debug("downloading the remote templation {}", this.remote);

            final var normalizedName = templation.toString().replaceAll("/", "-").replaceAll(":", "-");
            target = Path.of(SNIP_TMP_DIR, normalizedName);
            FileUtils.deleteQuietly(target.toFile());
            Files.createDirectories(target);

            var targetFile = Path.of(TMP_DIR, normalizedName + ".zip");

            FileUtils.copyURLToFile(this.remote, targetFile.toFile());
            var zip = new ZipFile(targetFile.toFile());
            zip.extractAll(target.toString());

            var realname = zip.getFileHeaders().iterator().next().getFileName();
            target = Path.of(target.toString(), realname);

            log.debug("templation donwloaded at {}", target);

        } else {

            target = Path.of(SNIP_TMP_DIR, this.localhost.toFile().getName());
            Files.createDirectories(target);

            FileUtils.copyDirectory(this.localhost.toFile(), target.toFile(), false);
            log.debug("templation copied to {}", target);
        }

        return target;
    }

    public static TemplationFetcher create(URI templation) throws IOException {

        Path localTemplate = null;
        URL remoteTemplate = null;

        if(LOCAL_TEMPLATE.matcher(templation.toString()).matches()){

            localTemplate = new File(templation).toPath();
            if(!Files.exists(localTemplate)){
                throw new TemplationNotFoundException(templation.toString());
            }
            log.debug("using templation from local at: {}", localTemplate);

        } else if(REMOTE_TEMPLATE.matcher(templation.toString()).matches()) {
            remoteTemplate = templation.toURL();
            log.debug("templation from custom URL: {}", remoteTemplate);
        } else {
            remoteTemplate = new URL("https://api.github.com/repos/" + templation + "/zipball");
            log.debug("templation from github: {}", remoteTemplate);
        }

        if(null!= remoteTemplate) {

            var https = (HttpURLConnection)remoteTemplate.openConnection();
            https.setRequestMethod("HEAD");

            if(HttpURLConnection.HTTP_OK != https.getResponseCode()){
                throw new TemplationNotFoundException(templation.toString());
            }

        }

        var fetcher = new TemplationFetcher();
        fetcher.templation = templation;
        fetcher.localhost = localTemplate;
        fetcher.remote = remoteTemplate;

        return fetcher;
    }
}
