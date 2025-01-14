# SynchronyDemo

The service has multiple endpoints where each has a unique operation connecting with MySQL or Redis Cache.
Below are the endpoints and mentioned its sample request.

# v1/create/user
1. This endpoint is used to create a new user in DB and add the user in Redis Cache in parallel with given userdetails.
sample-request-path: src/test/resource/json/request/valid-add-user-request.json

# v1/get/user/details
1. This endpoint is used to fetch userdetails from Redis Cache or DB for the given Id.
2. Fetch by Id from cache is initiated followed by fetch by email in case of Fetch by Id fails. If both cache call fails, user is fetched from DB and saved to cache.
sample-request-path: src/test/resource/json/request/valid-get-user-request.json

# v1/get/all/users
1. This endpoint is used to fetch all userdetails from Redis Cache and DB.
2. Redis Cache call is initiated first. If it fails, details are fetched from DB. If no users found, exception is thrown with "No users found."
3. No request is required to fetch all users hence Get Mapping is used.

# v1/update/user
1. This endpoint is used to update a userdetail in both DB and Redis Cache in parallel for given Id.
sample-request-path: src/test/resource/json/request/valid-update-user-request.json

# v1//delete/user
1. This endpoint is used to delete userdetails from Redis Cache and DB in parallel based on given id/cache key.
2. If user not found in either of Cache/DB, exception is thrown with "No users found".
sample-request-path: src/test/resource/json/request/valid-delete-user-request.json

# v1/delete/all/users
1. This endpoint is used to delete all userdetails from Redis Cache and DB.
2. No request is required to delete all users hence Delete mapping used.


Note : Redis Cache and DB should be running in local to connect and recieve/update data successfully.
