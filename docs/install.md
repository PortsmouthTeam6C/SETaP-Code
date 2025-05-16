## Installation and Setup Guide

### Dependencies
- Install NodeJS and npm from the [official website](https://nodejs.org/en/download) or from your package manager
- Install Java from [OpenJDK](https://jdk.java.net/24/), from [Oracle](https://www.oracle.com/uk/java/technologies/downloads/), or from your package manager
- Install Maven from the [official website](https://maven.apache.org/install.html) or from your package manager
- Install git from the [official website](https://git-scm.com/downloads) or from your package manager

**Clone the Repository**

```bash
git clone https://github.com/PortsmouthTeam6C/SETaP-Code.git
```

### Frontend
**1. Navigate to the correct directory:**
```bash
cd Frontend
```

**2. Install dependencies:**
```bash
npm install
```
**3. Run the application:**
```bash
npm run dev
```

### Backend
```bash
mvn clean install
mvn compile
mvn exec:java -Dexec.mainClass="uk.ac.port.setap.team6c.Main"
```
