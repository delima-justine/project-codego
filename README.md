# 🚨 ResQ PH

**Your Pocket Companion for Emergency Preparedness & Community Support.**

ResQ PH is a centralized mobile application designed to bridge the gap between the public and emergency services. It reduces the time spent searching for emergency contacts while fostering a supportive community where users can share survival experiences and disaster safety tips.

---

## 📖 Table of Contents
- [About the Project](#-about-the-project)
- [Key Features](#-key-features)
- [Target Audience](#-target-audience)
- [Limitations](#-limitations)
- [Tech Stack](#-tech-stack)
- [Development Team](#-development-team)
- [Getting Started](#-getting-started)
- [Contributing](#-contributing)
- [License](#-license)

---

## ℹ️ About the Project

In critical situations, every second counts. **ResQ PH** aims to:
1.  **Reduce Search Time:** Eliminate the panic of searching for hotlines by providing a centralized directory.
2.  **Ensure Accessibility:** Make emergency contacts available even without an internet connection.
3.  **Build Community:** Create a platform for sharing survival stories, tips, and practical advice.

### 🎯 The Goal
While this app acts as an information and sharing platform, its ultimate goal is to **enable responders to take quicker action**. By providing users with instant, verified access to the *correct* local emergency hotlines, we streamline the initial contact process, reducing overall response times.

---

## ✨ Key Features

### 👤 **User Login & Registration**
* Secure sign-up and login functionality.
* Personalized experience for every user.

### 📢 **Emergency Experiences & Sharing Hub**
* A community-driven section where users can share survival stories.
* Post and read tips/advice regarding accidents, earthquakes, floods, and other disasters.
* Foster a helping community base.

### 📰 **News & Updates**
* **Real-Time Information:** Stay updated with the latest disaster news and weather reports.
* **Visual Feed:** Browse articles with images and summaries to stay informed about current events.

### 📍 **Live Location Tracker**
* **Community Tracking:** View the real-time location of other active community members on an interactive map.
* **Safety & Privacy:** Toggle location sharing on or off. See who is active nearby.
* **Map Integration:** Powered by OpenStreetMap for detailed local geography.

### 📞 **Centralized Emergency Contacts**
* A complete, organized list of emergency hotlines.
* Direct access to Police, Fire, Medical, and Rescue teams.

### 📶 **Offline Access**
* **Crucial Feature:** Users can view the emergency contact directory **without** an internet or cellular data connection.
* **Smart Navigation:** Home, News, and Tracker tabs are automatically disabled when offline or not authenticated, ensuring users can always access emergency contacts.
* **Authentication-Based Access:** Certain features require user authentication to ensure community safety and accountability.

### ✏️ **Profile Management**
* Users can customize their profiles (Name, Profile Picture, Bio).

### ℹ️ **About Page**
* In-app documentation regarding the app's purpose and usage guide.

---

## 👥 Target Audience

| Segment | Benefit |
| :--- | :--- |
| **General Public** | Immediate access to reliable hotlines during emergencies and a platform to learn/share survival tips. |
| **Emergency Responders** | Indirectly benefits from streamlined calls. Users calling the *right* department leads to faster coordination and verified information flow. |

---

## ⚠️ Limitations & Disclaimer

* **Information Only:** This application serves as an information and sharing platform. It **does not** directly connect users to emergency responders via the app itself, nor does it provide real-time rescue services.
* **Limited Coverage:** The emergency contact directory initially covers selected regions. We aim to expand this in future updates based on available data sources.
* **Community Dependent:** The effectiveness of the "Sharing Hub" relies on active user participation.
* **Data Privacy:** While basic security measures are implemented, users are advised to be mindful when sharing sensitive personal details in public stories or bios.

---

## 🛠 Tech Stack

![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)
![SQLite](https://img.shields.io/badge/sqlite-%2307405e.svg?style=for-the-badge&logo=sqlite&logoColor=white)

*   **Authentication:** Firebase Authentication
*   **Cloud Database:** Cloud Firestore (Real-time updates)
*   **Local Database:** Room Persistence Library (SQLite)
*   **Maps:** OpenStreetMap (osmdroid)
*   **Networking:** Retrofit & Gson
*   **Image Loading:** Coil

---

## 👥 Development Team

| Icon | Name | Role |
| :---: | :--- | :--- |
| <img src="https://api.dicebear.com/7.x/identicon/svg?seed=Arroyo" width="40" height="40"> | **John Matthew Arroyo** | Backend Developer |
| <img src="https://api.dicebear.com/7.x/identicon/svg?seed=Kath" width="40" height="40"> | **Kathleen Citron** | QA Tester |
| <img src="https://api.dicebear.com/7.x/identicon/svg?seed=Delima" width="40" height="40"> | **Justine Delima** | Project Manager |
| <img src="https://api.dicebear.com/7.x/identicon/svg?seed=Quiambao" width="40" height="40"> | **Patricia Quiambao** | UI/UX Developer 1 |
| <img src="https://api.dicebear.com/7.x/identicon/svg?seed=Ynion" width="40" height="40"> | **Ma. Bea Mae Ynion** | UI/UX Developer 2 |

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
* Android Studio (latest version recommended)
* Java Development Kit (JDK) 11 or higher (usually bundled with Android Studio)

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/your-username/ResQ-PH.git
    ```
2.  **Open in Android Studio**
    * Launch Android Studio.
    * Select "Open an existing Android Studio project".
    * Navigate to the cloned directory and select it.
3.  **Sync Gradle**
    * Android Studio should automatically detect the `build.gradle.kts` files and sync.
    * If not, click "File" > "Sync Project with Gradle Files".
4.  **Run the application**
    * Connect an Android device or start an Emulator.
    * Click the green "Run" button (Play icon) in the toolbar.

---

## 🤝 Contributing

Contributions are what make the open-source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📝 License

Distributed under the [MIT] License. See `LICENSE` for more information.

---

**ResQ PH** — *Empowering communities, one connection at a time.*
