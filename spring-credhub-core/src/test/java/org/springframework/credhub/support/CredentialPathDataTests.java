package org.springframework.credhub.support;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CredentialPathDataTests extends JsonParsingUnitTestsBase {
	@Test
	public void deserializeWithPaths() {
		String json = "{\n" +
				"  \"paths\": [\n" +
				"    {\n" +
				"      \"path\": \"/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director-name/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director-name/deploy1/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director-name/deploy2/\"\n" +
				"    },\n" +
				"    {\n" +
				"      \"path\": \"/director2/\"\n" +
				"    }\n" +
				"  ]\n" +
				"}";

		CredentialPathData paths = parseResponse(json, CredentialPathData.class);

		assertThat(paths.getPaths().size()).isEqualTo(5);
		assertThat(paths.getPaths()).extracting("path")
				.contains("/", "/director-name/", "/director-name/deploy1/", "/director-name/deploy2/", "/director2/");
	}

	@Test
	public void deserializeWithNoPaths() {
		String json = "{\n" +
				"  \"paths\": []" +
				"}";

		CredentialPathData paths = parseResponse(json, CredentialPathData.class);

		assertThat(paths.getPaths().size()).isEqualTo(0);
	}
}