# Spring Data Elasticsearch Standalone Module

This repository hosts a standalone module leveraging Spring Data elasticsearch, providing a seamless integration for elasticsearch with your Spring-based applications. This module provides abstractions and utilities for working with elasticsearch repositories, making it easier for developers to perform common operations such as indexing, searching, and aggregating data.


### Features
This standalone module includes implementations of various basic Elasticsearch queries, which are essential for interacting with Elasticsearch. The features include:

 **Bulk Operations:**
- Bulk Insert
- Bulk Update
- Bulk Delete

 **Search Queries:**
- Term Query
- Wildcard Query
- Match Query
- Match Phrase Query
- Match Phrase Prefix Query
- Searching with pagination

### Prerequisites

- Java 17 or later
- Maven or Gradle (for building and managing dependencies)
- An Elasticsearch instance (local or remote) for integration testing

### Installation or How to use

To use spring-data-elasticsearch standalone module, in your Spring Boot application, simply add the following dependency to your pom.xml:

```
<dependencyManagement>
		<dependency>
		    <groupId>com.milton</groupId>
			<artifactId>spring-data-elastic-search</artifactId>
		    <version>${version}</version>
		</dependency>
</dependencyManagement>
```
and in dependency

```
<dependency>
    <groupId>com.milton</groupId>
	<artifactId>elastic-search</artifactId>
    <version>${version}</version>
</dependency>

```

To ensure Spring Boot can scan the components, configurations, and repositories defined in the standalone Elasticsearch module, you need to specify the base packages in your Spring Boot application's configuration

```
@SpringBootApplication
@ComponentScan(basePackages = {"your-application-base-package","com.milton.elasticsearch"})
@Slf4j
public class MySpringBootApplication {
 public static void main(String[] args) {
        SpringApplication.run(MySpringBootApplication.class, args);
    }
}

```
Create a service class named YourService or whatever you wish that will be responsible for the business logic using the EsHelper.

```
@Service
public class YourService {

    @Autowired
    private EsHelper esHelper;

    // Your business logic using the repository
}
```


**References:**

For more detailed information on Elasticsearch, you can visit the official [Elasticsearch Documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)

Additionally, you can visit this blog post for a guide on [Getting Started with Spring Boot and Elasticsearch](https://codecrafting1101.blogspot.com/2024/03/getting-started-with-spring-boot-and.html)