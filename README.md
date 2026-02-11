# 🦆 Duck Social Network

**Duck Social Network** is a desktop social media application designed for the avian community. Built using **Java** and **JavaFX**, it simulates a social platform where users (ducks) can interact, form friendships, communicate, and participate in competitive events.

## 🚀 Key Features

* **User Management:** Create, update, and delete user profiles (Swimming Ducks & Flying Ducks).
* **Friendship System:** Send, accept, or reject friend requests. View friendship graphs.
* **Messaging:** Real-time chat functionality between friends.
* **Event Management:**
    * Create and manage public events.
    * **Race Simulation:** Users can subscribe to swimming races. The race logic is handled using **Multi-threading** and specific algorithms (Strategy Pattern) to determine winners based on duck stats (speed, stamina).
* **Smart UI:**
    * Pagination for large data sets.
    * Real-time notifications using the **Observer Pattern**.
    * Filtering and search capabilities.

## 🛠️ Tech Stack

* **Language:** Java (JDK 17+)
* **GUI Framework:** JavaFX (FXML)
* **Database:** PostgreSQL
* **Build Tool:** Gradle / Maven
* **Architecture:** MVC (Model-View-Controller)

## 🧩 Architecture & Design Patterns

This project was built to demonstrate advanced Object-Oriented Programming concepts:
* **Layered Architecture:** Domain, Repository, Service, Controller.
* **Repository Pattern:** Abstracted data access logic.
* **Observer Pattern:** Used for real-time UI updates (e.g., when a race finishes or a message is received).
* **Strategy Pattern:** Implemented for different race calculation algorithms.
* **DTOs (Data Transfer Objects):** Used for transferring complex data (e.g., User Profile Page) to the UI.

## ⚙️ Setup

1.  Clone the repository.
2.  Set up the **PostgreSQL** database using the script provided in `data/schema.sql` (or relevant folder).
3.  Configure database credentials in `Main.java` or config file.
4.  Run the application!

---
*Created as a project for Advanced Programming Methods (MAP).*
