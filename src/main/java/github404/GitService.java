package github404;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GitService {

	private final RestTemplate restTemplate = new RestTemplate();
	private final String GITHUB_API = "https://api.github.com";
	private String token;

	public GitService(@Value("${github.token}") String githubToken) {
		this.token = githubToken;
	}

	public List<Map<String, Object>> getNonForkRepositories(String username) {
		String reposUrl = GITHUB_API + "/users/" + username + "/repos";

		try {
			ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(reposUrl, HttpMethod.GET,
					getHeaders(), new ParameterizedTypeReference<>() {
					});

			List<Map<String, Object>> repositories = response.getBody();
			if (repositories == null)
				return List.of();

			List<Map<String, Object>> result = new ArrayList<>();

			for (Map<String, Object> repo : repositories) {
				Boolean isFork = (Boolean) repo.get("fork");
				if (isFork)
					continue;

				String repoName = (String) repo.get("name");
				Map<String, Object> owner = (Map<String, Object>) repo.get("owner");
				String ownerLogin = (String) owner.get("login");

				List<Map<String, String>> branches = getBranches(ownerLogin, repoName);

				result.add(Map.of("repositoryName", repoName, "ownerLogin", ownerLogin, "branches", branches));
			}

			return result;

		} catch (HttpClientErrorException.NotFound e) {
			throw new NoSuchElementException("User not found");
		}
	}

	private List<Map<String, String>> getBranches(String owner, String repo) {
		String url = GITHUB_API + "/repos/" + owner + "/" + repo + "/branches";

		ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, getHeaders(),
				new ParameterizedTypeReference<>() {
				});

		List<Map<String, Object>> branches = response.getBody();
		if (branches == null)
			return List.of();

		List<Map<String, String>> result = new ArrayList<>();

		for (Map<String, Object> branch : branches) {
			String name = (String) branch.get("name");
			Map<String, Object> commit = (Map<String, Object>) branch.get("commit");
			String sha = (String) commit.get("sha");

			result.add(Map.of("name", name, "lastCommitSha", sha));
		}

		return result;
	}

	private HttpEntity<Void> getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token);
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		return new HttpEntity<>(headers);
	}
}
