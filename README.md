# Test Helper
Test Helper library is a Spock Framework extension written in Java 11 and Groovy 3. Library aim is to easily setup test environment while working with MongoDB and Google Pub Sub Emulator using Testcontainers or local instances.
It can be used with both Spring Boot and Micronaut based applications.

* Add library dependency.
### Micronaut
Gradle
```
testimplementation("pl.mkjb:test-helper:0.1")
testImplementation("org.testcontainers:spock:1.15.1")
testImplementation("org.testcontainers:mongodb:1.15.1")
testImplementation("org.testcontainers:gcloud:1.15.1")
```

### Spring Boot
Maven
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>1.15.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
  <dependency>
      <groupId>pl.mkjb</groupId>
      <artifactId>test-helper</artifactId>
      <version>0.1</version>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.codehaus.groovy</groupId>
      <artifactId>groovy</artifactId>
      <version>3.0.7</version>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.spockframework</groupId>
      <artifactId>spock-core</artifactId>
      <version>2.0-M4-groovy-3.0</version>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.spockframework</groupId>
      <artifactId>spock-spring</artifactId>
      <version>2.0-M4-groovy-3.0</version>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>spock</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>testcontainers</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>mongodb</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>gcloud</artifactId>
      <scope>test</scope>
  </dependency>
</dependencies>
```

## Testcontainers
* Apply @Testcontainers annotation over the class declaration

## Working with MongoDB
* Apply @MongoContainer annotation over the class declaration to use MongoDB in your tests. You can use either mongo from Testcontainers or local instance. 
  By, default Testcontainers is starting fresh MongoDB instance for each test class. If you want to switch to local instance simply apply ```@MongoContainer(testContainers = 'false')```
### Micronaut specific setup
* Your test class must implement ```TestPropertyProvider``` interface to provide database uri to your application.
```
    @Override
    Map<String, String> getProperties() {
        return Map.of(
                MONGODB_URI, db.getReplicaSetUrl(),
        )
    }
```
* Add a field to test class. It must be annotated with ```@Shared```. 
``` 
@Shared
MongoDBContainer db
```
### Spring Boot specific setup


### Populate your database
* To load some test data to your database you can use ```@Populate``` annotation. It allows you to load multiple JSON files to specific database and collection. Source files should be added to ```resources``` directory.
```@Populate``` annotation can be applied on class or a test method. Collections are always dropped before insert new data.
```
@Populate(from = ['source_filename.json'], db = 'db_name', coll = ['collection_name'])
```

## Working with Google PubSub Emulator
* TODO
### Micronaut specific setup
* TODO
### Spring specific setup
* TODO
### Awaitility, Pooling Conditions
* TODO
## Publish new version
* gradle clean publishToMavenLocal
