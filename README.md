## Test task: REST API for exchange rates

### Run Application (Console)

```sh
# build
./gradlew clean build

# run 
./gradlew bootRun

# check alive
curl http://localhost:8080/actuator/health
```

### Customer Use Cases
- get a list of currencies used in the project. 
  - `curl -L 'localhost:8080/api/currencies' -H 'Content-Type: application/json'`

- get exchange rates for a currency.
  - `curl -L 'localhost:8080/api/currencies/EUR/rates' -H 'Content-Type: application/json'`

- add new currency for getting exchange rates.
  - `curl -X POST -L 'localhost:8080/api/currencies' -H 'Content-Type: application/json' --data '{"code": "EUR"}'`
