# Platzi Fake Store API — Rest Assured Test Suite

A complete API automation project for [Platzi Fake Store API](https://fakeapi.platzi.com) built with **Rest Assured + TestNG + ExtentReports** in Java.

---

## Project Structure

```
platzi-api-tests/
├── pom.xml
└── src/
    └── test/
        ├── java/com/platzi/api/
        │   ├── tests/
        │   │   ├── AuthTests.java             
        │   │   ├── ProductsGetTests.java      
        │   │   ├── ProductsCrudTests.java    
        │   │   ├── ProductsFilterTests.java   
        │   │   ├── CategoriesTests.java       
        │   │   └── UsersTests.java            
        │   └── utils/
        │       ├── BaseTest.java
        │       ├── ConfigManager.java
        │       ├── ExtentReportManager.java
        │       └── TestNGListener.java
        └── resources/
            └── testng.xml
```

---

### Run specific module
```bash
mvn clean test -Dtest=AuthTests
mvn clean test -Dtest=ProductsGetTests
mvn clean test -Dtest=ProductsCrudTests
mvn clean test -Dtest=CategoriesTests
mvn clean test -Dtest=UsersTests
```

---

## Reports

After the run, find the HTML report at:
```
test-output/ExtentReport.html
```

---

## API Under Test

Base URL: `https://api.escuelajs.co/api/v1`

| Module         | Endpoints Covered                              |
|----------------|------------------------------------------------|
| Auth           | POST /auth/login, GET /auth/profile            |
| Products GET   | GET /products, /products/{id}, /products/slug  |
| Products CRUD  | POST / PUT / DELETE /products                  |
| Filter         | GET /products?title, price_min, price_max, categoryId |
| Categories     | Full CRUD + /categories/{id}/products          |
| Users          | Full CRUD + pagination                         |

---

## Test Coverage

| Type        | Count |
|-------------|-------|
| Functional  | 31    |
| Negative    | 15    |
| Boundary    | 4     |
| Schema      | 1     |
| **Total**   | **51** |
