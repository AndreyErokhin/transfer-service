# transfer-service
Test assignment for the fintech company. The rest API for money transfers between accounts.
Using the Spring framework is forbidden.

To implement the service I used Drpwizard frameweork. More information you can on the project page: https://www.dropwizard.io/

### Remarks.
I tried to use as less frameworks as possible and as much pure JAVA as possible.

There is only 1 integration test in the project:
   `transfer-service/src/test/java/nl/revolut/TransferServiceApplicationTest.java`
I added this test as an example how I would do the testing. But In general API and services are covered by tests.

The Account API I added for convenience. I didn't spend much time on checking the concurrency issues and other potential drawbacks.   

There are some features that are missing, like: explanation why transfer is failed, syncronous transfer execution, adittional verification of the input data, API documentation. But this can be easily added if there is such a need.  

## How to build the project.
1. Clone or download the project.
2. Execute gradle task `distZip` using gradle wrapper.
  Linux:
  ```
  ./gradlew distZip
  ```
  Windows:
  ```
  gradlew.bat distZip
  ```
  3. Navigate to ${project-directory}/build/distributions
  4. Unzip the archive `transfer-service-1.0-SNAPSHOT.zip` to some destination directory.
  5. Navigate to the destination directory.
  6. Navigate to the `bin` folder and execute the startup script `transfer-service` or `transfer-service.bat` depends on which operating system are you trying to run the service.

Script takes two parameters:
```
./transfer-service server ~/git/transfer-service/src/main/resources/config.yml
```
First parameter should be `server` like in example above. Second parameter is the path to the application configuration file.
The example of the configuration file is in the ${project-directory}/src/main/resources/config.yml.

After that server should start and you should be able to access the service on your localhost.

The initial accounts can be added through the configuration file or through the Accounts API.
