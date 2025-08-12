# AI Resume Builder Backend

This is the backend service for the AI Resume Builder application. It is built using Java and Spring Boot, providing RESTful APIs to generate and manage resumes using AI.

## Features
- Generate professional resumes using AI
- RESTful API endpoints for resume creation
- Modular service and controller structure
- Easily configurable via `application.properties`

## Project Structure
```
src/
	main/
		java/com/resume/backend/
			ResumeAiBackendApplication.java      # Main Spring Boot application
			ResumeRequest.java                  # Model for resume requests
			controller/ResumeController.java    # REST API controller
			service/ResumeService.java          # Service interface
			service/ResumeServiceImpl.java      # Service implementation
		resources/
			application.properties              # App configuration
			resume_prompt.txt                   # AI prompt template
	test/
		java/com/resume/backend/
			ResumeAiBackendApplicationTests.java # Unit tests
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run
1. **Clone the repository:**
	 ```sh
	 git clone <repo-url>
	 cd resume-ai-backend-main
	 ```
2. **Build the project:**
	 ```sh
	 ./mvnw clean install
	 ```
3. **Run the application:**
	 ```sh
	 ./mvnw spring-boot:run
	 ```
	 The backend will start on `http://localhost:8080` by default.

### API Endpoints
- `POST /api/resume/generate` - Generate a resume from user input

#### Example Request
```
POST /api/resume/generate
Content-Type: application/json

{
	"name": "John Doe",
	"email": "john@example.com",
	...
}
```

### Configuration
Edit `src/main/resources/application.properties` to set environment variables, API keys, etc.

### Testing
Run tests with:
```sh
./mvnw test
```

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License
This project is licensed under the MIT License.
