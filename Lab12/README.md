# Assignment Report  
**Assignment 1**  
**Alessio Tommasi**

**GitHub Repo**:  
[https://github.com/AlessioTommasi-supsi/USI_MobileWearableComputing/tree/main/Lab12](https://github.com/AlessioTommasi-supsi/USI_MobileWearableComputing/tree/main/Lab12)

---

## **Components of Android Application**

### **Activity**  
An **Activity** represents a single screen with a user interface.  
**Example from Lab01**:  

- **Fig1 Location**: `/res/layout/activity_main.xml`
- **Fig2 Location**: `/java/com/MainActivity.java`

In the code, the Activity is linked in Java at line 27 during the `onCreate` method to ensure correct setup and usage.

---

### **Service**  
A **Service** handles background operations without a user interface.  
We did not use Services in Lab01, but in a Twitter-like app, they could be used for synchronizing notifications or downloading new tweets.

---

### **Broadcast Receiver**  
A **Broadcast Receiver** allows the app to respond to system events or events from other apps.  
We didn’t use this in Lab01 either, but an example could be limiting resource usage when the battery is low or resetting the counter when the app is put in the background.

---

**Content Provider**: Allows the sharing of data between different applications. Content providers provide a standard interface for accessing data, such as contacts or media, that can also be used by other apps.


