# Overview

This is a very simple web server.
The goal is to experiment with Clojure, using 3rd party libraries where possible
to simplify managing things like:

- Package management: [Leiningen](http://leiningen.org/)
- HTTP Web Serving: [Ring](https://github.com/ring-clojure/ring)
- URL Routing: [Compojure](https://github.com/weavejester/compojure)
- Data Access: [Korma](http://www.sqlkorma.com/)
- CSV Generation: [Clojure-csv](https://github.com/davidsantiago/clojure-csv/blob/master/test/clojure_csv/test/core.clj)

# Implementation Details
This webapp is designed to operate directly on the lp-webapp database, loading
entities as necessary.

## Prerequisites

You will need [Leiningen][1] 2.0.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Setup

```
cd leads-csv-clojure
lein deps
```

## Running

To start a web server for the application, run:

```
lein ring server-headless
```

Now go to your browser and enter:

```
http://localhost:3000/pages/:page_id
```

Where `page_id` is a valid page ID for your local `lp_webapp` database.

## License

Copyright © 2014 Aaron Oman
