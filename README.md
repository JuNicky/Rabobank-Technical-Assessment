# Rabobank Technical Assessment - Library Management System

## Versions used
* Java 21.0.6
* Maven 3.9.9
  

## Getting started
This README.md assumes that Java and Maven are already installed and that the project has been cloned or downloaded.

### Build the application
```mvn clean install```
This command will compile the code, run the tests and package the application into a .jar file.

### Run the application
```mvn spring-boot:run```
This command will start the application and it will be accessible at `http://localhost:8080`.

Upon initialization, a h2 database will be created at `<projectdirectory>/data`.


## API Endpoints

### Book Endpoints
-   `GET /books` - Get all books
-   `GET /books/{id}` - Get a book by ID
-   `GET /books/user/{userId}` - Get all books borrowed by a specific user
-   `GET /books/search?title={title}&author={author}` - Search for books by title and/or author
-   `POST /books` - Add a new book
-   `PUT /books/{id}` - Update an existing book
-   `DELETE /books/{id}` - Delete a book
-   `PUT /books/borrow/{id}/{userId}` - Mark a book as borrowed by a user
-   `PUT /books/return/{id}` - Mark a book as returned

### User Endpoints
-   `GET /users` - Get all users
-   `GET /users/{id}` - Get a user by ID
-   `POST /users` - Create a new user


## Running Tests
```mvn test```

This command will run all the unit tests for the application. Additionally, a report including the test coverage will be created at `<projectdirectory>/target/site/jacoco/index.html`.

The image below shows the test coverage.

![Code Coverage Screenshot](./assets/Code%20Coverage%20Screenshot.png)

## Running Checkstyle
```mvn checkstyle:checkstyle```

This command will run Checkstyle for the entire project. A report will be created at `<projectdirectory>/target/site/checkstyle.html`.

Note: we are using 
[Checkstyle](https://checkstyle.org/) 9.3 with sun_checks.xml ruleset. However, a few rules have been ignored, which can be found in `supressions.xml`.