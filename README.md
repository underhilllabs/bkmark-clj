# bkmarker

A Social Bookmarking site written in Clojure.



## Set Up

1. Change database settings in project.clj and src/bkmarker/db/dbconn.clj
2. Create database
3. Migrate database with: `lein ragtime migrate`
4. start the server `lein run`

## Deploy

#### Create an uberjar for deployment

```
lein uberjar
```
this will create target/bkmarker-0.1.0-SNAPSHOT.jar.

This jar file can be run with:

```
java -jar target/bkmarker-0.1.0-SNAPSHOT.jar 
```

## License

Copyright © 2015 Bart Lantz

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
