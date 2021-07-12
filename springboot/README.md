Projects created by start.spring.io contain Spring Boot, a framework that makes Spring ready to work inside your app, but without much code or configuration required. Spring Boot is the quickest and most popular way to start Spring projects. If you have any questions, please contact zdu.strong@gmail.com.

## Development environment setup
1. From https://adoptium.net install java v11, and choose Entire feature.<br/>
2. From https://code.visualstudio.com install Visual Studio Code. Next, install extension "Extension Pack for Java" and "IntelliCode".<br/>
3. From https://dev.mysql.com/downloads/installer install MySQL, the password of the root user is set to 123456.

## Available Scripts

In the project directory, you can run:

### `./mvn clean spring-boot:run`

Let’s build and run the program. Open a command line (or terminal) and navigate to the folder where you have the project files. We can build and run the application by issuing the command.

The last couple of lines here tell us that Spring has started. Spring Boot’s embedded Apache Tomcat server is acting as a webserver and is listening for requests on localhost port 8080. Open your browser and in the address bar at the top enter http://localhost:8080. You should get a nice friendly response like this:
"Hello, World!"

### `./mvn clean package`

To generate executable jar package

### `./mvn clean test`

Run unit tests

### `./diff`

Generate database version upgrade sql.<br/>
Does not support diff comparison to modify the field type and field length.<br/>

Its source code is in the ./.mvn/diff folder.<br/>

### `./mvn clean compile liquibase:dropAll`

Delete all tables in the development database

### `./mvn versions:display-dependency-updates`

Check that a new version of the dependency is available<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
    </dependency>

## Notes - Things to note

Some experience in use, if you already know it, you can skip it.
1. Before the new JPA entity, query all the required data and save the previous entity.
2. Jinq does not support nested select statements(select * from (select * from user)).
3. Jinq does not support union and union all.
4. Jinq doest not support right join.
5. Jinq doest not support outer join.

## Notes - jinq - getOnlyValue

get only one element, like this:

    this.UserEmailEntity().getOnlyValue();

## Notes - jinq - findFirst

get the first element, like this:

    this.UserEntity().findFirst();

## Notes - jinq - toList

get array

    this.UserEntity().toList();

get model array

    public List<String> getAllUsernameList() {
        return this.UserEntity().map(s -> this.userFormatter.format(s)).collect(Collectors.toList());
    }

## Notes - jinq - where

    this.UserEntity().where(s -> s.getUsername().equals("tom"));

## Notes - jinq - and

    this.UserEmailEntity().where(s -> s.getEmail().contains("jerry") && s.getUser().getUsername().contains("tom"));

## Notes - jinq - or

    this.UserEntity().where(s -> s.getUsername().contains("tom") || s.getUsername().contains("jerry"));

## Notes - jinq - or condition of array

    public long getUsers(List<String> names) {
        JPAJinqStream<UserEntity> stream = this.UserEntity();
        JPAJinqStream<UserEntity> streamOne = stream;
        for (String name : names) {
            JPAJinqStream<UserEntity> streamTwo = streamOne.where(s -> s.getUsername().contains(name));
            stream = stream == streamOne ? streamTwo : stream.orUnion(streamTwo);
        }
        return stream.count();
    }

## Notes - jinq - inner join

    this.UserEmailEntity().join(s -> JinqStream.of(s.getUser()));

    this.UserEntity().joinList(t -> t.getUserEmailList());

    this.UserEntity().join((s, t) -> t.stream(UserEmailEntity.class))
        .where(s -> s.getOne().getId().equals(s.getTwo().getId()));

## Notes - jinq - left join

    this.UserEmailEntity().leftOuterJoin(s -> JinqStream.of(s.getUser()));

    this.UserEntity().leftOuterJoinList((s) -> s.getUserEmailList());

    this.UserEntity().leftOuterJoin((s, t) -> t.stream(UserEmailEntity.class),
        (s, t) -> s.getId().equals(t.getId()));

## Notes - jinq - pagination

    JPAJinqStream<UserEntity> stream = this.UserEntity();
    return new PaginationModel<>(1, 10, stream, (s) -> s.getUsername());

## Notes - jinq - group by

Group by id, username

    var stream = this.UserEntity().group(s -> new Pair<>(s.getId(), s.getUsername()), (s, t) -> t.count());
    return new PaginationModel<>(1, 15, stream, (s) -> {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", s.getOne().getOne());
        map.put("username", s.getOne().getTwo());
        map.put("countGroup", s.getTwo());
        return map;
    });

## Notes - jinq - order by

Sort by username first, then by id

    this.UserEntity().sortedBy(s -> s.getId()).sortedBy(s -> s.getUsername());

## Notes - jinq - Use subqueries in where

    this.UserEntity().where((s, t) ->
        t.stream(UserEmailEntity.class).where(m -> m.getUser().getId().equals(s.getId())).count() > 0
    );

## Notes - jinq - Format entity to model

All data can be obtained and set to the model, support any structure

    public UserModel format(UserEntity userEntity) {
        var email = JinqStream.from(userEntity.getUserEmailList()).select(s -> s.getEmail()).findFirst().orElse("");
        var userModel = new UserModel().setId(userEntity.getId()).setUsername(userEntity.getUsername()).setEmail(email);
        return userModel;
    }
    
## Notes - Params date


	URI url = new URIBuilder("/abc").setParameter("date", DateTimeFormatter.ISO_INSTANT.format(new Date().toInstant())).build();
	ResponseEntity<Object> response = this.testRestTemplate.getForEntity(url, Object.class);
	Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
	
	@GetMapping("/abc")
	public ResponseEntity<?> abc(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date date) {
		return ResponseEntity.ok().build();
	}

## Learn More

1. Jinq (http://www.jinq.org/docs/queries.html)
