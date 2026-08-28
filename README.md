# Hikvision Attendance Fetcher

A Java-based desktop application designed to securely connect to Hikvision access control and facial recognition terminals (e.g., DS-K1T320EFX) via ISAPI. The application fetches employee attendance logs (In/Out times), stores them in a local SQLite database for offline access, and allows HR administrators to export the records to Excel for payroll processing.

## 🚀 Features
* **Direct Device Integration:** Connects to Hikvision terminals over the local network using the ISAPI protocol and Digest Authentication.
* **Custom Date Range Filtering:** Intuitive UI with interactive date pickers to fetch logs for specific timeframes.
* **Offline Data Support:** Uses a local SQLite database to cache fetched records, ensuring data is instantly available upon app launch even without a network connection.
* **Duplicate Protection:** Safely handles multiple API fetches without duplicating records in the database.
* **Excel Export:** One-click export of attendance data to `.xlsx` format using Apache POI, perfect for HR and payroll systems.
* **External Configuration:** IP addresses and credentials are read from an external `config.properties` file, allowing users to update device details without modifying or recompiling the code.

## 🛠️ Tech Stack
* **Language:** Java (JDK 21)
* **UI Framework:** Java Swing
* **Build Tool:** Maven
* **Core Libraries:**
  * `Apache HttpClient 5` (Network requests & Digest Authentication)
  * `Gson` (JSON parsing)
  * `SQLite JDBC` (Local database management)
  * `JCalendar` (UI Date Picker)
  * `Apache POI` (Excel export generation)

## ⚙️ Prerequisites
To build and run this project from the source code, you will need:
* Java Development Kit (JDK) 21 installed.
* Apache Maven installed.
* A Hikvision terminal connected to the same local network (Ensure ISAPI is enabled on the device).

## 🚀 Getting Started

### 1. Configuration Setup
The application relies on an external configuration file. Create a file named `config.properties` in the root directory (or in the same folder as your executable) and add your device details:

```properties
device.ip=xxxxxxxxxx
device.username=admin
device.password=your_device_password
```

### 2. Running Locally (Development)
You can run the application directly from your IDE (e.g., IntelliJ IDEA) by executing the main method inside `com.attendance.MainUI`. Ensure dependencies are loaded via Maven before running.

### 3. Building the Executable JAR
To create a standalone "Fat JAR" containing all dependencies, run the following Maven command in your terminal:

```bash
mvn clean package
```

This will generate a file named `HikvisionAttendanceFetcher-1.0-SNAPSHOT-jar-with-dependencies.jar` inside the `target/` directory.

### 4. Creating a Windows .exe (Optional)
For client deployment, it is recommended to wrap the generated Fat JAR into a Windows executable using Launch4j.

1. Open Launch4j.
2. Set the **Output file** to `HikvisionAttendance.exe`.
3. Set the input **Jar** to your compiled Fat JAR.
4. Set the **Minimum JRE version** to `21`.
5. Build the wrapper.

> **Note:** Distribute the `.exe` file alongside the `config.properties` file for the application to function correctly.

## 📖 Usage Guide
1. Ensure the `config.properties` file is correctly configured with your device's IP and credentials.
2. Launch the application. Any previously fetched offline data will load automatically into the grid.
3. Select your desired **Start Date** and **End Date** using the calendar inputs.
4. Click **Fetch Attendance** to connect to the Hikvision terminal and download new logs.
5. Click **Export to Excel** to save the currently displayed grid data as an `.xlsx` file for your records.

## 🔒 Security Note
Do not commit your real `config.properties` or `attendance.db` (SQLite database) files to version control. They are ignored in this repository via `.gitignore` to prevent leaking sensitive device credentials and employee attendance logs.
