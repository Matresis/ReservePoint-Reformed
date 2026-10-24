# Reservation Management System (ReservePoint) - Reformed

#### Author: Matěj Červenka
#### Contact: 
- matej.cervenka1106@gmail.com
- [GitHub](https://github.com/Matresis)
- [LinkedIn](https://www.linkedin.com/in/matejcervenka)

## Deployment Information

- **Website URL:** [ReservePoint](http://141.147.9.34/)

## Overview

- ReservePoint is a web-based Reservation Management System built using Spring Boot. 
- It provides a user-friendly interface for users to register, log in, make reservations, and manage their bookings.
- The system ensures secure authentication, real-time reservation tracking, and structured data storage with a MySQL database.

## Reformed Features

- This project is a reformed version of the original Reservation Management System.
- The reform includes:
  - A complete overhaul of the user interface, aimed at enhancing its appeal, usability, and responsiveness.
  - A redesign of the frontend, involving a complete change of technologies—specifically, a migration from Thymeleaf to React.
  - A rewrite of the backend to facilitate integration with the new frontend.

---

## Installation & Setup
### Požadavky
- **JDK 17+**
- **MySQL 8+**
- **Maven**
- **Spring Boot**
- **SMTP účet pro emailovou komunikaci**

### Instalace
1. Naklonujte tento repozitář:

```bash
git clone https://github.com/Matresis/reservation-system.git
cd reservation-system
```

1. Ve složce `src/main/resources/` vytvořte soubor `application.properties`

2. Nastavte databázové připojení v `application.properties`:

    ```ini
    spring.datasource.url=jdbc:mysql://<your-db-host>:3306/reserve_system
    spring.datasource.username=<your-username>
    spring.datasource.password=<your-password>
    ```

3. Spusťte aplikaci pomocí Maven:

```bash
mvn spring-boot:run
```
- Aplikace poběží na http://localhost:8080/.

---

## Email Notification System
- Rezervační systém používá SMTP pro automatizovanou emailovou komunikaci.

### Konfigurace emailů
V souboru `application.properties`:

```ini
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your-email>
spring.mail.password=<your-app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```
### Odesílané emaily
- **Potvrzení rezervace** – Po vytvoření rezervace uživatel obdrží email s detaily rezervace.
- **Notifikace admina** – Admin dostane email o nové rezervaci.
- **Schválení / Zamítnutí** – Admin schválí nebo zamítne rezervaci a uživatel obdrží email s informací.
- **Požadavky na změny** – Požadavky na úpravy či zrušení rezervací jsou adminovi zasílány emailem.

---

## API Endpoints

### 1. Uživatelé

| HTTP Method | Endpoint        | Popis                             |
|-------------|-----------------|-----------------------------------|
| **GET**     | `/loginForm`    | Přihlašovací formulář             |
| **POST**    | `/register`     | Registrace uživatele              |
| **GET**     | `/registerForm` | Zobrazení registračního formuláře |
| **POST**    | `/logout`       | Odhlášení uživatele               |

### 2. Rezervace

| HTTP Method | Endpoint                                  | Popis                           |
|-------------|-------------------------------------------|---------------------------------|
| **GET**     | `/home`                                   | Domovská stránka                |
| **GET**     | `/reservations`                           | Seznam rezervací                |
| **GET**     | `/reservations/{id}`                      | Detaily rezervace               |
| **GET**     | `/reserveForm`                            | Formulář na vytvoření rezervace |
| **POST**    | `/make-reservation`                       | Vytvoření rezervace             |
| **POST**    | `/reservations/{id}/request-cancellation` | Žádost o zrušení rezervace      |
| **POST**    | `/reservations/{id}/request-modification` | Žádost o úpravu rezervace       |
| **POST**    | `/reservations/{id}/request-confirmation` | Žádost o potvrzení rezervace    |

### 3. Administrace

| HTTP Method | Endpoint                                    | Popis                                   |
|-------------|---------------------------------------------|-----------------------------------------|
| **GET**     | `/admin`                                    | Admin panel                             |
| **GET**     | `/admin/reservations`                       | Seznam rezervací pro admina             |
| **POST**    | `/admin/reservations/delete`                | Odstranění rezervace                    |
| **POST**    | `/admin/reservations/update`                | Aktualizace rezervace                   |
| **GET**     | `/admin/calendar`                           | Zobrazení kalendáře                     |
| **POST**    | `/admin/calendar/add-event`                 | Přidání události do kalendáře           |
| **GET**     | `/admin/calendar/events`                    | Zobrazení všech událostí v kalendáři    |
| **POST**    | `/admin/calendar/remove-from-calendar`      | Odebrání události z kalendáře           |
| **POST**    | `/admin/calendar/update`                    | Aktualizace události v kalendáři        |
| **GET**     | `/admin/calendar/{id}`                      | Zobrazení detailů konkrétní události    |
| **GET**     | `/admin/requests`                           | Zobrazení seznamu žádostí               |
| **POST**    | `/admin/requests/{id}/approve-cancellation` | Schválení žádosti o zrušení rezervace   |
| **POST**    | `/admin/requests/{id}/approve-confirmation` | Schválení žádosti o potvrzení rezervace |
| **POST**    | `/admin/requests/{id}/approve-modification` | Schválení žádosti o úpravu rezervace    |
| **POST**    | `/admin/requests/{id}/reject-confirmation`  | Zamítnutí žádosti o potvrzení rezervace |
| **POST**    | `/admin/requests/{id}/reject-modification`  | Zamítnutí žádosti o úpravu rezervace    |

---

## Core Features

### User Authentication & Management

**User Registration:**
- Users can create an account with a unique username and email.
- Passwords are securely stored using BCrypt hashing.
- Email validation ensures only valid addresses are accepted.

**User Login:**
- Registered users can log in with their credentials.
- Secure session handling ensures users remain logged in.
- Redirects to the home page upon successful login.

**User Roles:**
- Admins have extra privileges (e.g., approving/canceling reservations).

### User Available Features

**Create a Reservation:**
- Users can select a service and submit reservation details.
- The system auto-fills the user's name, surname, and email based on login credentials.
- A unique reservation ID is generated for each booking.

**View Reservations:**
- Users can view a list of their reservations.
- The reservation list includes details such as date, time, and service type.
- The system prevents users from booking the same time slot twice.

### Admin Available Features

**View Reservations:**
- Admin can view a list of all the reservations.
- The reservation list includes details such as date, time, and service type.

**View Reservation Details:**
- When viewing reservations, Admin can click on button **View Details** and see all detailed information about specific reservation.
- While viewing them, Admin has options to:
	- Edit some information of reservation and change the state of reservation (PENDING, CONFIRMED, CANCELED)
	- Delete the reservation completely
	- Add CONFIRMED reservation into application's **calendar**

---

### Email Communication System

The ReservePoint system uses an automated email notification system to facilitate communication between users and administrators regarding reservation requests, approvals, modifications, and cancellations.

#### 1. User Reservation Flow with Emails
When a user makes a reservation, the system sends automated emails to both the user and the administrator:

- **User Confirmation Email**:
	- The user receives an email confirming that their reservation request has been submitted.
	- The email includes details of the reservation and a unique reservation ID.

- **Admin Notification Email**:
	- The admin receives an email notifying them of a new reservation request.
	- The email contains a direct link to the reservation details page, where the admin can approve or reject the request.

#### 2. Admin Response and Notifications
Once the admin reviews the reservation request, they take one of the following actions:

- **Approval Process**:
	- If the admin **approves** the reservation, the system updates the database and sends an email to the user confirming the approval.

- **Rejection Process**:
	- If the admin **rejects** the reservation, they must provide a reason.
	- The system then sends an email to the user, informing them of the rejection and including the explanation given by the admin.

#### 3. User-Initiated Requests
Users can make additional requests related to their reservation after it has been approved.

- **Request for Confirmation**:
	- After approval, the user can request an additional confirmation from the admin.
	- The admin receives an email notification about the request.
	- The request appears on the admin’s request management page.

- **Modification Request**:
	- Users can request changes to their reservation (e.g., changing time or service type).
	- The admin receives an email with details of the requested modification.
	- The admin can approve or deny the request, and the user receives an email with the decision.

- **Cancellation Request**:
	- Users can request to cancel their reservation.
	- The admin is notified via email and can approve or reject the cancellation.
	- If approved, the user receives a cancellation confirmation email.

#### 4. Email Content and Templates
Each email contains relevant details and direct links to the reservation page for quick actions. The email system ensures smooth communication, reducing delays in handling reservations.


**View calendar:**
- Admin can view calendar of the reservation system.
- In calendar are all the CONFIRMED reservations added manually by Admin.

## Technologies Used

| Technology                             | Purpose                                                  |
|----------------------------------------|----------------------------------------------------------|
| **Spring Boot**                        | Backend framework for handling requests & business logic |
| **Spring Security**                    | Manages authentication & user access                     |
| **Thymeleaf**                          | Server-side templating engine for rendering views        |
| **MySQL**                              | Relational database for storing users & reservations     |
| **Maven**                              | Dependency management & project configuration            |
| **HTML & CSS**                         | Frontend styling & UI design                             |
| **JavaScript (Optional Enhancements)** | Dynamic UI interactions, form validation                 |
| **BCrypt**                             | Secure password hashing                                  |

## Database Schema

### 1. `customer_entity`

| Column Name  | Data Type      | Constraints                                    |
|--------------|----------------|------------------------------------------------|
| `id`         | `BIGINT` (PK)  | Auto-increment, Primary Key                    |
| `address`    | `VARCHAR(255)` | Nullable                                       |
| `created_at` | `DATETIME(6)`  | Not null                                       |
| `phone`      | `VARCHAR(255)` | Not null                                       |
| `user_id`    | `BIGINT` (FK)  | Not null, Unique, References `user_entity(id)` |

---

### 2. `reservation_entity`

| Column Name    | Data Type                                              | Constraints                                |
|----------------|--------------------------------------------------------|--------------------------------------------|
| `id`           | `BIGINT` (PK)                                          | Auto-increment, Primary Key                |
| `created_at`   | `DATETIME(6)`                                          | Not null                                   |
| `ordered_time` | `DATETIME(6)`                                          | Nullable                                   |
| `notes`        | `TEXT`                                                 | Nullable                                   |
| `status`       | `ENUM('APPROVED', 'CANCELED', 'CONFIRMED', 'PENDING')` | Not null                                   |
| `customer_id`  | `BIGINT` (FK)                                          | Not null, References `customer_entity(id)` |
| `service_id`   | `BIGINT` (FK)                                          | Not null, References `service_entity(id)`  |

---

### 3. `reservation_cancellation_request_entity`

| Column Name      | Data Type                     | Constraints                                   |
|------------------|-------------------------------|-----------------------------------------------|
| `id`             | `BIGINT` (PK)                 | Auto-increment, Primary Key                   |
| `reason`         | `VARCHAR(255)`                | Nullable                                      |
| `status`         | `ENUM('APPROVED', 'PENDING')` | Nullable                                      |
| `reservation_id` | `BIGINT` (FK)                 | Not null, References `reservation_entity(id)` |

---

### 4. `reservation_confirmation_request_entity`

| Column Name      | Data Type                     | Constraints                                   |
|------------------|-------------------------------|-----------------------------------------------|
| `id`             | `BIGINT` (PK)                 | Auto-increment, Primary Key                   |
| `status`         | `ENUM('APPROVED', 'PENDING')` | Nullable                                      |
| `reservation_id` | `BIGINT` (FK)                 | Not null, References `reservation_entity(id)` |

---

### 5. `reservation_modification_request_entity`

| Column Name            | Data Type                                 | Constraints                                   |
|------------------------|-------------------------------------------|-----------------------------------------------|
| `id`                   | `BIGINT` (PK)                             | Auto-increment, Primary Key                   |
| `requested_notes`      | `VARCHAR(255)`                            | Nullable                                      |
| `requested_order_time` | `DATETIME(6)`                             | Nullable                                      |
| `status`               | `ENUM('APPROVED', 'PENDING', 'REJECTED')` | Not null                                      |
| `service_id`           | `BIGINT` (FK)                             | Not null, References `service_entity(id)`     |
| `reservation_id`       | `BIGINT` (FK)                             | Not null, References `reservation_entity(id)` |

---

### 6. `service_entity`

| Column Name   | Data Type      | Constraints                 |
|---------------|----------------|-----------------------------|
| `id`          | `BIGINT` (PK)  | Auto-increment, Primary Key |
| `description` | `VARCHAR(255)` | Not null                    |
| `name`        | `VARCHAR(255)` | Not null                    |
| `price`       | `DOUBLE`       | Not null                    |

---

### 7. `user_entity`

| Column Name  | Data Type               | Constraints                 |
|--------------|-------------------------|-----------------------------|
| `id`         | `BIGINT` (PK)           | Auto-increment, Primary Key |
| `created_at` | `DATETIME(6)`           | Not null                    |
| `email`      | `VARCHAR(255)`          | Not null, Unique            |
| `name`       | `VARCHAR(255)`          | Not null                    |
| `password`   | `VARCHAR(255)`          | Not null                    |
| `role`       | `ENUM('ADMIN', 'USER')` | Not null                    |
| `surname`    | `VARCHAR(255)`          | Not null                    |
