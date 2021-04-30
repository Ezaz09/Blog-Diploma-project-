# Dev social network project
This is mini social network for developers.
You can use this project, how example, like a place where you can offer and discuss some ideas.

This project use spring boot security, so you and your team don't gonna see some anonymous offers and comments.

And you can register some moderators, which is decides what kind of offers can be published.

# Libraries
- backend
    - JDK 1.8
    - Lombok 1.18.10
    - Spring Boot
    - Liquibase
    - MySQL
    - Cage - CAptcha GEnerator Java Library
- frontend 
    - Vue.js

# How to run 
- Initialize next environment variables: 
    - GMAIL_USERNAME
    - GMAIL_PASSWORD
    
If you don't want to safe photos on server, You need initialize "CLOUDINARY_URL" and change every call "uploadImageOnServer" to "uploadImageOnCloudinary".

- After a download, write in "application-dev.properties" url path to your database, username and password.
This needs to made how for spring.datasource properties and as well as for spring.liquibase properties.   
- Write properties for "Email service" in "application-dev.properties". It's needs for sends restore password links to users. 
I'm using for this gmail, so if you wanna used for this something else, don't forget to rewrite other properties, like  "spring.mail.host". 
- Change in "application.properties" parameter "spring.profiles.active" from prod to dev, if it's needed.

# Link to deploy 
https://blog-diploma-project.herokuapp.com
