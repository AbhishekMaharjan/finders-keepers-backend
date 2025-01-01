
# **Project Setup**

## **Prerequisites**

- ***Java***

- ***Spring Boot***

- ***PostgreSQL***

- ***Docker***

- ***Maven***

- ***IDE(IntelliJ)***

## **Version**

- ***Java 17***

- ***Spring Boot 3.1***

- ***PostgreSQL(latest)***

- ***Docker(Latest)***

# **Build**

- For Java, install the suitable jdk in the project structure of the IDE [Java Installation](https://www.jetbrains.com/guide/java/tips/download-jdk/)

- For Spring Boot build, go to [Spring Initializer](https://start.spring.io/) or in a project directory add suitable dependencies in pom.xml file

- For PostgreSQL, install the required drivers and run configuration files or run the database in a docker [PostgreSQL installation](https://www.jetbrains.com/help/idea/running-a-dbms-file.html), for community edition [PostgreSQL installation](https://www.youtube.com/watch?v=JMT5F7kQIGs)

- For Docker, install docker using [Docker Installation](https://docs.docker.com/engine/install/ubuntu/)

# **Start**

- For maven build `mvn clean` and  `mvn install`

- To start docker container
  ```bash
     docker-compose up -d
   ```

- To stop the docker service run
  ```bash
     docker-compose down
  ```

- To see running docker services
    ```bash
       docker ps
    ```

  


