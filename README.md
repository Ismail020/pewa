# Spring Boot PEWA API

## Prerequisites

- **Java 17** or later
- **Maven** (for managing dependencies and building the project)
- **PostgreSQL** database (you can adjust the database properties in `application.properties`)

## Technologies Used

- **Spring Boot 3.3.3**
- **Spring Data JPA** (for database interaction)
- **PostgreSQL** for the database

## Getting Started

### 1. Clone the Repository

```bash
git clone git@gitlab.fdmci.hva.nl:ewa-dt/2425/team5-group/backend.git
cd backend
```

### 2. Set Up the Database

Create a PostgreSQL database for this project. You will need to adjust the database connection settings in the `src/main/resources/application.properties` file.

Example of `application.properties`:
```bash
# PostgreSQL DB settings
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Build the Project

Use Maven to build the project:
```bash
mvn clean install
```

### 4. Run the Application

To run the Spring Boot application:

```bash
mvn spring-boot:run
```
Once the application is running, you can access the API at http://localhost:8080.

### 5. Endpoints

The following endpoints are currently available:

| Method | Endpoint                    | Description                          |
|--------|-----------------------------|--------------------------------------|
| `GET`  | `/api/v1/demo-controller`   | Secured call, returns text           |
| `POST` | `/api/v1/auth/register`     | Register a new user                  |
| `POST` | `/api/v1/auth/authenticate` | Authenticate user                    |
| `POST` | `/api/v1/health`            | Get health status of the application |
| `POST` | `/api/v1/match/create`      | Creates a match in database          |

### 6. Postman Collection

The repository contains a Postman collection (`PEWA.postman_collection.json`) that includes pre-configured API requests to test the available endpoints.

#### How to use the Postman collection:

1. Download or clone the repository to access the `PEWA.postman_collection.json` file.
2. Open **Postman** on your machine.
3. Go to **File** > **Import** in Postman.
4. Click **Upload Files** and select the `PEWA.postman_collection.json` file from your cloned repository.
5. Once imported, the collection will appear in your Postman workspace with all the configured API requests ready for testing.

You can use this collection to quickly test the API endpoints and validate functionality.


### Running Tests

The project includes unit tests for various components. To run the tests, use the following Maven command:

```bash
mvn test
```
### 7. Websocket Endpoints and Channels

The following endpoints are set up for websockets:

| Type (Frame) | Endpoint           | Description                                    | Message format | Method      |
|--------------|--------------------|------------------------------------------------|----------------|-------------|
| `CONNECT`    | `/ws/game`         | Establishes WS connection                      | N/A            | activate()  |
| `SUBSCRIBE`  | `/user/queue/game` | Subscribes to game upgames for a specific user | JSON           | subscribe() |
| `SUBSCRIBE`  | `/topic`           | Subscribes to global updates                   | JSON           | subscribe() |
| `SEND`       | `/app/start`       | Starts matchmaking process                     | JSON           | publish()   |
| `SEND`       | `/app`             | Accepts messages from client                   | JSON           | publish()   |

`/topic`
and 
`/app` can be further extended and specified for particular users, for example `/app/chat/<gameId>` and `/topic/chat/<gameId>`