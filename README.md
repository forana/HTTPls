# Please
[![Circle CI](https://circleci.com/gh/forana/Please.svg?style=svg)](https://circleci.com/gh/forana/Please)

[Requests](http://docs.python-requests.org/en/latest/) for Java. Or, more accurately, a spiritual port of it. Because making HTTP requests shouldn't be painful (Java, _Please_).

With `Please`:

```java
HTTPResponse response = Please.get("http://alexforan.com/hello.txt").send();
System.out.println(response.getStatus()); // 200
System.out.println(response.getContentType()); // "text/plain"
System.out.println(response.getBodyText()); // "hello world\n"
```

Without `Please`, [it gets a lot nastier](https://github.com/forana/Please/blob/master/src/test/java/com/alexforan/please/ExampleTest.java). Please wraps Apache's HttpClient (the same library as that example), simplifying it and optimizing for common use cases.

(note: there's a similar project, [Unirest](https://github.com/Mashape/unirest-java/), that's focused on REST support - that might be more useful for your use case than this. Check out both!)

**[Full Javadoc Here](http://alexforan.com/Please/latest)**

## Installing

### Maven/Gradle

Artifact = `com.alexforan:please:0.1.0`

Until it's in JCenter/Maven Central (waiting on a response), you'll have to add the repository `http://dl.bintray.com/forana/maven`. Gradle:

```groovy
repositories {
    mavenCentral()
    maven { url 'http://dl.bintray.com/forana/maven' }
}
```

### Jar

[See Releases](https://github.com/forana/Please/releases)

## Example Usages

### Simple GET

```java
Please.get("http://httpbin.org/").send();
```

### GET with Query String

```java
// parameters can be in the URL or added via parameter() calls
Please.get("http://httpbin.org/get?a=b&c=d")
        .parameter("e", "f")
        .parameter("g", 6)
        .send();
```

### Opening an InputStream from a Request

```java
InputStream stream = Please
        .get("http://i.imgur.com/qCIiUWX.jpg")
        .send()
        .getBody();
doSomethingWithStream(stream);
stream.close();
```

### Sending a JSON-Bodied POST and Getting a JSON Response

`Please` integrates with the wonderful [Jackson](https://github.com/FasterXML/jackson) library for JSON.

```java
ObjectNode body = new ObjectNode(JsonNodeFactory.instance);
body.put("username", "falken");
body.put("password", "joshua");

JsonNode response = Please.post("http://httpbin.org/post")
    	.body(body)
    	.send()
    	.getJSON();
```

### POST-ing Form Data

```java
JsonNode response = Please.post("http://httpbin.org/post")
		.body(new Form()
    			.add("username", "falken")
                .add("password", "joshua"))
        .send();
```

### Uploading a File (multipart/form-data)

```java
JsonNode response = Please.post("http://httpbin.org/post")
		.body(new MultipartFormData()
        		.data("file", new File("something.png")))
        .send();
```

### Simpler Response Verification

```java
Please.get("http://httpbin.org/get")
		.sendAndVerify(); // like send(), but throws exception if the status isn't 20X
```

### and more

Take a look [at the tests](https://github.com/forana/Please/tree/master/src/test/java/com/alexforan/please) for more in-depth usage.

**[Full Javadoc Here](http://alexforan.com/Please/latest)**

## License

[Apache](http://www.apache.org/licenses/LICENSE-2.0). This thing is meant to be used.
