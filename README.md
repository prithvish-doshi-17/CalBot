[![Java CI with Maven](https://github.com/prithvish-doshi-17/CalBot/actions/workflows/maven.yml/badge.svg)](https://github.com/prithvish-doshi-17/CalBot/actions/workflows/maven.yml)
[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.5528822.svg)](https://doi.org/10.5281/zenodo.5528822)
[![Made with](https://img.shields.io/badge/Made%20with-Java-blue)](https://www.java.com/en/)
[![License: MIT](https://img.shields.io/badge/license-MIT-green)](https://github.com/elric97/CalBot/blob/master/LICENSE)
[![Platform](https://img.shields.io/badge/platform-discord-blue)](https://discord.com/)
[![GitHub issues open](https://img.shields.io/github/issues/prithvish-doshi-17/CalBot)](https://github.com/prithvish-doshi-17/CalBot/issues)
[![GitHub issues closed](https://img.shields.io/github/issues-closed/prithvish-doshi-17/CalBot)](https://github.com/prithvish-doshi-17/CalBot/issues)
<img src="./badges/jacoco.svg">
[![Check Style](https://github.com/prithvish-doshi-17/CalBot/actions/workflows/checkstyle.yml/badge.svg)](https://github.com/prithvish-doshi-17/CalBot/actions/workflows/checkstyle.yml)
# aPAS
<p align = center>
  <a href="">
    <img src="https://raw.githubusercontent.com/elric97/CalBot/master/Design/Logo.png" alt="Logo" height="400" width="800"/>
  </a>
</p>

## Introduction 😁
Do you feel overwhelmed sometimes when you have alot of things to do?

"Achieving a small incremental goal is perceived as easier -- and more satisfying -- than maintaining the status quo" --[Source](https://www.sciencedaily.com/releases/2018/11/181108142313.htm)

The agile sprint model follows something on similar lines to this above statement. So following this, we thought of bringing agile sprint model to personal life.
aPAS takes the [unscheduled events](https://github.com/elric97/CalBot/wiki/Algorithm-to-compute-tasks-for-each-day) at beginning of each week as input and divide the task on user's plate on daily basis, by which he/she can easily able to finish tasks just before deadlines and it won't be overwhelming. 

This model gives the true picture of how much of an assignment load someone has and how to further manage it. We bring to you exactly that to manage your everyday tasks with our discord bot.
Do you ever want to find time for Leetcode and workouts amidst a very busy week full of assignments but just can’t? Our discord bot will show you most optimised things to do each day to complete them and still find some time for unplanned activities.

#### Here's a short introduction video:
https://user-images.githubusercontent.com/55187770/135385469-924f4ded-4768-4bac-9a7b-daf0bbff46e1.mp4

#### Quick read
[Project Vision](https://github.com/elric97/CalBot/wiki/Project-vision)

[Algorithm](https://github.com/elric97/CalBot/wiki/Algorithm-to-compute-tasks-for-each-day)

[Design Docs explained](https://github.com/elric97/CalBot/wiki/Design-Docs)

[Developer's Guide](https://elric97.github.io/CalBot/)

## Commands
* **!oauth** - To get the authentication link for calendar
* **!event** - To display a list of upcoming events on your calendar
* **!add <!title> <!hours needed> <!deadline mm/dd/yyyy>** - To add events to your calendar
* **!show** - To show events in the upcoming week and display time needed to be dedicated to each event today
* **!update <!title> <!new hours needed> <!new deadline mm/dd/yyyy>** - To update an already existing event with new number of hours and/or new deadline
* **!delete <!title>** - To delete an event from your calendar 

## Technologies
<p>
  <a href="https://www.java.com/en/"> 
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" alt="java" width="35" height="35"/>
  </a>
  <a href="https://spring.io/"> 
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" alt="spring" width="35" height="35"/>
  </a>
  <a href="https://www.postgresql.org/"> 
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" alt="postgresql" width="35" height="35"/>
  </a>
  <a href="https://www.docker.com/">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" alt="docker" height="35" width="35">
  </a>
  <a href="https://maven.apache.org/">
    <img src="https://symbols.getvecta.com/stencil_74/18_apache-maven-icon.2a3ad94f03.svg" alt="maven" height="35" width="35">
  </a>
  <a href="https://discord4j.com/">
    <img src="https://raw.githubusercontent.com/Discord4J/discord4j-web/master/public/logo.svg?sanitize=true" alt="Discord4J" height="35" width="35"/>
  </a>
</p> 

Java\
Spring Boot\
PostgreSQL\
Docker\
Maven\
Discord4J



## Getting started
* Make sure you have the following things installed
  * Java 8
  * IntelliJ or Eclipse
  * PostgreSQL
  * Docker (optional)
* Google Cloud Platform - We used Google calendar API by creating a project on [GCP](https://developers.google.com/workspace/guides/create-project), Check [Quickstart](https://developers.google.com/calendar/api/quickstart/java) for setting up Google calendar APIs. The ./Credentials.json of the project is required for authenticating the user.
* Discord Developer Application -We created a [Discord Developer profile](https://discord.com/developers/docs/intro) for configuring a Discord BOT 
* Environment variables can be found at [applications.properties](/src/main/resources/application.properties) and Credentials need to be updated at [credentials.json](/src/main/resources/credentials.json)
* The user needs to create a server on Discord, and then visit this [link](https://discord.com/api/oauth2/authorize?client_id=904864050691854367&permissions=0&scope=bot%20applications.commands) to authorise the Discord bot to get added to that server
* (Optional) To start project using docker run the following command in main directory
````
docker build . -t 'image-name' //Don't forget the dot
docker run -dp 8080:8080 'image-name' //This will start app on port on 8080
````
* [Developer Guide](https://elric97.github.io/CalBot/) 

#### Here's a demo video showing how to initialize the bot and the usage of all commands
https://user-images.githubusercontent.com/43064854/140317989-1e683eda-211d-4806-8e72-e88097eb6aa1.mp4


## Future Scope 🐾
There are multiple dimensions to this project catering to the interests of
Various developers. Please check [CONTRIBUTING.md](./CONTRIBUTING.md) and [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) for contributing rules


### If you are a front-end developer 💻
Currently aPAS has been configured to work with Discord as the GUI but given the structure of the project it can be easily extended for other platforms such as
* Telegram
* WhatsApp
* Facebook 

### If you are a back-end developer 🛠️
Why just stop at Google calendar? Why not make it more user-friendly for all users with various calendars such as:
* Samsung Calendar
* Apple calendar
Extending aPAS for these will require addition of service classes for these calendars

### If you are an ML Enthusiast 🧑‍🔬
* Why not use a voice-bot to set and manage events,Implementing this using an audio model can be an interesting extension to the project.
* The priority algorithm can be personalized for a user based on the amount of time they spend on a particular task.

### If you want to have a hang of DevOps
* Configure deployment on Heroku
* Add more code analysis tools like SonarQube, etc

## Updates from previous version
* Completed algorithm implementation for scheduling events
* Added functionality to delete an event
* Added functionality to update an existing event with new number of hours and/or new deadline
* Added functionality to stop users from creating multiple events of the same name
* Implementation of token authentication
* Added code coverage tools and testing support
* Added unit tests to files and made them routinely execute on each push to master branch

## Team Members
* Chao-Ting Hung
* Prithvish Doshi
* Seeya Prabhumoni
* Shao-Yo Chao
* Vraj Chokshi
