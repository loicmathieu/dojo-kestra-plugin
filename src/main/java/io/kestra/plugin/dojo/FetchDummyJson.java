package io.kestra.plugin.dojo;

import io.kestra.core.models.annotations.Plugin;
import io.kestra.core.models.property.Property;
import io.kestra.core.models.tasks.RunnableTask;
import io.kestra.core.models.tasks.Task;
import io.kestra.core.runners.RunContext;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

@SuperBuilder
@ToString
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Schema(
    title = "Fetch a dummy JSON",
    description = "Fetch a dummy JSON from 'https://dummyjson.com"
)
@Plugin(
    examples = {
        @io.kestra.core.models.annotations.Example(
            title = "Simple revert",
            full = true,
            code = """
                """
        )
    }
)
public class FetchDummyJson extends Task implements RunnableTask<FetchDummyJson.Output> {
    private static final String URL_PATTERN = "https://dummyjson.com/%s?limit=%s";

    @NotNull
    @Schema(title = "The type of JSON to fetch")
    public Property<String> jsonType;

    @NotNull
    @Builder.Default
    @Schema(title = "Number of JSON to fetch")
    public Property<Integer> limit = Property.of(10);

    @Override
    public Output run(RunContext runContext) throws Exception {
        String renderedType = runContext.render(jsonType).as(String.class).orElseThrow();
        Integer renderedLimit = runContext.render(limit).as(Integer.class).orElseThrow();
        String url = URL_PATTERN.formatted(renderedType, renderedLimit);
        runContext.logger().info("Fetched dummy JSON from {}", url);

        try (HttpClient client = HttpClient.newHttpClient()) {
            var request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
            var file = runContext.workingDir().createFile(renderedType + ".json");
            Files.write(file, response.getBytes());
            var fileUri = runContext.storage().putFile(file.toFile());
            return Output.builder().uri(fileUri).build();
        }
    }

    @Getter
    @Builder
    public static class Output implements io.kestra.core.models.tasks.Output {
        private URI uri;
    }
}
