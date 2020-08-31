# Doorkeeper - door access manager

## How to use

```http request
GET /check?keyId=1&roomId=1&entrance=true HTTP/1.1
Host: localhost:8080
```

```http request
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 24

{"code":"enter_success"}
```

## How to run 

* `docker-compose up -d`
* Setup a local Redis instance manually and run `./gradlew bootRun`

