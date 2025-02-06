## Getting started
### Configure the PSQL database on your local machine
1. Install postgresql from your OS's package manager
   1. Take note of the port, your username and password when you set them. The default port is 5432
2. Create a database and take note of the name: `create database setap;`
3. Create a file named `.env` in `/src/main/resources/` and fill out the file with the information you entered earlier:
```dotenv
DB_USER=your_username_here
DB_PASS=your_password_here
DB_PORT=your_database_port_here
DB_NAME=your_database_name_here
```
4. Run `DatabaseManager.initializeDatabase();` in the main method to set up all tables and relationships.
