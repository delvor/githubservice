package github404;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/github")
public class Controller {

	private final GitService gitHubService;

	public Controller(GitService gitHubService) {
		this.gitHubService = gitHubService;
	}

	@GetMapping("/users/{username}/repos")
	public ResponseEntity<?> getRepos(@PathVariable String username) {
		try {
			List<Map<String, Object>> repos = gitHubService.getNonForkRepositories(username);
			return ResponseEntity.ok(repos);
		} catch (NoSuchElementException e) {
			return ResponseEntity.status(404).body(Map.of("status", 404, "message", e.getMessage()));
		}
	}
}
