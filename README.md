# TCP-Server

A Clojure library for creating TCP servers. Similar in scope to the
`clojure.contrib.server-socket` library, but without the baggage of
having to depend on Clojure-Contrib.

## Usage

This will create a small server that sends "Hello World" to any
connecting client, then closes the connection.

    (use 'net.tcp.server)

    (defn handler [reader writer]
      (.append writer "Hello World"))

    (def server
      (tcp-server 
        :port    5000
        :handler (wrap-io handler)))

    (start server)

## License

Copyright (C) 2011 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
