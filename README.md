# ETrade – Second-hand Goods Reselling Application

## Project Overview

ETrade is a Java-based web application that allows users to buy, sell, and chat about second-hand goods in a user-friendly and interactive environment. The application includes user registration, login, product listing, browsing, and a chat system for buyer-seller communication.



##  Project Structure


<img width="500" height="400" alt="image" src="https://github.com/user-attachments/assets/369dac85-ea02-423a-ad63-64311e6872b7" />


##  Key Features

### User Management

* User registration and login
* Session-based authentication
* Role management for future admin access

### Product Listing & Browsing

* Create a new item with title, description, price, and location
* Browse available products
* Filter/search functionality planned

###  Chat System

* Initiate chat between users
* Real-time message sending (WebSocket support via `ChatSocket`)
* View chat history



##  Technologies Used

| Layer              | Tools / Frameworks                    |
| ------------------ | ------------------------------------- |
| Presentation Layer | JSP, HTML, CSS, JavaScript            |
| Business Logic     | Java Servlets, Custom Services        |
| Data Layer         | MySQL (or PostgreSQL), JDBC, DAOs     |
| DevOps             | Docker, GitHub Actions (CI), IntelliJ |



##  How to Run the Application

###  Prerequisites

* Java 17+
* Apache Tomcat 10
* MySQL or PostgreSQL
* Maven
* Docker (optional for containerized deployment)

###  Local Deployment (Manual)

1. Clone the repository
2. Set up your database and import schema
3. Configure DB connection in DAO classes
4. Build and deploy with Maven
5. Run the project in a servlet container (e.g., Tomcat)
6. Open in browser:http://localhost:8080/views/login.jsp

###  Docker Deployment (Recommended)


docker-compose up --build

Once the containers are running, open:
http://localhost:8080/views/login.jsp


