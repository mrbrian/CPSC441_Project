CPSC 441 Mafia!

1. Install Java
    * `sudo apt-get update`
    * `java -version`
    * `sudo apt-get install default-jre`
    * `sudo apt-get install default-jdk`
    * `sudo apt-get install ant`
   
   
2. Extract __assignment3.tar.gz__
 
3. Navigate to __assignment3__
 
4. Build assignment3
    * `ant`

5. How to run the game __(server)__
    * Navigate to __bin__
    * `java server.SelectServer <port number>`
    
   
6. How to run the game (__client(s)__)
    * Perform this step for as many clients as desired/wanted
        * `java client.TCPClient <server public IP> <server port number>`


7. Commands that a user can use in the terminal
   * Following commands can be used when not logged in
      * `/createaccount username password` - allows user to create a new account
      * `/login username password` - lets player to login to the game and then they will be able to play games
      * `/showstate` - give up-to-date information about player's attributes

   * Following commands can be used when logged in but not yet in a game room
      * `/createroom room#` - create a game room that other players can join
      * `/listrooms` - lists already existing game rooms
      * `/logout` - end program
      * `/listusers` - shows all players that are currently online
      * `/showstate` - give up-to-date information about player's attributes
      * `/setalias name` - use a pseudonym to hide your identity 
      * `/observe room#` - look in on a game in progress
      * `/join room#` - join a room that already exists
      * `/accept` - accept an invite to a game room
      
   * Following commands can be used when in a room before game starts
      * `/ban username` - kick user from the room
      * `/leave` - leave current room
      * `/invite username` - invite friend to game
      * `/startgame` - begin the game
      * `/showstate` - give up-to-date information about player's attributes
      
   * Following commands can be used when a game is in progress
      * `/ban username` - kick a user from game
      * `/vote username` - vote for who you want to lynch 
      * `/switchturn` - switch state from day to night
      * `/showstate` - give up-to-date information about player's attributes
