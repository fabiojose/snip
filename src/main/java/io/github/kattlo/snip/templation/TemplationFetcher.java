package io.github.kattlo.snip.templation;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;

/**
 * @author fabiojose
 */
@Slf4j
public class TemplationFetcher {

    private static final String TMP_DIR = "/tmp/snip";

    private static final Pattern LOCAL_TEMPLATE = 
        Pattern.compile("^file:/.+$");

    private URI templation;
    private Path localhost;
    private URL remote;

    private TemplationFetcher() {}
    
    public Path fetch() throws IOException, URISyntaxException {

        Path target = null;

        if(null!= this.remote){
            log.debug("downloading the remote templation {}", this.remote);

            this.remote.getFile();
            target = Path.of(TMP_DIR, templation.toString());
            FileUtils.deleteQuietly(target.toFile());
            Files.createDirectories(target);

            var targetFile = Path.of("/tmp", templation.toString().replaceAll("/", "-") + ".zip");

            FileUtils.copyURLToFile(this.remote, targetFile.toFile());
            var zip = new ZipFile(targetFile.toFile());
            zip.extractAll(target.toString());

            var realname = zip.getFileHeaders().iterator().next().getFileName();
            target = Path.of(target.toString(), realname);

            log.debug("templation donwloaded at {}", target);

                
        } else {
        
            target = Path.of(TMP_DIR, this.localhost.toFile().getName());
            Files.createDirectories(target);

            FileUtils.copyDirectory(this.localhost.toFile(), target.toFile(), false);
            log.debug("templation copied to {}", target);
        }

        return target;
    }

    public static TemplationFetcher create(URI templation) throws IOException {
        log.debug("templation from {}" + templation);

        Path localTemplate = null;
        URL remoteTemplate = null;

        if(LOCAL_TEMPLATE.matcher(templation.toString()).matches()){
            localTemplate = new File(templation).toPath();
            if(!Files.exists(localTemplate)){
                throw new TemplationNotFoundException(templation.toString());
            }
            log.debug("using templation from local at: {}", localTemplate);

        } else {
            remoteTemplate = new URL("https://api.github.com/repos/" + templation + "/zipball");
            var https = (HttpsURLConnection)remoteTemplate.openConnection();
            https.setRequestMethod("HEAD");

            if(HttpsURLConnection.HTTP_OK != https.getResponseCode()){
                throw new TemplationNotFoundException(templation.toString());
            }

            log.debug("using template from remote at: {}", remoteTemplate);
        }

        var fetcher = new TemplationFetcher();
        fetcher.templation = templation;
        fetcher.localhost = localTemplate;
        fetcher.remote = remoteTemplate;

        return fetcher;
    }
}
