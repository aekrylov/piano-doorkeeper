# Doorkeeper - door access manager

## How to use

A test instance is available at https://piano-doorkeeper.herokuapp.com/

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

```http request
HTTP/1.1 403 Forbidden
Content-Type: application/json
Content-Length: 24

{"code":"access_denied"}
```

### Possible response codes

* `200` `enter_success`
* `200` `leave_success`
* `400` `invalid_request`
* `403` `access_denied` - the user does not have access to the room
* `403` `enter_different_room` - unable to enter, the user has entered a different room already
* `403` `leave_not_in_room` - unable to leave, the user has not entered this room

## How to run 

* `docker-compose up -d`
* Setup a local Redis instance manually (or use `docker-compose up redis`) and run `./gradlew bootRun`

