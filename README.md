# SnapSale  
*An Android Application for Exploring Current Promotions, Locating Retail Stores, Generating Recipes, and Creating Thematic Shopping Baskets*  

**Bachelor's Thesis – University of Bucharest, Faculty of Mathematics and Computer Science (2024)**  

---

## Overview

**SnapSale** is a mobile application for Android that centralizes shopping promotions from major commercial chains in Romania and helps users make optimized, informed purchasing decisions.

The app was designed in the context of persistent inflation and changing consumer behavior, where many people focus on products on sale and need tools to compare offers efficiently. SnapSale provides a unified experience that:

- Aggregates offers from multiple store chains
- Lets users search products and promotions
- Generates recipes using AI
- Creates thematic shopping baskets
- Locates nearby stores and provides navigation

The project demonstrates an end-to-end Android solution integrating:

- Web scraping (Java + Jsoup)  
- Cloud backend (Firebase Authentication & Realtime Database)  
- Modern Android UI (XML-based layouts)  
- AI services (OpenAI API)  
- Geospatial APIs (Maps SDK, Places API, Directions API)  
- MVC architecture & repository pattern  

---

## Core Features

### 1. Aggregated Promotions & Search

SnapSale centralizes promotional data by scraping public web pages of Romanian retail chains using **Jsoup**. Data is then stored in **Firebase Realtime Database**.

**Highlights:**

- Automatic extraction of:
  - Offer images
  - Titles, subtitles, and quantities
  - Categories
  - Validity periods
  - Prices (old price, new price, discount)
- Intelligent search bar with support for:
  - Product name (e.g., `Roșii ciorchine`)
  - Product category (e.g., `legume`)
  - Store name (e.g., `Kaufland`)
- Results are clickable and redirect the user to the specific store page where the offer belongs

---

### 2. Favorite Offers & AI Recipe Generation

Users can mark offers as favorites using a heart-shaped button.

When an offer is added to favorites:

- It is stored in a **personal favorites list** for that user
- A **custom recipe** is automatically generated using the **OpenAI API**

**Recipe contents:**

- Recipe name
- Ingredient list (including the product from the offer)
- Step-by-step cooking instructions

Each favorite offer is associated with its own generated recipe, which can be viewed on the favorites screen.

---

### 3. Thematic Shopping Baskets

SnapSale introduces **predefined shopping baskets** that group products around a particular theme (e.g., breakfast baskets, party baskets, weekly basket, etc.).

There are two levels of baskets:

1. **Store-specific baskets**  
   - Each store has predefined basket types
   - For each basket type, a subset of categories is selected (relevant for that theme)
   - Products are chosen randomly from these categories (or all products if below a threshold)
   - The basket contains a set of offers that match the theme

2. **Global baskets**  
   - Built by combining similar basket types across all stores
   - Designed to offer a more general, cross-store shopping experience

A validation step (using the OpenAI API) can be used to check whether a basket respects the intended theme.

---

### 4. Store Localization & Navigation

SnapSale integrates location and navigation functionality using:

- **Google Maps SDK**
- **Google Places SDK**
- **Google Directions API**

**Capabilities:**

- Determine user’s current location (FusedLocationProviderClient)
- Search locations by:
  - Full address
  - Place name (e.g., `"Universitatea din București"`)
  - Partial names (e.g., `"universitate"`)
- Choose a location as a reference point
- From the reference point:
  - Find the nearest store of a selected chain (Kaufland, Lidl, Carrefour, Penny)
  - Display the most efficient route to that store
  - Navigate across nearby stores using markers or UI arrows
- Optionally connect to the Google Maps app for turn-by-turn navigation

---

### 5. User Accounts & Authentication

SnapSale supports two user types:

- **Registered users**
  - Created with email & password
  - Authenticated via Firebase Authentication
  - Data (favorites, baskets, etc.) is tied to their account

- **Anonymous users**
  - Can use most of the app without creating an account
  - When they decide to register, the anonymous account can be linked to an email/password pair (no data loss)

Authentication features include:

- Register with email & password
- Login with email & password
- Anonymous sign-in
- Upgrade anonymous account to registered account
- Logout

---

## Architecture

### MVC (Model–View–Controller)

SnapSale follows an **MVC architecture**, splitting the app into:

- **Models** – data structures and business rules  
- **Views** – XML layouts and UI components  
- **Controllers (Activities/Adapters/Services)** – coordination between data and UI  

---

### Models

Key model classes:

- `User`  
  - Stores user-related data (e.g., username)

- `Store`  
  - Represents a commercial store and maps its categories

- `Category` (abstract)  
  - Base class for store categories (name + validity period)

- `StoreCategory` (extends `Category`)  
  - Adds a mapping of offers for a specific store category

- `FavoredCategory` (extends `Category`)  
  - Represents a category consisting only of favorited offers

- `Sale`  
  - Represents a promotional offer
  - Attributes: store, category, image, title, subtitle, quantity, old price, new price, discount, validity period

- `FavoredSale` (extends `Sale`)  
  - Adds: time when favorited, and associated recipe

- `Recipe`  
  - Recipe name
  - List of ingredients
  - List of instructions

- `Basket`  
  - Predefined shopping basket
  - Attributes: basket name, type, validity, and the set of offers included

**Manager classes** (e.g., `FirebaseManager`, `FirebaseAuthenticationManager`) centralize access to:

- Firebase Authentication
- Firebase Realtime Database
- References for stores, users, favorites, baskets

**Repository classes** (e.g., `UsersRepository`, `SalesRepository`, `BasketsRepository`) implement the logic for:

- Fetching data
- Adding new records
- Updating records
- Removing records  

This separation ensures a cleaner architecture and easier maintenance.

---

### Views (UI)

Views are defined in XML and use the Android UI toolkit.  

Common containers:

- `LinearLayout`, `RelativeLayout`
- `RecyclerView` with `LinearLayoutManager` and `GridLayoutManager`
- `NestedScrollView`, `HorizontalScrollView`
- `CardView` for card-style product display
- `DrawerLayout` and `NavigationView` for side navigation
- `CustomNavigationView` – extended navigation component with more menu items

Key screens include:

- **Authentication**
  - `activity_sign_up.xml` – registration screen
  - `activity_login.xml` – login screen

- **Home**
  - `activity_home.xml` – main page for logged-in users
  - `activity_home_guest.xml` – main page for anonymous users
  - Components:
    - Search bar with autocomplete
    - Buttons for each store
    - Buttons showing counts for favorites and baskets
    - Side navigation drawer + bottom navigation

- **Location**
  - `activity_location.xml` – store locator for authenticated users
  - `activity_location_guest.xml` – store locator for anonymous users
  - Contains map fragment + buttons for each store type

- **Store views**
  - `activity_<store>.xml` – store-specific page with category sidebar + offers list
  - Layouts:
    - `<store>_progress_layout.xml` – loading state while fetching offers
    - `<store>_no_sales_layout.xml` – empty-state UI
    - `<store>_sale_layout.xml` – offer cards with heart button to favorite

- **Favorites**
  - `activity_favorites.xml`
  - `favorites_store_layout.xml` – favorite offer layout
  - `favorites_store_recipe_layout.xml` – recipe display
  - `favorites_store_recipe_ingredients_layout.xml`
  - `favorites_store_recipe_instructions_layout.xml`

- **Baskets**
  - `activity_baskets.xml` – for authenticated users
  - `activity_baskets_guest.xml` – for anonymous users
  - `baskets_general_layout.xml` – general basket card
  - `baskets_<store>_layout.xml` – detailed basket view per store
  - `baskets_<store>_sale_layout.xml` – offer layout inside a basket

- **Profile**
  - `activity_profile.xml` – user profile (username, email, password change, logout)
  - `activity_profile_guest.xml` – guest profile with redirect to registration

---

### Controllers & Services

Main responsibilities:

- Activities handle user interaction and update views
- Repositories/services handle:
  - Firebase access
  - Web scraping (Jsoup)
  - AI recipe generation (OpenAI API)
  - Location and route calculation (Maps, Places, Directions APIs)
- Callbacks and async tasks are used for background operations and updating the UI once data is loaded

---

## Tech Stack

**Language & Platform**

- Android (Java)
- Android Studio
- Gradle build system

**Backend & Data**

- Firebase Authentication
- Firebase Realtime Database (NoSQL JSON-based storage)

**Networking & Web Scraping**

- `OkHttpClient` – HTTP requests
- `Jsoup` – HTML parsing and web scraping

**AI Integration**

- OpenAI API (GPT-based model) for recipe generation

**Maps & Location**

- Google Maps SDK
- Google Places SDK (Autocomplete, Nearby Search)
- Google Directions API (routes, distance, duration)

---

## Screenshots

### Home

<p float="left">
  <img width="400" alt="autocomplete" src="https://github.com/user-attachments/assets/06577588-66f5-4426-aaa8-4d0acdb66e20" />
  <img width="400" alt="autocomplete_results" src="https://github.com/user-attachments/assets/6242ddfd-38dd-4aae-9f86-9e9467ab70f2" />
  <img width="400" alt="favorites" src="https://github.com/user-attachments/assets/db293557-32fd-421c-91ce-09c3f7e9d4fa" />
  <img width="400" alt="baskets" src="https://github.com/user-attachments/assets/fc732f3f-dda7-4cf2-9095-1c6c815e3a80" />
</p>

### Location

<p float="left">
  <img width="400" alt="autocomplete" src="https://github.com/user-attachments/assets/52176cd6-034f-4f9a-b427-7ce98bf82f91" />
  <img width="400" alt="autocomplete_results" src="https://github.com/user-attachments/assets/1f09fe6c-a412-45a5-9992-22a41daf7b8e" />
  <img width="400" alt="favorites" src="https://github.com/user-attachments/assets/e38b7792-ec80-4664-87fb-ca38a206cd73" />
  <img width="400" alt="baskets" src="https://github.com/user-attachments/assets/8064507f-92cb-4532-abea-66d9310148b7" />
</p>

### Search

<p float="left">
  <img width="400" alt="autocomplete" src="https://github.com/user-attachments/assets/ea297bc5-a0e7-444c-8481-ef15dd2d95c0" />
  <img width="400" alt="autocomplete_results" src="https://github.com/user-attachments/assets/9956bea3-6f42-4235-a753-992c1dc4f11f" />
  <img width="400" alt="favorites" src="https://github.com/user-attachments/assets/f8834d27-1819-4e5d-9ec3-bf823a1cfa64" />
  <img width="400" alt="baskets" src="https://github.com/user-attachments/assets/ee6ebd54-8903-4d50-917b-d3743eeb2bee" />
</p>

### Favorites

<p float="left">
  <img width="400" alt="autocomplete" src="https://github.com/user-attachments/assets/52120520-dbd6-447d-9e07-d2c39abdd217" />
  <img width="400" alt="autocomplete_results" src="https://github.com/user-attachments/assets/5bd2468f-28ee-4ce5-b9b3-c058b4e74142" />
  <img width="400" alt="favorites" src="https://github.com/user-attachments/assets/df824965-1fe8-4caf-9bcb-025080ddb956" />
  <img width="400" alt="baskets" src="https://github.com/user-attachments/assets/3f5fb4cb-b5d9-40e3-8b23-2c127b4be43c" />
</p>

### Baskets

<p float="left">
  <img width="400" alt="autocomplete" src="https://github.com/user-attachments/assets/b5cb2b7a-eb5d-4c87-83d1-64ea37a97a26" />
  <img width="400" alt="autocomplete_results" src="https://github.com/user-attachments/assets/d3741fc5-311d-4fe7-bda9-57dd195c1810" />
  <img width="400" alt="favorites" src="https://github.com/user-attachments/assets/f1d2d293-6f56-4e68-a3e7-94d1783e60a0" />
  <img width="400" alt="baskets" src="https://github.com/user-attachments/assets/fa5c1ddf-cd45-4cfb-b4f1-e9d37ce65e4d" />
</p>
