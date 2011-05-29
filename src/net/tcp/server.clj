(ns net.tcp.server
  "Functions for creating a threaded TCP server."
  (:import [java.net InetAddress ServerSocket Socket SocketException]))

(defrecord TcpServer
  [server-socket handler connections])

(defn- socket-server [options]
  (ServerSocket.
   (:port options)
   (:backlog options 50)
   (InetAddress/getByName (:host options "127.0.0.1"))))

(defn tcp-server
  "Create a new TcpServer. Takes the following keyword arguments:
    :host    - the host to bind to (defaults to 127.0.0.1)
    :port    - the port to bind to
    :handler - a function to handle incoming connections
    :backlog - the maximum backlog of connections to keep (defaults to 50)"
  [& {:as options}]
  {:pre [(:port options) (:handler options)]}
  (TcpServer. (socket-server options)
              (:handler options)
              (atom #{})))

(defn close-socket [server socket]
  (swap! (:connections server) disj socket)
  (when-not (.isClosed socket)
    (.close socket)))

(defn- accept-connection
  [{:keys [server-socket handler connections] :as server}]
  (let [^Socket socket (.accept server-socket)]
    (swap! connections conj socket)
    (future
      (try (handler socket)
           (finally (close-socket server socket))))))

(defn start
  "Start a TcpServer going."
  [server]
  (while true
    (try
      (accept-connection server)
      (catch SocketException _))))

(defn stop
  "Stop the TcpServer and close all open connections."
  [server]
  (doseq [conn @(:connections server)]
    (close-socket server conn)))
