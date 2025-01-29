User Interface - Command Line Interface (CLI)

CLI - Command-Line Interface (CLI). It has the following functionalities (modes):
●	Registering a new user
●	User login
●	Adding posts (chat)
●	Displaying the post board (chat): 10 latest messages
●	File Transfer::
-	Sending files from a user-specified path to a special disk server for general filing.
-	Downloading a file with a user-specified name.
●	HELP: Basic information about the project and its usage.
Manager

The Manager class is an integral part of the ApiGateway package. It serves as the central manager responsible for handling microservices within the application. This class follows the Singleton pattern to ensure that only one instance of Manager exists throughout the application's execution. Here's a breakdown of the key components and methods in the code:
Fields:
•	instance: A private static field that holds the single instance of the Manager class.
•	microserviceAgent: An instance of the MicroserviceAgent class, used for communication with microservices.
Constructor:
•	private Manager(): This private constructor initializes the microserviceAgent, ensuring that only one instance of Manager can be created.
Public Method:
1.	getInstance(): This public static method is used to obtain the single instance of the Manager class. It employs synchronization to ensure thread safety during instance creation.
Private Methods:
1.	handleCommunication(String request): This private method handles communication with microservices through the MicroserviceAgent. It sends a request and receives a response.
2.	registerMicroservice(String serviceName): This private method is responsible for registering a microservice. It creates a request string, sends it to the MicroserviceAgent, and parses the response to obtain the assigned port.
3.	parsePortFromResponse(String response): Another private method for parsing the port from the response string received after registration.
4.	parseRegisterFromResponse(String response): This method parses the registered port from the response string.
5.	findAvailablePort(): This private method finds an available port for microservices. It starts from the default port number of 3000 and increments until an available port is found.
6.	isPortOccupied(int port): A private method that checks if a given port is occupied by attempting to create a new server socket. It returns true if the port is occupied.
In essence, the Manager class acts as a crucial component in the Microservice Manager, facilitating the registration, communication, and management of microservices within the application. It ensures that microservices are assigned unique ports and provides a centralized point for controlling their lifecycle.

MICROSERVICE AGENT

The MicroserviceAgent class serves as the orchestrator for microservice management within the application's architecture. It handles the initiation, termination, and port retrieval for microservices. Here is a description of the key components and methods in the code:
Fields:
•	instance: A private static field that holds the single instance of the MicroserviceAgent class.
•	microserviceThreads: A map that stores microservice threads, indexed by microservice names.
•	microservicePorts: A map that associates microservice names with their respective ports.
Constructor:
•	private MicroserviceAgent(): This private constructor initializes the microserviceThreads map. It follows the Singleton pattern, ensuring only one instance of MicroserviceAgent exists.


Public Method:
1.	getInstance(): This public static method is used to obtain the single instance of the MicroserviceAgent class. It employs synchronization to ensure thread safety during instance creation.
2.	startMicroservice(String serviceName): This static method starts a microservice with the given service name. It creates a new thread for the microservice and invokes its main method, allowing it to run independently.
3.	stopMicroservice(String serviceName): This method stops a microservice with the specified service name. It interrupts the microservice thread, releasing the associated port.
4.	communication(String request): This static method handles communication requests from the Manager class. It parses requests, extracts relevant information, and dispatches appropriate actions based on the request type.
5.	registerMicroservice(String serviceName, int port): This method registers a microservice with the given service name and port. If the microservice is not already registered, it adds it to the microservicePorts map and starts it.
6.	getPort(String serviceName): This method retrieves the port associated with a microservice based on its service name. If the microservice is not found, it returns -1.
The MicroserviceAgent class plays a crucial role in mediating interactions between microservices and the Manager. It enables the dynamic management of microservices by starting, stopping, and allocating ports, ultimately contributing to the overall microservices architecture's functionality.

API Manager

The APIManager class in the ApiGateway package serves as the entry point for managing API-related functionalities in the application. It handles incoming client connections, communication, and interactions with microservices. Here is an overview of the key components and methods in the code:

Main Method:
•	main(String[] args): The entry point of the application, which starts the APIManager by invoking the startAPIManager method.
Private Methods:
1.	startAPIManager(): This method initializes the APIManager, creates a ServerSocket, and enters a loop to accept incoming client connections. Each client connection is handled by a separate thread (ClientHandler).
2.	createServerSocket(): Creates a ServerSocket on port 3000 and sets the reuse address property.
3.	acceptClientConnection(ServerSocket server): Accepts a client connection and returns the associated Socket.
4.	handleIOException(IOException e, String errorMessage): Handles IOExceptions by printing the stack trace and displaying an error message.
Inner Class: ClientHandler
•	This inner class implements the Runnable interface and is responsible for handling communication with individual clients.
Methods within ClientHandler:
1.	run(): Implements the Runnable interface's run method. It sets up communication streams with the client, handles client communication, and closes the client socket when done.
2.	handleClientCommunication(PrintWriter out, BufferedReader in): Handles communication with the client by processing incoming messages and responding accordingly. It identifies the message type and invokes the appropriate method.
3.	sendError(PrintWriter out): Sends an error response to the client in case of an invalid message type.
4.	sendMicroservicePortResponse(PrintWriter out, String serviceName): Sends a microservice port response to the client, requesting a port for a specific microservice. It communicates with the Manager to obtain the port.
5.	deleteMicroserviceAgent(PrintWriter out, String serviceName): Handles the deletion of a microservice agent and provides a log message.
6.	closeClientSocket(): Closes the client socket.
The APIManager class serves as a central component for handling client requests and managing microservices' communication through the Manager. It provides a way to request microservice ports and handle agent deletions.

API Gateway

API Gateway - CNApp entry points for users. Typically, an API Gateway has only one component. Its functionality involves forwarding user requests to the appropriate microservices. The API Gateway is stateless, operating without maintaining state (transferring byte streams between various components, both from microservices to clients and from clients to microservices). It maintains connections with clients using threads.
Public Methods:
1. Method `run()`: -The `run()` method is responsible for handling the client connection via the server socket. Upon accepting a connection, this method creates a `BufferedReader` object for reading input data from the client and a `PrintWriter` object for sending output data to the client. It then reads lines of requests from the client using the `readLine()` method and passes them to the `forwardRequest()` method for processing. Responses returned by `forwardRequest()` are sent back to the client using the `PrintWriter` object. In case of input-output errors (IOException), the method catches the exception and prints it to the standard error output.
Private Methods:
1. Method `forwardRequest(String request, String serviceName, PrintWriter out)`: The `forwardRequest()` method is responsible for forwarding requests from the client to the appropriate microservices based on the request type. Upon receiving a request from the client, this method registers and starts the corresponding microservice using the `Manager` class. It then retrieves the port on which this microservice is running and creates a client socket for communicating with the microservice. The request is forwarded to the microservice via the client socket, and the response is received and forwarded back to the client using the `PrintWriter` object. Finally, the microservice is stopped and unregistered using the `Manager`. In case of input-output errors (IOException) or thread interruption (InterruptedException), the method catches the exception and prints it to the standard error output.
Microservices in the application are also stateless, meaning they do not store state between requests and pass data (bytes) either to other microservices or to end users.


Types of microservices:
1.	User login
2.	User registration
3.	Chat - available only for logged-in users, allows adding new entries to the board
4.	Board - accessible without the need for logging in, displays the latest 10 posts
5.	File transfer - available only for logged-in users, allows sending and downloading specific files.

Database Project

 The database is written in MySQL and is named "tst". It is used to store information about users, files in the system, and archive user posts. It consists of two tables: "uzytkownicy" and "posty". The detailed structure of this database is described below.

Graphical Representation of the Database

 
      Database code
1.	-- Tworzenie bazy danych 'tst' (jeśli nie istnieje)
2.	CREATE DATABASE IF NOT EXISTS tst;
3.	
4.	-- Używanie bazy danych 'tst'
5.	USE tst;
6.	
7.	-- Tworzenie tabeli 'uzytkownicy' do przechowywania informacji o użytkownikach
8.	CREATE TABLE IF NOT EXISTS uzytkownicy (
9.	    id INT AUTO_INCREMENT PRIMARY KEY,
10.	    login VARCHAR(255) NOT NULL,
11.	    haslo VARCHAR(255) NOT NULL
12.	);
13.	
14.	-- Tworzenie tabeli 'posty' do przechowywania wiadomości czatu
15.	CREATE TABLE IF NOT EXISTS posty (
16.	    id INT AUTO_INCREMENT PRIMARY KEY,
17.	    nick VARCHAR(255) NOT NULL,
18.	    tresc TEXT,
19.	    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP
20.	);
21.	
22.	-- Ustawianie kodowania znaków na UTF-8
23.	ALTER DATABASE tst CHARACTER SET utf8 COLLATE utf8_unicode_ci;
24.	ALTER TABLE uzytkownicy CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
25.	ALTER TABLE posty CONVERT TO CHARACTER SET utf8 COLLATE utf8_unicode_ci;
26.	
27.	select * from uzytkownicy;




General Operation Scheme:

User Login:
1. The user enters their login credentials, i.e., username and password.
2. A request is sent to the login microservice through the API, and the data is appropriately interpreted.
3. The server authorizes this data by querying the "users" table in the database.
4. If the data is correct, the server grants access to resources only for logged-in users. The response contains the operation status, which, in turn, determines the user's login status stored in the "log" variable.
User Registration:
1. A new user enters their data, including username and password.
2. A request is sent to the registration microservice through the API, and the data is appropriately interpreted.
3. The server checks whether the login is not already in use by another user.
4. If the data is unique, the server adds the new user to the "users" table in the database.
5. A response is returned to the user with the appropriate status.

Chat:
1. The chat is available only for logged-in users. The "log" variable is checked.
2. If the previous point is true, the user can enter a message.
3. A request is sent to the login microservice through the API, and the data is appropriately interpreted.
4. Chat messages are stored in the "chat" table in the database, including information about the author, content, and time. The server sends a command to the database.
5. The status of whether the operation was successful is returned.

Board:
1. The board is available without the need for logging in.
2. A request is sent to the login microservice through the API, and the data is appropriately interpreted.
3. The server retrieves the latest 10 posts from the "chat" table that can be read.
4. In the response, the server provides the 10 latest messages to the user, and the data is interpreted and displayed in the console if the operation status is successful.

File Transfer:
1. Available only for logged-in users. The "log" variable is checked.
2. If the previous point is true, the user can perform the appropriate operation or send a request with the file name for download or the given file path for file upload, depending on the selected option.
3. A request is sent to the file transfer microservice through the API, and the data is appropriately interpreted.
4. The server connects to the database and retrieves values from the "files" table.
5. Then, from the server based on the saved URL address, the appropriate file is sent to the user by sending a response according to the protocols.
6. On the user's side, the file is decoded and the saveFile() function is called to save the file.
7. In the case of sending a file based on the given path, the file is encoded in Base64, and then a request is sent according to the protocols to the file transfer microservice where we interpret the request through the API and then send packets to the file server to save it in the folder. Data about the file and its URL address are stored in the database in the "files" table.
8. Finally, a message with the appropriate status is returned to the client.
