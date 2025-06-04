# Dispatch Management System Spring Boot application

This is the Spring Boot application for management of dispatching activities.

## Setting up the application locally

### Setting up the Docker containers

A lot of the underlying technologies used in this application (such as ***PostgreSQL*** database) are set up using
Docker. First of all, you will have to locally install Docker (you can navigate to [Docker](https://www.docker.com/)
official website for more details about installation). After installing Docker, make sure you are in the source folder
of the application (namely in the *dispatch-management-system-core* folder). Then, you can run the following command:
`
docker compose up
`

### Setting up the Dev profile

For local development, a dedicated **dev** profile was created to bypass some checks and to add
configurations specific to local development (such as connection to a local database, for example).
This is why, prior to starting the application, we need to set up the **dev** profile. The setting will be done
through an environment variable.

*IntelliJ IDEA* IDE was used to develop this application, so an example on doing this will be provided for
IntelliJ, but the approach should be similar to any other IDE or code editor.
Here are the steps to follow:

* Navigate to ***src/main/java/io.kovin.dispatch.management.system.DispatchManagementSystemCoreApplication*** class
* To the left of the name of the class should be a play button; right click on cucumber
* Click on **Modify Run Configuration...**
* Go to the **Environment variables** input form and click on that file-like button with three lines on cucumber (or you
  can use the *Shift+Enter* combination)
* In the *name* column of the matrix add **SPRING_PROFILES_ACTIVE** and in the name *value* column add **dev**
* Apply the changes and run the application

Now, the application is using the **dev** profile we have just set.

### Locally connect to the database

***DBeaver*** was the database management platform of choice for connecting to the database. The interface
is pretty intuitive, so you will have to create a new connection (this has to be done regardless of the platform
you are using). For local development, here is the required data to connect to the database:

| Property      | Value     | 
|---------------|-----------|
| Username      | Kovin     |
| Password      | Kovin     |
| Port          | 54322     |
| Database name | kovin_dms | 