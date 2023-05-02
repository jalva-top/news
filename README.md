# News

## How to launch the application
- Clone the repository to a local directory: `git@github.com:jalva-top/news.git`
- Enter the directory via terminal
- Build the project: `./gradlew clean build`. 
    Make sure you have necessary java version (17) or set your current version in `build.gradle.kts` file (`sourceCompatibility`, `jvmTarget`)
- `docker-compose build`
- `docker-compose up -d`
-  OpenAPI: http://localhost:8080/swagger-ui/index.html

## How to use the application
Let's concider that the Security API is an external resource outside the application scope. This part can be used without Bearer token
- Create a user: `POST /security/users`
- Create a token: `POST /security/users/{userId}/token`

The folloving API needs Bearer token Authorization and a user with `EDITOR` role apart from GET articles endpoints
- Create an author:  `POST api/v1/authors`
- Create an article: `POST api/v1/articles`
- Update an article: `PUT api/v1/articles/{articleId}`
- Delete an article: `DELETE api/v1/articles/{articleId}`
