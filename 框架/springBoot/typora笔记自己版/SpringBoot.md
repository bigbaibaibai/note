# 一、Spring Boot入门

## 1、HelloWorld

一个功能：

浏览器发送hello请求，服务器接受请求并处理，响应Hello World字符串；

### 1、创建一个maven工程；（jar）

### 2、导入spring boot相关的依赖

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```

### 3、编写一个主程序；启动Spring Boot应用

```java
/**
 *  @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {

        // Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

### 4、编写相关的Controller、Service

```java
@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "Hello World!";
    }
}

```



### 5、运行主程序测试

### 6、简化部署

```xml
 <!-- 这个插件，可以将应用打包成一个可执行的jar包；-->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

将这个应用打成jar包，直接使用java -jar的命令进行执行；

## 2、Hello World探究

### 1、POM文件

#### 1、父项目

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.9.RELEASE</version>
</parent>

他的父项目是
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-dependencies</artifactId>
  <version>1.5.9.RELEASE</version>
  <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
他来真正管理Spring Boot应用里面的所有依赖版本；

```

Spring Boot的版本仲裁中心；

以后我们导入依赖默认是不需要写版本；（没有在dependencies里面管理的依赖自然需要声明版本号）

#### 2、启动器

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**spring-boot-starter**-==web==：

​	spring-boot-starter：spring-boot场景启动器；帮我们导入了web模块正常运行所依赖的组件；



Spring Boot将所有的功能场景都抽取出来，做成一个个的starters（启动器），只需要在项目里面引入这些starter相关场景的所有依赖都会导入进来。要用什么功能就导入什么场景的启动器



### 2、主程序类，主入口类

```java
/**
 *  @SpringBootApplication 来标注一个主程序类，说明这是一个Spring Boot应用
 */
@SpringBootApplication
public class HelloWorldMainApplication {

    public static void main(String[] args) {

        // Spring应用启动起来
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}

```

@**SpringBootApplication**:    Spring Boot应用标注在某个类上说明这个类是SpringBoot的主配置类，SpringBoot就应该运行这个类的main方法来启动SpringBoot应用；



```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = {
      @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class),
      @Filter(type = FilterType.CUSTOM, classes = AutoConfigurationExcludeFilter.class) })
public @interface SpringBootApplication {
```

@**SpringBootConfiguration**:Spring Boot的配置类；

​		标注在某个类上，表示这是一个Spring Boot的配置类；

​		@**Configuration**:配置类上来标注这个注解；

​			配置类 -----  配置文件；配置类也是容器中的一个组件；@Component



@**EnableAutoConfiguration**：开启自动配置功能；

​		以前我们需要配置的东西，Spring Boot帮我们自动配置；@**EnableAutoConfiguration**告诉SpringBoot开启自动配置功能；这样自动配置才能生效；

```java
@AutoConfigurationPackage
@Import(EnableAutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
```

​      	@**AutoConfigurationPackage**：自动配置包

​		@**Import**(AutoConfigurationPackages.Registrar.class)：

​		Spring的底层注解@Import，给容器中导入一个组件；导入的组件由AutoConfigurationPackages.Registrar.class；

==将主配置类（@SpringBootApplication标注的类）的所在包及下面所有子包里面的所有组件扫描到Spring容器；==

​	@**Import**(EnableAutoConfigurationImportSelector.class)；

​		给容器中导入组件？

​		**EnableAutoConfigurationImportSelector**：导入哪些组件的选择器；

​		将所有需要导入的组件以全类名的方式返回；这些组件就会被添加到容器中；

​		会给容器中导入非常多的自动配置类（xxxAutoConfiguration）；就是给容器中导入这个场景需要的所有组件，并配置好这些组件；		![自动配置类](E:/note/typora/SpringBoot/images/%E6%90%9C%E7%8B%97%E6%88%AA%E5%9B%BE20180129224104.png)

有了自动配置类，免去了我们手动编写配置注入功能组件等的工作；

​		SpringFactoriesLoader.loadFactoryNames(EnableAutoConfiguration.class,classLoader)；



==Spring Boot在启动的时候从类路径下的META-INF/spring.factories中获取EnableAutoConfiguration指定的值，将这些值作为自动配置类导入到容器中，自动配置类就生效，帮我们进行自动配置工作；==以前我们需要自己配置的东西，自动配置类都帮我们；

J2EE的整体整合解决方案和自动配置都在spring-boot-autoconfigure-1.5.9.RELEASE.jar；

## 3、快速创建Spring Boot项目

IDEA：使用 Spring Initializer快速创建项目

IDE都支持使用Spring的项目创建向导快速创建一个Spring Boot项目；

选择我们需要的模块；向导会联网创建Spring Boot项目；

默认生成的Spring Boot项目；

- 主程序已经生成好了，我们只需要我们自己的逻辑
- resources文件夹中目录结构
  - static：保存所有的静态资源； js css  images；
  - templates：保存所有的模板页面；（Spring Boot默认jar包使用嵌入式的Tomcat，默认不支持JSP页面）；可以使用模板引擎（freemarker、thymeleaf）；
  - application.properties：Spring Boot应用的配置文件；可以修改一些默认设置；

# 二、Spring Boot配置

## 1、配置文件

SpringBoot使用一个全局的配置文件，配置文件名是固定的；

•application.properties

•application.yml



配置文件的作用：修改SpringBoot自动配置的默认值；SpringBoot在底层都给我们自动配置好；



YAML（YAML Ain't Markup Language）

​	YAML  A Markup Language：是一个标记语言

​	YAML   isn't Markup Language：不是一个标记语言；

标记语言：

​	以前的配置文件；大多都使用的是  **xxxx.xml**文件；

​	YAML：**以数据为中心**，比json、xml等更适合做配置文件；

​	YAML：配置例子

```yaml
server:
  port: 8081
```

​	XML：

```xml
<server>
	<port>8081</port>
</server>
```



## 2、YAML语法：

### 1、基本语法

k:(空格)v：表示一对键值对（空格必须有）；

以**空格**的缩进来控制层级关系；只要是左对齐的一列数据，都是同一个层级的

```yaml
server:
    port: 8081
    path: /hello
```

属性和值也是大小写敏感；



### 2、值的写法

#### 字面量：普通的值（数字，字符串，布尔）

​	k: v：字面直接来写；

​		字符串默认不用加上单引号或者双引号；

​		""：双引号；不会转义字符串里面的特殊字符；特殊字符会作为本身想表示的意思

​				name:   "zhangsan \n lisi"：输出；zhangsan 换行  lisi

​		''：单引号；会转义特殊字符，特殊字符最终只是一个普通的字符串数据

​				name:   ‘zhangsan \n lisi’：输出；zhangsan \n  lisi



#### 对象、Map（属性和值）（键值对）：

​	k: v：在下一行来写对象的属性和值的关系；注意缩进

​		对象还是k: v的方式

```yaml
friends:
		lastName: zhangsan
		age: 20
```

行内写法：

```yaml
friends: {lastName: zhangsan,age: 18}
```



#### 数组（List、Set）：

用- 值表示数组中的一个元素

```yaml
pets:
 - cat
 - dog
 - pig
```

行内写法

```yaml
pets: [cat,dog,pig]
```



### 3、配置文件注入案例

使用@ConfigurationProperties注解，从全局配置文件中获取属性

写一个配置文件：

~~~yaml
person:
  user-name: zhangsan    #userName等价于user-name
  age: 18
  boss: false
  birth: 1998/9/5

  map: {k1: v1,k2: v2}
  list: [v1,v2,v3,v4]

  dog:
    name: yangyang
    age: 22
~~~

写一个类用于接收属性，创建Bean注册到容器中

==这个类中一定要写setter方法，否则会赋值失败==

```java
//@ConfigurationProperties注解表示从配置文件中获取相应属性配置，注册Bean，prefix属性表示在配置文件中的前缀
@ConfigurationProperties(prefix = "person")
//必须加入到容器中才能使用IOC容器的功能
@Component
public class Person {
    private String userName;
    private Integer age;
    private Boolean boss;
    private Date birth;

    private Map<String, Object> map;
    private List<Object> list;

    private Dog dog;
    
    //get。。。  set。。。
}
```

```java
public class Dog {
    private String name;
    private Integer age;
    //get。。。   set。。。
}
```

如果需要在写yaml文件的时候，有==提示信息==，需要添加一个依赖，并重新启动主程序

```xml
<!--导入配置文件处理器，配置文件进行绑定就会有提示-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

## 3、@Value获取值和@ConfigurationProperties获取值比较

|                      | @ConfigurationProperties | @Value     |
| -------------------- | ------------------------ | ---------- |
| 功能                 | 批量注入配置文件中的属性 | 一个个指定 |
| 松散绑定（松散语法） | 支持                     | 不支持     |
| SpEL                 | 不支持                   | 支持       |
| JSR303数据校验       | 支持                     | 不支持     |
| 复杂类型封装         | 支持                     | 不支持     |

## 4、导入配置文件

### 1）@PropertySource

加载指定配置文件，把指定配置文件中的属性加载到java环境变量中（BeanFactory的systemProperties属性）

案例：

创建一个person.properties配置文件（也可以是yaml，这里以properties为例）

~~~properties
person.user-name=张三
person.age=18
person.birth=2018/9/5
person.boss=false
person.map.k1=v1
person.map.k2=v2
person.list=v1,v2,v3,v4
person.dog.name=yangyang
person.dog.age=22
~~~

写一个Person类，==必须使用@ConfigurationProperties注解，用来指定前缀，并赋值注册bean==

@PropertySource注解只是把配置文件中的值加载到环境变量中，具体的赋值和注册还是@ConfigurationProperties注解在做

```java
//@ConfigurationProperties注解表示从配置文件中获取相应属性配置，注册Bean，prefix属性表示在配置文件中的前缀
@ConfigurationProperties(prefix = "person")
//必须加入到容器中才能使用IOC容器的功能
@Component
@PropertySource(value = {"classpath:person.properties"})
public class Person {
    private String userName;
    private Integer age;
    private Boolean boss;
    private Date birth;

    private Map<String, Object> map;
    private List<Object> list;

    private Dog dog;
    
    //get。。。  set。。。
}
```



### 2）@ImportResource

用于导入spring配置文件，SpringBoot使用的配置类来管理配置，可以通过@ImportResource注解的方式加载spring配置文件，配置文件中的配置和自动配置类中的配置共同起作用，且配置文件优先级高



案例：

在SpringBoot应用程序上加载spring配置文件

~~~java
@SpringBootApplication
@ImportResource(value = {"classpath:beans.xml"})
public class Springboot1Application {

    public static void main(String[] args) {
        SpringApplication.run(Springboot1Application.class, args);
    }

}

~~~

配置文件：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="dog" class="xyz.thishome.springboot1.bean.Dog">
        <property name="name" value="xiaohome"></property>
        <property name="age" value="18"></property>
    </bean>

</beans>
```

经过测试，发现dog确实被注册到IOC容器中，配置文件生效

==不建议使用spring配置文件，SpringBoot旨在消除配置文件，推荐使用配置类的方式。==

## 5、配置文件占位符

可以在配置文件中使用表达式占位符，SpringBoot在读取配置文件时会将其转化为需要的实际值

### 1）随机数

~~~
${random.value}、${random.int}、${random.long}
${random.int(10)}、${random.int[1024,65536]}
~~~

### 2）综合案例

```yaml
person:
  user-name: ${random.uuid}   #使用UUID
  age: ${random.int}    #使用随机数
  boss: false
  birth: 1998/9/5

  map: {k1: v1,k2: v2}
  list: [v1,v2,v3,v4]

  dog:
    name: ${person.user-name}_dog  #引用环境变量中其他属性的值
    #name: ${person.user-hello}_dog  #如果引用一个不存在的属性，name的值为${user.hello}_dog这个字符串
    #name: ${person.user-hello:user}_dog  #使用:可以指定，如果属性不存在的默认值，name的值为user
    age: 10
```



## 6、Profile多环境支持

### 1）多配置文件

我们在主配置文件编写的时候，文件名可以是   application-{profile}.properties/yml

默认使用application.properties的配置；

切换配置文件：

**application.properties**

```yaml
spring:
  profiles:
    active: dev   #切换环境为dev
server:
  port: 8080
```

**application-dev.properties**

```yaml
server:
  port: 8081
```

**application-prod.properties**

```yaml
server:
  port: 8082
```

### 2）yml支持多文档块方式

在yml文件中，---表示文档块分割线，相当于多配置文件的效果

```yaml
spring:
  profiles:
    active: dev  #激活dev环境
    
server:
  port: 8080

---

server:
  port: 8081

spring:
  profiles: dev

---

server:
  port: 8082

spring:
  profiles: prod
```

### 3）其他切换环境方式

运行时指定某个环境变量的值

1、命令行：

​	`java -jar spring-boot-02-config-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev；`

​	可以直接在测试的时候，配置传入命令行参数

2、虚拟机参数；

​	`-Dspring.profiles.active=dev`



## 7、配置文件加载顺序及位置

### 1）自动加载配置文件

springboot 启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件

–file:./config/                    #工程下的config目录下

–file:./                                #工程下

–classpath:/config/         #类路径下config目录下

–classpath:/                     #类路径下

优先级由高到底，高优先级的配置会覆盖低优先级的配置；

SpringBoot会从这四个位置全部加载主配置文件；**互补配置**；

### 2）指定配置文件位置

==我们还可以通过spring.config.location来改变默认的配置文件位置==（需要使用命令行参数，指定该属性的值）

**项目打包好以后，我们可以使用命令行参数的形式，启动项目的时候来指定配置文件的新位置；指定配置文件和默认加载的这些配置文件共同起作用形成互补配置；**

java -jar spring-boot-02-config-02-0.0.1-SNAPSHOT.jar --spring.config.location=G:/application.properties

### 3）外部配置加载

**==SpringBoot也可以从以下位置加载配置； 优先级从高到低；高优先级的配置覆盖低优先级的配置，所有的配置会形成互补配置==**

**1.命令行参数**

所有的配置都可以在命令行上进行指定

java -jar spring-boot-02-config-02-0.0.1-SNAPSHOT.jar --server.port=8087  --server.context-path=/abc

多个配置用空格分开； --配置项=值



2.来自java:comp/env的JNDI属性

3.Java系统属性（System.getProperties()）

4.操作系统环境变量

5.RandomValuePropertySource配置的random.*属性值



==**由jar包外向jar包内进行寻找；**==

==**优先加载带profile**==

**6.jar包外部的application-{profile}.properties或application.yml(带spring.profile)配置文件**

**7.jar包内部的application-{profile}.properties或application.yml(带spring.profile)配置文件**



==**再来加载不带profile**==

**8.jar包外部的application.properties或application.yml(不带spring.profile)配置文件**

**9.jar包内部的application.properties或application.yml(不带spring.profile)配置文件**



10.@Configuration注解类上的@PropertySource

11.通过SpringApplication.setDefaultProperties指定的默认属性

所有支持的配置加载来源；

[参考官方文档](