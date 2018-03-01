# Geisha

_Tiny Java MVC Framework._

[> more detail](https://www.nosuchfield.com/2017/11/08/Geisha-Tiny-Java-MVC-Framework/)

## Requirements

* Java8+

## Dependency

Apache Maven

    <dependency>
        <groupId>com.nosuchfield</groupId>
        <artifactId>geisha</artifactId>
        <version>1.0.0-RELEASE</version>
    </dependency>
    
Apache Buildr

    'com.nosuchfield:geisha:jar:1.0.0-RELEASE'
    
Apache Ivy

    <dependency org="com.nosuchfield" name="geisha" rev="1.0.0-RELEASE" />
    
Groovy Grape

    @Grapes( 
    @Grab(group='com.nosuchfield', module='geisha', version='1.0.0-RELEASE') 
    )
    
Gradle/Grails

    compile 'com.nosuchfield:geisha:1.0.0-RELEASE'
    
Scala SBT

    libraryDependencies += "com.nosuchfield" % "geisha" % "1.0.0-RELEASE"
    
Leiningen

    [com.nosuchfield/geisha "1.0.0-RELEASE"]
    
## Example

```java
@Component
@RequestMapping("/person")
public class Hello {

    @RequestMapping("/info")
    public String hello(@Param("name") String name, @Param("age") String age) {
        return "hello " + name + ", your age is " + Integer.valueOf(age);
    }

}
```
```java
public class Application {

    public static void main(String[] args) {
        Geisha.run();
    }

}
```
Run Application and visit <http://127.0.0.1:5200/person/info?name=张三&age=18>

Result: 

    hello 张三, your age is 18
    
## License GPL

Project License can be found [here](https://github.com/RitterHou/Lilith/blob/master/LICENSE).
