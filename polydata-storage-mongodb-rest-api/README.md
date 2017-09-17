# Mongodb storage REST api

Service for fetching mongodb records over RESTful + Hateaos API

## API endpoints

/api/v1/storage/{storage} - Get storage information
/api/v1/storage/{storage}/query - Query stored records
/api/v1/storage/{storage}/tags - GET tags
/api/v1/storage/{storage}/tags/{tags-storage} - GET tags from specific tag storage
/api/v1/storage/{storage}/tag/{tag} - GET tag index
/api/v1/storage/{storage}/poly/{id} - GET tag by id
/api/v1/storage/{storage}/poly - POST with list of IDs

## System endpoints

/jmx/ - JMX console

## Debug API calls

Storage info
```
curl -v http://localhost:8080/api/v1/storage/localhost
```

Query request
```
curl -v -X POST -H "Content-Type: application/json" -d '{}'  http://localhost:8080/api/v1/storage/localhost/query
```

Random order
```
curl -v -X POST -H "Content-Type: application/json" -d '{"randomOrder":"true"}'  http://localhost:8080/api/v1/storage/localhost/query
```

