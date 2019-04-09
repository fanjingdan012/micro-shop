simplest spring boot+hibernate+mysql app template
with optimistic lock manage
# Run
- Fill in application.properties
- Open browser
  - GET http://localhost:8080/Users/
  - POST http://localhost:8080/Users
    `{"name"="fjd","email"="fjd@fjd.com"}`
  - PUT http://localhost:8080/Users/1
    `{"name":"fjd4","email":"abb@aa.com","version":0}`
    version is the current version in db