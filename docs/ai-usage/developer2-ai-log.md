# Developer 2 AI Log

## Member

**Name:** Diego Armando Mestre Gomez  
**Role:** Developer 2 — People

## AI Usage

### 1. Person model review
**Purpose:** Review the structure of the people domain model.

**AI assistance:** Used AI to review the abstract `Person` class and the `Customer` and `Seller` specializations.

**Student decision:** The inheritance structure was kept consistent with the project class diagram.

---

### 2. Encapsulation and attributes
**Purpose:** Verify the visibility and access methods of the people classes.

**AI assistance:** Used AI to review private attributes, constructors, getters, and setters.

**Student decision:** The classes use private attributes and only the accessors required by the project design.

---

### 3. Person persistence
**Purpose:** Review how customers and sellers are stored and loaded.

**AI assistance:** Used AI to review the repository persistence logic using text files.

**Student decision:** `PersonRepository` is responsible for saving and loading customers and sellers.

---

### 4. Customer and seller registration
**Purpose:** Review the business operations for people.

**AI assistance:** Used AI to review customer registration, customer listing, and seller listing.

**Student decision:** People operations are handled through `PersonService` instead of being accessed directly from the UI.

---

### 5. Preloaded sellers
**Purpose:** Verify the required initial sellers.

**AI assistance:** Used AI to review the initialization behavior when the seller data file does not exist.

**Student decision:** The system initializes and persists three sellers so they are available when the application starts.

---

## Final Reflection

AI was used as a support tool to review the people model, persistence, validation, and service logic. The final implementation decisions were made by the student and verified through project testing.