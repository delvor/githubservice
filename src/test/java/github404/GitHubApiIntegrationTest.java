package github404;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GitHubApiIntegrationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void shouldFetchNonForkRepositoriesWithBranchesForValidUser() {
		// Given
		String username = "octocat";
		String url = "http://localhost:" + port + "/api/github/users/" + username + "/repos";

		// When
		ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, null, List.class);

		// Then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<?> body = response.getBody();
		assertThat(body).isNotNull().isNotEmpty();

		Map<String, String> firstRepo = (Map<String, String>) body.get(0);
		assertThat(firstRepo).containsKeys("repositoryName", "ownerLogin", "branches");

		Object branchesObj = firstRepo.get("branches");
		assertThat(branchesObj).isInstanceOf(List.class);

		List<Map<String, String>> branches = (List<Map<String, String>>) branchesObj;
		assertThat(branches).isNotEmpty();

		Map<String, String> firstBranch = (Map<String, String>) branches.get(0);
		assertThat(firstBranch).containsKeys("lastCommitSha", "name");

	}

}
