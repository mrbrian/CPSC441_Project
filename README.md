CPSC 441 Mafia!

1. Install Java
    * `sudo apt-get update`
    * `java -version`
    * `sudo apt-get install default-jre`
    * `sudo apt-get install default-jdk`
2. Extract __assignment3.tar.gz__
3. Navigate to __assignment3__
4. How to run the game __(server)__
    * Navigate to __bin__
    * `java server.SelectServer <port number>`
5. How to run the game (__client(s)__)
    * Perform this step for as many clients as desired/wanted
        * `java client.TCPClient <server public IP> <server port number>`
