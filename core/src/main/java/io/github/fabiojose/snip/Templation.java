package io.github.fabiojose.snip;

import io.github.fabiojose.snip.context.Context;
import io.github.fabiojose.snip.context.Placeholders;
import io.github.fabiojose.snip.model.ProjectName;
import io.github.fabiojose.snip.model.ProjectNamespace;
import io.github.fabiojose.snip.model.ProjectVersion;
import io.github.fabiojose.snip.processor.Processor;
import io.github.fabiojose.snip.templation.ConfigurationLoader;
import io.github.fabiojose.snip.templation.ScriptExecutor;
import io.github.fabiojose.snip.templation.TemplationFetcher;
import io.github.fabiojose.snip.templation.TemplationNotFoundException;
import io.github.fabiojose.snip.util.JSONUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;

/**
 * Snip class to process template and generate new projects.
 */
@AllArgsConstructor
public class Templation {

    private static final Logger log = LoggerFactory.getLogger(Templation.class);

    private URI location;
    private Map<String, String> customPlaceholders;

    private Path projectLocation;

    private ProjectName projectName;
    private ProjectVersion projectVersion;
    private ProjectNamespace projectNamespace;

    /**
     * @return Location with brand new generated project
     * @throws UncheckedIOException When there errors related to i/o (e.g. errors to download remote template or errors during the project write to local storage)
     * @throws TemplationNotFoundException When the template is unreachable
     */
    public Path newProject() {

        final var placeholders = Placeholders.builder();
        placeholders.name(projectName.getName());
        placeholders.version(projectVersion.getVersion());
        placeholders.namespace(projectNamespace.getNamespace());

        placeholders.parameters(
            customPlaceholders
                .entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList())
        );

        final var projectDir = this.projectLocation.resolve(this.projectName.getName());
        log.debug("Project will generated in {}", projectDir);

        boolean success = false;
        try {
            final var fetcher = TemplationFetcher.create(this.location);
            log.debug("Templation located at {}", this.location);

            // download or copy
            final var template = fetcher.fetch();

            FileUtils.copyDirectory(template.toFile(), projectDir.toFile());
            log.debug("template copied to project directory at {}", projectDir);

            // load .snip.yml, if any
            var config = ConfigurationLoader.load(projectDir);

            // custom placeholder rules, if any
            config.
                flatMap(c -> JSONUtil.pointer(c).asObject("#/placeholders"))
                .ifPresent(placeholders::rules);

            // remove .git folder (if exists)
            var gitdir = projectDir.resolve(".git");
            FileUtils.deleteQuietly(gitdir.toFile());

            var context = Context.create(placeholders.build(), template, projectDir);

            // process folders parameters
            Processor.forDirectories().process(context);;

            // process file name parameters
            Processor.forFiles().process(context);

            // process file content parameters
            Processor.forContent().process(context);

            // run post script, if any
            config
                .flatMap(c -> JSONUtil.pointer(c).asObject("#/post/script"))
                .ifPresent(script ->
                    ScriptExecutor.create(script, projectDir).execute());

            log.info("New project created at: " + projectDir);
            success = true;

            return projectDir;
        }catch(IOException e) {
            throw new UncheckedIOException(e);
        }finally {
            if(!success) {
                try{
                    FileUtils.forceDelete(projectDir.toFile());
                }catch(IOException e) {
                    log.error("can not delete " + projectDir, e);
                }
            }
        }
    }

    public static TemplationBuilder newBuilder() {
        return new TemplationBuilder();
    }

    public static class TemplationBuilder {

        private TemplationBuilder() {}

        private URI location;

        private Map<String, String> customPlaceholders = new HashMap<>();

        private Path projectLocation;

        private ProjectName projectName;
        private ProjectVersion projectVersion;
        private ProjectNamespace projectNamespace;

        /**
         * The location of template
         */
        public TemplationBuilder withLocation(URI location) {
            this.location = Objects.requireNonNull(location);
            return this;
        }

        public TemplationBuilder withPlaceholder(String name, String value) {
            this.customPlaceholders.put(
                    Objects.requireNonNull(name),
                    Objects.requireNonNull(value)
                );
            return this;
        }

        public TemplationBuilder withPlaceholders(Map<String, String> placeholders) {
            this.customPlaceholders.putAll(placeholders);
            return this;
        }

        public TemplationBuilder withProjectLocation(Path location) {
            this.projectLocation = Objects.requireNonNull(location);

            if (!Files.exists(this.projectLocation)) {
                throw new IllegalArgumentException(
                    "Project location does not exists: " + location
                );
            }
            return this;
        }

        public TemplationBuilder withProjectName(String name) {
            this.projectName = new ProjectName(name);
            return this;
        }

        public TemplationBuilder withProjectVersion(String version) {
            this.projectVersion = new ProjectVersion(version);
            return this;
        }

        public TemplationBuilder withProjectNamespace(String namespace) {
            this.projectNamespace = new ProjectNamespace(namespace);
            return this;
        }

        public Templation build() {
            return new Templation(
                location,
                customPlaceholders,
                projectLocation,
                projectName,
                projectVersion,
                projectNamespace
            );
        }
    }
}
