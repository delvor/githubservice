# GitHub Repository Branch Viewer

This Spring Boot application provides a REST API to fetch **non-fork** GitHub repositories for a given user along with their **branches and last commit SHA**. It works as a proxy to the GitHub REST API and handles authentication using a personal access token.

## 📌 Features

- Fetches public non-fork repositories for a specified GitHub username.
- For each repository, retrieves all branches with the SHA of the last commit.
- Skips forked repositories.
- Uses authenticated requests to avoid GitHub API rate limiting.
- Proper error handling (e.g. 404 if user not found, 403 for rate limit exceeded).

## 📦 Technologies

- Java 17+
- Spring Boot 3+
- Spring Web 
- Maven

## 🚀 Running the Application

### Prerequisites

- Java 17+
- Maven
- GitHub personal access token (classic or fine-grained with `public_repo` read access)

### 1. Clone the repository

```bash
git clone git@github.com:delvor/githubservice.git
cd githubservice
```

### 2. Configure GitHub token

Edit a src/main/resources/application.properties file:

```
github.token=ghp_your_token_here
```
ℹ️ You can generate a token at https://github.com/settings/tokens

### 3. Build and run the project

```bash
mvn spring-boot:run
```
The API will be available at http://localhost:8080.


``` endpoint
GET /api/github/users/{username}/repos
```

### Example
```bash
curl http://localhost:8080/api/github/users/octocat/repos
```

```Sample response

[
  {
    "repositoryName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "a84d88e7554fc1fa21bcbc4efae3c782a70d2b9d"
      }
    ]
  }
]
```

### ✅ Tests
To run tests:

```bash
mvn test
```
Integration tests use TestRestTemplate and expect the application to load on a random port.
