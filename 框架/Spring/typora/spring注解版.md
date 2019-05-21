# 一、IOC-组件注册

## 1.配置类基本使用@Configuration

被**@Configuration**注解标识的类被称为配置类

配置类的作用是代替配置文件，之前写代码如果要配置一个组件，需要在配置文件中进行配置

有了配置类，以后只需要在配置类中写需要的组件即可，无需使用配置文件

### 配置一个Bean@Bean

**配置文件方式**

~~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    
    <bean id="person" class="xyz.thishome.bean.Person">
        <property name="name" value="小红"></property>
        <property name="age" value="18"></property>
    </bean>
    
</beans>
~~~~

**配置类方式**

~~~java
//表示这个类是一个配置类
@Configuration
public class ApplicationConfig {
    //这里表示为容器中添加一个Bean，对应配置文件的bean标签
    // Bean的id默认为方法名，可以通过value属性指定Id
    @Bean
    public Person person(){
        return new Person("小蓝",17);
    }
    
}
~~~

## 2.自动扫描的包@ComponentScan

配置文件方式，包含自动扫描策略，这里为注解的方式

~~~xml
<!-- 不包含controller --> 
<mvc:component-scan base-package="xyz.thishome">
        <mvc:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller"></mvc:include-filter>
 </mvc:component-scan>


<!-- 只包含，需要禁用默认的过滤器 --> 
<mvc:component-scan base-package="xyz.thishome" use-default-filters="false">
        <mvc:include-filter type="annotation" expression="org.springframework.stereotype.Controller"></mvc:include-filter>
</mvc:component-scan>


~~~

**配置类方式**，使用**@ComponentScan**注解标识在配置类的上面配置顶扫描的包

用**@ComponentScan.Filter**注解标识扫描策略，通过注解区分

~~~java
//在配置类上标注自动扫描的包，这里为不包含controller
@ComponentScan(value = "xyz.thishome" , excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {
            Controller.class
         })
})
@Configuration
public class ApplicationConfig {
    
    
//只包含controller，在@ComponentScan中添加一个useDefaultFilters属性为false
@ComponentScan(value = "xyz.thishome" , includeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION,classes = {Controller.class})
},useDefaultFilters = false)
@Configuration
public class ApplicationConfig {
  
~~~

### 自定义扫描规则TypeFilter

创建一个实现**TypeFilter**接口，实现它的match方法，可以获取到扫描类的基本信息，通过类的信息，可以自定义过滤规则

~~~java
public class MyTypeFilter implements TypeFilter {
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        //获取正在扫描类的类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        //获取类名
        String className = classMetadata.getClassName();
        //获取注解信息，获取所有注解
        metadataReader.getAnnotationMetadata().getAnnotationTypes();
        //如果类名包含er则返回true包含，否则返回false不包含
        if (className.contains("er")) {
            return true;
        }
        return false;
    }
}
~~~



使用自定义过滤规则

~~~java
@ComponentScan(value = "xyz.thishome" , excludeFilters = {
    	//使用自定义过滤规则，指定过滤类
        @ComponentScan.Filter(type=FilterType.CUSTOM,classes = MyTypeFilter.class)
})
@Configuration
public class ApplicationConfig {
~~~

## 3.Bean范围（单\多例）@Scope

使用**@Scope**注解

~~~java
	@Bean
    @Scope("prototype")  //默认为单实例的，singleton，这里指明是多实例
    public Person person(){
        return new Person("小龙",23);
    }
~~~

**singleton单实例**：IOC启动时，会调用方法创建实例放到IOC容器中，以后每次获取该实例就直接从IOC容器中来拿即可（map.get()）；

**prototype多实例**：IOC容器启动时并不会调用方法创建对象，每次获取的时候才会调用方法创建对象

## 4.懒加载@Lazy

使用**@Lazy**注解，针对单实例；

单实例对象**默认在IOC启动时被创建**，使用了懒加载，该对象会**在第一次使用时被创建**；

~~~java
 	@Lazy
	@Bean
    public Person person(){
        return new Person("小龙",23);
    }
~~~

## 5.条件注册Bean@Conditional**

创建一个条件类，实现**Condition**接口

使用**@Conditional**注解标注在创建bean的方法上调用此类

**此注解也可以标注配置类**，表示对类中组件统一设置

~~~java
//实现condition 接口
public class WindowsCondition implements Condition {
    //在此方法中定义规则
    //context，获取上下文信息，metadata获取注解信息
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata{
        //获取Bean创建的工厂
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();

        //获取类加载器，可以通过类加载器判断当前环境下是否存在某个类
        ClassLoader classLoader = context.getClassLoader();
		try {			
       	   context.getClassLoader().loadClass("xyz.thishome.service.PersonService");
        } catch (ClassNotFoundException e) {
            return false;
        }
        
        //获取Bean定义的注册类，可以判断容器中是否有某个Bean，也可以注册一个Bean,获取一个Bean 
        BeanDefinitionRegistry registry = context.getRegistry();
		//判断容器中是否包含person
        boolean hasPerson = context.getRegistry().containsBeanDefinition("person");
        
        //获取运行时环境信息
        Environment environment = context.getEnvironment();
        //获取当前操作系统环境
        String osName = environment.getProperty("os.name");
        //如果为Windows系统则返回true
        if(osName.contains("Windows")){
            return true;
        }
        return false;
    }
}
~~~

使用这个规则添加Bean

~~~java
	@Bean("bill")
    @Conditional({WindowsCondition.class})//如果这个规则返回true，则添加Bean，否则不添加
    public Person person(){
        return new Person("bill",60);
    }
~~~

## 6.快速导入组件@import**

@import注解中可以传入三种类型

- Bean.class        直接传入组件的class，直接导入
- ImportSelecter.class        接口ImportSelecter实现类的class，自定义逻辑导入
- ImportBeanDefinitionRegistrar.class       接口ImportBeanDefinitionRegistrar实现类的class，自行导入

### 1）直接快速导入

直接使用**@Impor**t注解里面直接写需要导入的组件class，**id默认为全类名**

~~~java
@Configuration
@Import({Color.class,Red.class})
public class ApplicationConfig2 {
~~~

### 2）定义逻辑导入组件

先创建一个实现**ImportSelecter**接口的实现类，实现方法，返回需要导入组件的全类名的String类型的数组

~~~java
//实现ImportSelector接口
public class MyImportSelecter implements ImportSelector {

    @Override    //AnnotationMetadata包含了当前类被标注的所有注解信息
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //返回需要导入组件的群类名，如果为null会报错
        return new String[]{"xyz.thishome.bean.Red","xyz.thishome.bean.Color"};
    }
}
~~~

使用选择器，只需在Import注解中添加这个类的class即可

~~~java
@Configuration
@Import({MyImportSelecter.class})
public class ApplicationConfig2 {
~~~

### 3）自行导入组件

实现ImportBeanDefinitionRegistrar接口，利用参数自行导入组件

~~~java
public class MyImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    //两个参数  
    //AnnotationMetadata  可以获取表示类的注解信息
    //BeanDefinitionRegistry  获取Bean定义注册类的信息，可以通过此对象注册Bean,获取IOC中Bean，判断IOC中是否存在某个Bean
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		 //如果有person对象则创建Bean
        boolean hasPerson = registry.containsBeanDefinition("person");
        if (hasPerson){
            //定义注册Bean的信息，可以对Bean进行设置
            RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(Red.class);
            //添加Bean到IOC容器，可以指定Bean名（id）
            registry.registerBeanDefinition("red",rootBeanDefinition);
        }
    }
}
~~~

## 7.FactoryBean注册组件

创建工厂类

```java
//定义工厂类，实现FactoryBean接口
public class ColorFcotryBean implements FactoryBean {
    @Override    //创建Bean的方法
    public Object getObject() throws Exception {
        return new Color();
    }
    @Override    //返回类的类型
    public Class<?> getObjectType() {
        return Color.class;
    }
    @Override    //是否为单实例
    public boolean isSingleton() {
        return true;
    }
}
```

使用工厂类配置Bean

~~~java
	//这里虽然返回的是工厂类，但实际添加到容器中的是工厂类中getObject()的对象
    @Bean
    public ColorFcotryBean color(){
        return new ColorFcotryBean();
    }
~~~

获取Bean

```java
@Test
public void test3(){
    //这里获取的是class xyz.thishome.bean.Color类型的对象
    System.out.println(context.getBean("color").getClass());
    //如果想直接获取这个工厂类，需要在ID前加上一个&符号，class xyz.thishome.bean.ColorFcotryBean
    System.out.println(context.getBean("&color").getClass());
}
```

## 8.总结Bean注册

Bean注册一共有三种方法：

​	① 配置文件中配置Bean（<bean></bean>标签）的方式

​	② @Component、@Repository、@Service、@Controller这四个注解来标识的Bean被注册

​	③ 配置类中@Bean注解标注方法的方式，方法返回值被注册

  	④ @Import注解标注的方式，导入组件



# 二、IOC-Bean生命周期

## 1.bean的生命周期

​	bean创建----->初始化----->销毁过程

容器可以帮我们管理；

我们可以自己来定义初始化和销毁的方法，容器在bean进行到当前生命周期的时候来调用我们自定义的初始化和销毁方法

**创建：**

​	单实例：在容器启动的时候创建对象

​	多实例：在每次获取的时候创建对象**

**初始化：**

​	**对象创建完成，并赋值好**，调用初始化方法

**销毁：**

​	单实例：容器关闭的时候

​	多实例：容器不管你什么时候销毁**

## 2.配置时指定初始化和销毁方法

创建一个Bean类

~~~java
public class Car {

    public Car() {
        System.out.println("Car Constructor . . . ");
    }
	//初始化方法
    public void init() {
        //容器创建Bean的时候被调用，构造器之后
        System.out.println("Car init . . .");
    }
	//销毁方法
    public void destroy() {
        //如果是单实例，容器调用close()方法时被调用
        System.out.println("Car destroy . . .");
    }

}
~~~

配置Bean时指定初始化和销毁方法

```java
@Bean(initMethod = "init",destroyMethod = "destroy")
public Car car(){
    return new Car();
}
```

## 3.bean实现接口，容器自动调用

可以让Bean实现初始化和销毁的接口，容器在初始化和销毁Bean的时候就会自行调用响应的方法

**初始化接口：InitializingBean**       **销毁接口：DisposableBean**

```java
public class Cat implements InitializingBean , DisposableBean {
    public Cat() {
        System.out.println("Cat Constructor . . . ");
    }
    @Override
    public void destroy() throws Exception {
        System.out.println("Cat Destroy . . . ");
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("Cat AfterPropertiesSet . . .");
    }
}
```

## 4.注解标注方法JSR250标准

可以在Bean的初始化和销毁方法上标注注解，在容器加载好对象，赋好值之后，就会调用初始化方法，容器关闭时就会调用销毁方法，这两个注解是java的JSR250规范注解

初始化：**@PostConstruct**               销毁： **@PreDestroy**

```java
public class Dog {
    public Dog(){
        System.out.println("Dog Constructor . . .");
    }
    @PostConstruct
    public void init(){
        System.out.println("Dog Init . . .");
    } 
    @PreDestroy
    public void destroy(){
        System.out.println("Dog Destroy . . .");
    }
}
```

## 5.后置处理器

在容器中所有Bean初始化方法（上述3种）之前和之后调用进行操作，需要实现**BeanPostProcessor**接口，BeanPostProcessor后置处理器是最基础的后置处理器

**如果bean没有初始化方法，同样会调用**

​	初始化之前工作：**postProcessBeforeInitialization()**

​	初始化之后工作：**postProcessAfterInitialization()**

```java
//定义一个后置处理器
@Component   //加入到容器中
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override    //bean  刚创建出来的实例    beanName  实例在容器中的名字（id）
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessAfterInitialization--->"+beanName+"--->"+ bean +" . . . ");
        //返回后来需要用的对象，如，返回传入的实例，或者包装后返回
        return bean;
    }

    @Override    //bean  初始化后的实例    beanName  实例在容器中的名字（id）
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("postProcessBeforeInitialization--->"+beanName+"--->"+ bean +" . . . ");
        return bean;
    }
}
```



### 1）BeanPostProcessor原理

```java
//创建好的Bean所有属性赋值
populateBean(beanName, mbd, instanceWrapper);
//调用初始化方法
exposedObject = initializeBean(beanName, exposedObject, mbd);
{
    //调用所有后置处理器的postProcessBeforeInitialization()方法
    applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
    {
        Object result = existingBean;
        //遍历所有的后置处理器
		for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            //调用postProcessBeforeInitialization()方法
			Object current = beanProcessor.postProcessBeforeInitialization(result, beanName);
            //如果返回null直接返回，不在执行后来的后置处理器
			if (current == null) {
				return result;
			}
            //后置处理器执行后，把处理过的Bean交给后来的后置处理器处理
			result = current;
		}
		return result;
    }
    //调用Bean的初始化方法
    invokeInitMethods(beanName, wrappedBean, mbd);
    //调用所有后置处理器的postProcessAfterInitialization()方法，源码同上
    applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
}
```

### 2）spring底层的运用

例1，**在bean对象中使用IOC容器**，spring在底层使用**ApplicationContextAwareProcessor**后置处理器

需要让使用IOC容器的Bean实现一个接口

```java
//实现ApplicationContextAware接口
public class Pig implements ApplicationContextAware {
    //定义一个ApplicationContext变量，便于把IOC容器保存起来
    private ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //把IOC容器赋值给属性
        this.context=applicationContext;
    }
}
```

​	**ApplicationContextAwareProcessor会调用postProcessBeforeInitialization()方法，在初始化之前调用该对象的setApplicationContext()方法，传入IOC容器**

~~~java
//如果对象为ApplicationContextAware类型，则强制转换，并调用赋值方法
if (bean instanceof ApplicationContextAware) {
	((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
}
~~~



例2，**BeanValidationPostProcessor**，用来做数据校验功能，在Web中用到的挺多



例3，**InitDestroyAnnotationBeanPostProcessor**，用于执行@PostConstruct标注的初始化方法

在Bean初始化前，找到被@PostConstruct标注的方法，利用反射调用



例4，**AutowiredAnnotationBeanPostProcessor**，在Bean初始化之前，找到被**@Autowire**注解标识的属性，为其赋值



### 3）总结

spring用注解标注的方法，或者属性，大多数都是用BeanPostProcessor后置处理器，在初始化前后调用或者赋值的，碰到注解的时候就要首先想到这个注解是不是又一个BeanPostProcessor在起作用



### 4）所有后置处理器

#### 1）**Bean后置处理器**

继承关系

~~~java
|BeanPostProcessor

	|DestructionAwareBeanPostProcessor

	|MergedBeanDefinitionPostProcessor

	|InstantiationAwareBeanPostProcessor

		|SmartInstantiationAwareBeanPostProcessor
~~~



**1.BeanPostProcessor**

Bean初始化前后调用

```java
public interface BeanPostProcessor {
    //在Bean初始化之前被调用，返回值为后续使用的Bean
	Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException;

	//在Bean初始化之后调用，返回值为后续使用的Bean
	Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException;

}
```



**2.InstantiationAwareBeanPostProcessor**

关于对象实例化前后以及实例化后设置propertyValues的回调

```java
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {
	/**
	* Bean 实例化之前调用，尝试返回代理对象，如果创建代理对象成功，立即执行BeanPostProcessor的
	* postProcessAfterInitialization方法，直接返回对象
	* 在目标bean实例化之前，应用这个BeanPostProcessor。返回的bean对象可能是要使用的代理，而不是
    * 目标bean，从而有效地抑制目标bean的默认实例化。如果此方法返回非null对象，则bean创建过程将短
    * 路。应用的惟一进一步处理是来自配置的BeanPostProcessor的
    * postProcessAfterInitialization()回调。此回调将仅应用于具有bean类的bean定义。特别是，
    * 它不会应用于具有“工厂方法”的bean。后处理器可以实现扩展的
    * SmartInstantiationAwareBeanPostProcessor接口，以预测它们将在这里返回的bean对
    * 象的类型。
	*/
	Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;

	/**
	* 在对象实例化完毕执行populateBean(属性赋值)之前 如果返回false则spring不再对对应的bean实例
	* 进行自动依赖注入。在postProcessPropertyValues()方法之前
	*
	* 在实例化bean之后，通过构造函数或工厂方法执行操作，但在Spring属性填充(来自显式属性或自动连接)
	* 之前执行操作。这是对给定bean实例执行自定义字段注入的理想回调，刚好在Spring的自动连接开始之
	* 前。
	*/
	boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;
	
    /**
    * 在Bean属性赋值之前执行，可以用来检查和修改属性，最终返回的PropertyValues会应用到bean中
    * @Autowired、@Resource等就是根据这个回调来实现最终注入依赖的属性的。
    *
    * 在工厂将给定属性值应用到给定bean之前，对它们进行后处理。允许检查是否满足所有依赖项，例如基于
    * bean属性设置器上的“Required”注释。还允许替换要应用的属性值，通常是在原有属性值的基础上创建一
    * 个新的MutablePropertyValues实例，添加或删除特定值。
    */
	PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException;

}

```



**3）SmartInstantiationAwareBeanPostProcessor**

这个接口主要是spring框架内部来使用

~~~java
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    //用来返回目标对象的类型（比如代理对象通过raw class获取proxy type 用于类型匹配）
    @Nullable
    default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }
    //这里提供一个拓展点用来解析获取用来实例化的构造器（比如未通过bean定义构造器以及参数的情况下，会根据这个回调来确定构造器）
    @Nullable
    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
            throws BeansException {
        return null;
    }
    //获取要提前暴露的bean的引用，用来支持单例对象的循环引用（一般是bean自身，如果是代理对象则需要取用代理引用）
    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
~~~



**4.MergedBeanDefinitionPostProcessor**

用来将merged BeanDefinition暴露出来的回调

~~~java
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {
    //在bean实例化完毕后调用 可以用来修改merged BeanDefinition的一些properties 或者用来给后续回调中缓存一些meta信息使用
    //这个算是将merged BeanDefinition暴露出来的一个回调
    void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);
}
~~~



**5.DestructionAwareBeanPostProcessor**

~~~java
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {
    //这里实现销毁对象的逻辑
    void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;
    //判断是否需要处理这个对象的销毁
    default boolean requiresDestruction(Object bean) {
        return true;
    }
}
~~~

方法执行顺序
~~~sql

1、InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation(beanClass, beanName)
    该方法在创建对象之前会先掉用，如果有返回实例则直接使用不会去走下面创建对象的逻辑，并在之后执行
        BeanPostProcessor.postProcessAfterInitialization(result, beanName)
2、SmartInstantiationAwareBeanPostProcessor.determineCandidateConstructors(beanClass, beanName)
    如果需要的话，会在实例化对象之前执行
3、MergedBeanDefinitionPostProcessor.postProcessMergedBeanDefinition(mbd, beanType, beanName)
    在对象实例化完毕 初始化之前执行
4、InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)
    在bean创建完毕初始化之前执行
5、InstantiationAwareBeanPostProcessor.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName)
    在bean的property属性注入完毕 向bean中设置属性之前执行
6、BeanPostProcessor.postProcessBeforeInitialization(result, beanName)
    在bean初始化（自定义init或者是实现了InitializingBean.afterPropertiesSet()）之前执行
7、BeanPostProcessor.postProcessAfterInitialization(result, beanName)
    在bean初始化（自定义init或者是实现了InitializingBean.afterPropertiesSet()）之后执行
8、其中DestructionAwareBeanPostProcessor方法的postProcessBeforeDestruction(Object bean, String beanName)会在销毁对象前执行


DestructionAwareBeanPostProcessor 中的requiresDestruction(Object bean)是用来判断是否属于当前processor处理的bean
SmartInstantiationAwareBeanPostProcessor中的predictBeanType(Class<?> beanClass, String beanName)是用来预判类型的
SmartInstantiationAwareBeanPostProcessor.getEarlyBeanReference(exposedObject, beanName)
    这个方法仅仅是在这一步是作为一个ObjectFactory封装起来放到singletonFactories中的，
    仅在并发情况下 刚好在当前对象设置进去，而另一个bean创建需要getBean获取时才会立即执行
    因此这一步的顺序是不一定的，有可能永远不会执行（无并发循坏依赖对象创建的场景）
    可能在3之后对象实例化完毕执行addSingleton(beanName, singletonObject);之前执行到
因此这三个方法没有严格的顺序意义

~~~



#### 2）BeanFactory后置处理器

1.BeanFactoryPostProcessor

在BeanFactory对象初始化后调用这个后置处理器

```java
public interface BeanFactoryPostProcessor {

	//BeanFactory初始化后，所有将要加载到IOC容器的对象被实例化之前，调用此方法
    //在标准初始化之后修改应用程序上下文的内部bean工厂。将加载所有bean定义，但是还没有实例化bean。	  //这甚至允许覆盖或添加属性到急启动bean。
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
```



2.BeanDefinitionRegistryPostProcessor

可以利用BeanDefinitionRegistryPostProcessor给容器中再额外添加一些组件；

```java
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	//在BeanFactory创建之后，在BeanFactoryPostProcessor执行之前执行
    //在标准初始化之后修改应用程序上下文的内部bean定义注册表。将加载所有常规bean定义，但是还没有实例
    //化bean。这允许在下一个后期处理阶段开始之前添加进一步的bean定义。
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
```





# 三、IOC-属性赋值

## 1.@Value直接赋值

可以在某个Bean属性上使用@Value注解，直接为其赋值，Bean必须要在Ioc容器中，值可以用${}的方式使用当前环境变量中的值

```java
public class Person {

    @Value("李四")
    private String name;
    @Value("#{3*6}")
    private Integer age;
```

## 2.外部配置文件赋值

可以引入外部配置文件对属性进行赋值，**需要使用getter  setter方法**

新建一个配置文件person.properties

```properties
person.name=李四
person.age=18
```

在配置类（使用前的任意类即可）上标注**@PropertySource**注解注册配置文件，会把配置文件中的信息**加载到运行时环境变量中**，value代表配置文件的url，可以是多个，数组类型

~~~java
@PropertySource(value = "classpath:person.properties")
@Configuration
public class ApplicationConfigValue {
    @Bean
    public Person person(){
        return new Person();
    }
}
~~~

使用配置文件中的值为属性赋值

~~~java
public class Person {

    @Value("${person.name}")
    private String name;
    @Value("${person.age}")
    private Integer age;
~~~

## 3.外部配置文件值获取

**所有外部配置文件被注册后，属性都会被加载到运行时环境变量中去，就可以通过环境变量对象来获取值**

```java
@Test
public void test5(){
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfigValue.class);
    //获取运行时环境信息
    ConfigurableEnvironment environment = context.getEnvironment();
    //获取运行时环境中的某个属性值
    String name = environment.getProperty("person.name");
    System.out.println(name);
}
```



# 四、IOC-自动装配

## 1.@Autowired自动装配规则

@Autowired自动注入规则：

​	1) 默认会**优先按照类型**去容器中找对应的组件：applicationContext.getBean(BookDao.class);

​	2) 如果找到多个相同类型的组件，在**将属性名作为组件的id**去容器中查找；

​		applicationContext.getBean("bookDao");

​	3) 可以通过指定id自动装配，使用**@Qualifier**注解

​		@Qualifier("bookDao")    ==  applicationContext.getBean("bookDao");

​	4) 使用@Autowired注解默认就一定要把属性自动装配好，容器中没有组件就会报错，可以设置**required**属性改为不必须

​		默认为true，必须，设置为false，不必须，如果容器中没有，就不自动装配

​	5) 设置首选bean，当存在多个相同类型Bean时，**添加@Primary注解的Bean就为首选Bean**

​		**如果使用@Qualifier注解，首选失效**

```java
@Service
public class BookService {

    @Qualifier("bookDao")
    @Autowired(required = false)
    private BookDao bookDao;
```

## 2.JSR标准自动装配

JSR标准为java规范

### 1）@Resource注解(JSR250)

可以和@Autowired一样实现自动装配功能：默认是按照组件名称自动装配的；

没有支持@Primary功能，没有支持@Autowired(reqiured=false)功能

### 2）@Inject注解(JSR330)

需要导入javax.inject包，和Autowired的功能一样，支持@Primary功能，但是没有@Autowired(reqiured=false)功能。

## 3.方法、构造器自动装配

**@Autowired注解可以标注在方法、构造器、属性、参数上**

### 1）标注在方法上

spring容器创建当前对象，就会调用方法，完成赋值；

方法使用的参数，自定义类型的值从IOC容器中获取

```java
@Autowired
public void setBookDao(BookDao bookDao) {
    this.bookDao = bookDao;
}
```

### 2）标注在构造器上

默认加在IOC容器中的组件，容器启动时会调用无参构造器创建对象，在进行初始化赋值等操作

如果@Autowired注解标注在有参构造器上，构造器的参数会从IOC容器中获取

**如果组件只有有参构造器，有参构造器的@Autowired注解可以省略**，参数位置的组件会自动从IOC容器中获取

```java
@Autowired
public BookService(BookDao bookDao) {
    this.bookDao=bookDao;
}
```

### 3）@Bean注解与自动装配

**@Bean注解标注的方法，参数能自动从IOC容器中获取**

```java
@Bean
public BookService bookService(BookDao bookDao){
    BookService bookService = new BookService();
    bookService.setBookDao(bookDao);
    return bookService;
}
```

## 4.自动装配Spring底层组件

自定义组件想要使用Spring容器底层的一些组件（ApplicationContext，BeanFactory，xxx），

需要自定义组件实现xxxAware（**Aware都是使用后置处理器的机制**）；

### ApplicationContextAware接口

在自定义组件中使用IOC容器

~~~java
public class Dog implements ApplicationContextAware {
	//定义一个自己的ApplicationContext对象，用于接收传入的对象
    private ApplicationContext context;
	
    //在对象初始化之前，会调用实现ApplicationContextAware接口的setApplicationContext方法，将ApplicationContext对象传入参数
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
~~~

### BeanNameAware接口

获取Bean在IOC容器中的名字

~~~java
public class Dog implements BeanNameAware {
    
 	//获取IOC为Bean创建实例的名字   
    @Override
    public void setBeanName(String name) {
        System.out.println(name);
    }
}
~~~

### EmbeddedValueResolverAware接口

解析字符串，可以获取环境变量中的值（**重点**）

~~~java
public class Dog implements EmbeddedValueResolverAware {

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        //可以解析字符串中的值，可以使用el表达式获取环境变量中的值，或者使用Spring表达式进行计算
        String value = resolver.resolveStringValue("你好${os.name},我是#{20*18}");
        //打印你好Windows 10,我是360
        System.out.println(value);
    }
}
~~~

## 5.@Profile环境切换（重点）

## 1）使用方式

**根据当前的环境，动态的激活和切换一系列组件的功能**

​					`开发环境`	`测试环境`	`生产环境`

​	        数据源：		`A数据库`		`B数据库`		`C数据库`

在改动少量代码的情况下快速切换环境，可以使用@Profile注解，在注册容器的时候指定该容器在哪个环境下被加载到IOC容器中，环境名是自己指定的


​	**加了环境标识的Bean，只有环境被激活后才会生效**

​	==不加环境表示的Bean，在任何环境下都会生效==    @Profile("default")，为默认环境任何环境下生效

~~~java
@Configuration
//把数据源配置文件中的属性加载到环境变量中去
@PropertySource("classpath:db.properties")
//实现字符串解析的接口，可以通过字符串的解析获取环境变量中的值
public class MainConfigOfProfile implements EmbeddedValueResolverAware {

    @Value("${jdbc.username}")
    private String user;

    private String driver;

    //测试环境下使用的数据源
    @Bean
    @Profile("test")
    public DataSource testDateSource(@Value("${jdbc.password}") String psw) { //value注解标注在参数上
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(psw);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        return dataSource;
    }

    //开发环境下使用的数据源
    @Bean
    @Profile("dev")
    public DataSource devDateSource(@Value("${jdbc.password}") String psw) { //value注解标注在参数上
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setUser(user);
        dataSource.setPassword(psw);
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/book");
        return dataSource;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        //通过解析字符串，获取环境变量中的值，赋值到对象的属性中
        driver = resolver.resolveStringValue("jdbc.driver");
    }
}
~~~

~~~properties
jdbc.username=root
jdbc.password=123456
jdbc.driver=com.mysql.jdbc.Driver
~~~

## 2）切换生产环境的方式

​	1.使用命令行参数

​		``-Dspring.profiles.active=test``

​	2.配置文件中配置一个属性

```properties
#在任意可以加载到环境变量的配置文件中配置即可
spring.profiles.active=dev
```

​	3.使用代码的方式，需要在初始化IOC容器之前进行操作

~~~java
 @Test
    public void testMain() {
        //如果使用代码的方式，在IOC容器初始化之前就要设置，所以不能写有参构造器，因为在有参造构器中就已经把IOC容器初始化了
        //1.创建IOC容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        //2.获取IOC环境对象，并设置环境变量，指定开发环境
        context.getEnvironment().setActiveProfiles("test","dev");
        //3.注册配置类
        context.register(MainConfigOfProfile.class);
        //4.手动启动刷新容器
        context.refresh();
        String[] names = context.getBeanNamesForType(DataSource.class);
        for (String name : names) {
            System.out.println(name);
        }
    }
~~~

## 3）@Profile标注在类上

如果把@Profile标注在类上，那么这个配置类只有在指定生产环境下才会被加载

~~~java
@Configuration
@Profile("test")
public class MainConfigOfProfile implements EmbeddedValueResolverAware {

~~~

# 五、AOP切面编程

## 1.AOP相关注解

动态代理，引发的面向切面编程。==指在程序运行期间，动态的将某段代码块切入到指定方法指定位置进行运行的编程方式==。

**@EnableAspectJAutoProxy**：注解标注在配置类上，表示开启注解版aop模式

​	等价于配置文件中的`<aop:aspectj-autoproxy></aop:aspectj-autoproxy>`

**@Aspect**：注解标注在类上，表示当前类 是一个切面类。

**@Before**：前置通知，在**方法执行前执行**

**@After**：后置通知，在方法执行后执行（**无论是否发生异常**）

**@AfterRunning**：返回通知，在方法正常结束后执行(**没有异常**)，可以访问到方法的结果，需要注解的`returning=“封装参数”`属性，指定接收返回值的参数名。

**@AfterThrowing**：异常通知，在方法**抛出异常**之后执行，可以访问异常对象，需要注解的`throwing = "e"`属性，来指定异常对象接收的参数名

**@Around**：环绕通知，围绕着方法执行，相当于**动态代理的全过程**，必须有返回值，返回值即代理方法的实际返回值，需要写一个**ProceedingJoinPoint**类型的参数

如果想要在方法中访问方法的细节（方法名，参数等），**需要在方法里加入一个==JoinPoint==类型的参数** 可以通过这个对象来访问方法细节，==注意，该参数如果有，一定要放在参数列表的第一位==

## 2.AOP简单使用

​		为计算除法的方法添加日志，计算前的计算参数，计算后的计算结果，如果有异常的异常信息

1.需要导入aop模块

~~~xml
		<dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>4.3.18.RELEASE</version>
        </dependency>
~~~

2.定义一个业务逻辑类

~~~java
public class MathCalculator {

    public int dev(int i, int j) {
        return i / j;
    }

}
~~~

3.定义一个日志切面类

~~~java
/**
 * 日志切面类
 */
@Aspect  //表示当前类是一个切面类
public class LogAspects {

    //定义切面表达式
    // 内部引用，写方法名即可，pointCut()
    // 外部引用，写方法名全限定名，xyz.thishome.aop.LogAspects.pointCut()
    @Pointcut("execution(public int xyz.thishome.bean.MathCalculator.*(..))")
    public void pointCut() {
    }

    //前置通知，在目标方法执行之前执行，调用切面表达式
    @Before("pointCut()")
    public void logStart(JoinPoint joinPoint) {  //JoinPoint对象，获取方法的详细信息
        //获取方法签名，通过方法签名对象获取方法名
        String name = joinPoint.getSignature().getName();
        //获取参数列表
        Object[] params = joinPoint.getArgs();
        //打印日志
        System.out.println(name + "方法开始，参数为：" + Arrays.asList(params));
    }

    //后置通知，在目标方法执行之后执行
    @After("pointCut()")
    public void logEnd(JoinPoint joinPoint) {
        System.out.println(joinPoint.getSignature().getName() + "方法结束");
    }

    //返回通知，在目标方法*正常*返回之后执行
    @AfterReturning(value = "pointCut()", returning = "result")  //指定需要接受返回值的参数
    public void logReturn(JoinPoint joinPoint, Object result/*接受返回值的参数*/) {
        System.out.println(joinPoint.getSignature().getName() + "方法正常返回，运行结果为：" + result);
    }

    //异常通知，在目标方法抛出异常之后执行
    @AfterThrowing(value = "pointCut()", throwing = "e") //指定需要接受异常对象的参数名
    public void logException(JoinPoint joinPoint, Exception e/*接受异常对象的参数*/) {
        System.out.println(joinPoint.getSignature().getName() + "方法异常，异常信息为：" + e);
    }

    @Around("pointCut()")    //环绕通知处理异常
    public Object exceptionHandle(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = null;
            result = joinPoint.proceed(joinPoint.getArgs());
            return result;
        } catch (Exception e) {
            //注意，int类型返回值不能为null，所以如果发生异常返回0
            return 0;
        }
    }
}

~~~



==注意，一定要在切面类上加上**@Aspect**注解，告诉spring这是一个切面类，否则无效==

4.将两个类加入到容器中

~~~java
@Configuration
@EnableAspectJAutoProxy  //开启基于注解的aop模式
public class MainConfigOfAOP {

    @Bean
    public MathCalculator mathCalculator() {
        return new MathCalculator();
    }

    @Bean
    public LogAspects logAspects() {
        return new LogAspects();
    }

}
~~~



==注意，一定要在配置类上加上**@EnableAspectJAutoProxy**注解，开启spring注解版aop功能，否则无效==

## 2.切面表达式

[pointcut 切面表达式 切入点表达式](http://www.cnblogs.com/duenboa/p/6665474.html)

下面给出一些常见切入点表达式的例子。

- 任意公共方法的执行：

  ```
  execution(public * *(..))
  ```

- 任何一个以“set”开始的方法的执行：

  ```
  execution(* set*(..))
  ```

- `AccountService` 接口的任意方法的执行：

  ```
  execution(* com.xyz.service.AccountService.*(..))
  ```

- 定义在service包里的任意方法的执行：

  ```
  execution(* com.xyz.service.*.*(..))
  ```

- 定义在service包或者子包里的任意方法的执行：

  ```
  execution(* com.xyz.service..*.*(..))
  ```

- 定义在modules包下面的所有包或子包下面以Controller结尾的文件下面的所有方法

~~~
com.zx.modules..*.*Controller.*(..))
~~~

## 3.AOP原理分析

### 1）@EnableAspectJAutoProxy注解

~~~java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)  //使用AspectJAutoProxyRegistrar，导入组件
public @interface EnableAspectJAutoProxy {
~~~

使用@Import，向IOC容器中添加了一个组件

看一下AspectJAutoProxyRegistrar的工作工作流程

```java
//实现ImportBeanDefinitionRegistrar接口，可以自行向容器中添加组件
class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {
@Override
	public void registerBeanDefinitions(
			AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		//在这一步，调用AopConfigUtils的方法向容器中注册了一个组件
        //这个方法的意思是，在需要的情况下，注册一个AspectJAnnotationAutoProxyCreator组件
AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);
        //底层调用了这个方法
 /------------------------------------------------------------{
            private static BeanDefinition registerOrEscalateApcAsRequired(Class<?> cls, BeanDefinitionRegistry registry, Object source) {
        //判断registry注册器是否为null
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        
        //AUTO_PROXY_CREATOR_BEAN_NAME表示想IOC容器中添加组件的id名
        //org.springframework.aop.config.internalAutoProxyCreator
        //如果IOC容器中已经包含这个组件
		if (registry.containsBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME)) {
			BeanDefinition apcDefinition = registry.getBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME);
			if (!cls.getName().equals(apcDefinition.getBeanClassName())) {
				int currentPriority = findPriorityForClass(apcDefinition.getBeanClassName());
				int requiredPriority = findPriorityForClass(cls);
				if (currentPriority < requiredPriority) {
					apcDefinition.setBeanClassName(cls.getName());
				}
			}
			return null;
		}
        //在这里定义了一个bean，对bean进行处理
		RootBeanDefinition beanDefinition = new RootBeanDefinition(cls);
		beanDefinition.setSource(source);
		beanDefinition.getPropertyValues().add("order", Ordered.HIGHEST_PRECEDENCE);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        //把这个bean添加到容器中去，这个Bean的类型为AnnotationAwareAspectJAutoProxyCreator
		registry.registerBeanDefinition(AUTO_PROXY_CREATOR_BEAN_NAME, beanDefinition);
		return beanDefinition;
	}
------------------------------------------------------------------*/
    	//把EnableAspectJAutoProxy注解的信息拿来
		AnnotationAttributes enableAspectJAutoProxy =
				AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
     	//如果注解中包含proxyTargetClass属性，则如何操作
		if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
			AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
		}
     	//注解中包含exposeProxy属性，则如何操作
		if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
			AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
		}
	}
}
```

添加了一个组件

**名为**`org.springframework.aop.config.internalAutoProxyCreator`

**类型为**`org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator`

由此见得，aop在底层使用`AnnotationAwareAspectJAutoProxyCreator`来运作的，这个类型翻译过来叫，**注解方面的AspectJ代理创建器**，可以剖析这个类来探究底层原理

先看一下这个类的继承关系

```
AnnotationAwareAspectJAutoProxyCreator
	继承于AspectJAwareAdvisorAutoProxyCreator  AspectJ方面的通知自动代理创建器
		继承于AbstractAdvisorAutoProxyCreator  抽象通知自动代理创建器
			继承于AbstractAutoProxyCreator		抽象自动代理创建器
				-----
					AbstractAutoProxyCreator实现了两个接口
					**SmartInstantiationAwareBeanPostProcessor  巧妙实例化组件后置处理器
					BeanFactoryAware		注入了Bean工程这个底层组件
				-----
				继承于ProxyProcessorSupport	代理后置处理器支持，比较底层的后置处理器
				...
```

可以分析出来，`AnnotationAwareAspectJAutoProxyCreator`的运作

离不开`SmartInstantiationAwareBeanPostProcessor`这个后置处理器和`BeanFactoryAware`发挥的作用，**因为后置处理器可以在Bean初始化前后对Bean的操作**，注入了`BeanFactory`可以做很多事情了。

所以需要剖析`AbstractAutoProxyCreator`这个类如何实现后置处理器的功能

从代码刚开始执行开始分析整个流程

~~~
AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainConfigOfAOP.class);
创建IOC容器
    |1.register(annotatedClasses);注册我们传入的配置类
    |2.refresh();刷新IOC容器，对IOC容器进行初始化
    	|1.prepareRefresh();准备初始化
    	|2.ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();更新内部初始化
    	|3.prepareBeanFactory(beanFactory);准备把Bean工厂给需要使用它的用户，初始化Bean工厂
    	|4.invokeBeanFactoryPostProcessors(beanFactory);在上下文中调用工厂后置处理器给Bean
    	|5.registerBeanPostProcessors(beanFactory);注册Bean后置处理器去拦截哪些创建的Bean
    		|1.String[] postProcessorNames = beanFactory.getBeanNamesForType
(BeanPostProcessor.class, true, false);从Bean工厂中获取所有的后置处理器Bean的名字
			|2.区分实现PriorityOrdered接口和Ordered接口的Bean，关系到优先级PriorityOrdered优先级最高，其次是Ordered，最后是普通Bean。
			|3.因为AbstractAutoProxyCreator实现了Ordered接口，在Ordered注册处注册internalAutoProxyCreator（组件的id）组件
			|4.会尝试从容器中获取这个Bean对象，并检验实例事如否已经存在，如果没有这个对象不到，会创建一个对象加入容器
				|1.populateBean(beanName, mbd, instanceWrapper);给Bean的属性赋值
				|2.exposedObject = initializeBean(beanName, exposedObject, mbd);  //初始化Bean
					|1.invokeAwareMethods(beanName, bean);调用Aware方法，给Aware属性赋值
							|判断是否是Aware类型，如果是，调用响应的方法注入值
						|2.调用所有后置处理器的postProcessBeforeInitialization()方法
						|3.invokeInitMethods(beanName, wrappedBean, mbd);Bean初始化
						|4.调用所有后置处理器的postProcessAfterInitialization()方法
。。。
				


     
~~~

这里分析的不太好，文档中有专门的AOP笔记



# 六、声明式事务

## 1.简单使用步骤

（1）需要导入spring-jdbc的依赖

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>4.3.17.RELEASE</version>
</dependency>
```

（2）需要在配置文件中加上开启注解式事务的注解==@EnableTransactionManagement==

```java
@EnableTransactionManagement
@Configuration
@ComponentScan(value = "xyz.thishome.*")
public class MainOfConfigTx {
```

（3）需要配置一个事务管理器

```java
@Bean
public DataSourceTransactionManager transactionManager() throws Exception {
    return new DataSourceTransactionManager(dataSource());
    
}
```

（4）需要在具体的方法或类上添加事务注解==@Transactional==，加载类上表示类中所有方法都是一个事务单位

```java
@Transactional
public void insert(){
    userDao.insert();
    System.out.println("插入成功");
}
```

## 2.源码分析

  1）、@EnableTransactionManagement
  			利用TransactionManagementConfigurationSelector给容器中会导入组件
  			导入两个组件
  			AutoProxyRegistrar
  			ProxyTransactionManagementConfiguration
  2）、AutoProxyRegistrar：
  			给容器中注册一个 InfrastructureAdvisorAutoProxyCreator 组件；
  			InfrastructureAdvisorAutoProxyCreator：？
  			利用后置处理器机制在对象创建以后，包装对象，返回一个代理对象（增强器），代理对象执行方法利用拦截器链进行调用；

  3）、ProxyTransactionManagementConfiguration 做了什么？
  			1、给容器中注册事务增强器；
  				1）、事务增强器要用事务注解的信息，AnnotationTransactionAttributeSource解析事务注解
  				2）、事务拦截器：
  					TransactionInterceptor；保存了事务属性信息，事务管理器；
  					他是一个 MethodInterceptor；
  					在目标方法执行的时候；
  						执行拦截器链；
  						事务拦截器：
  							1）、先获取事务相关的属性
  							2）、再获取PlatformTransactionManager，如果事先没有添加指定任何transactionmanger最终会从容器中按照类型获取一个PlatformTransactionManager；
  							3）、执行目标方法
  								如果异常，获取到事务管理器，利用事务管理回滚操作；
  								如果正常，利用事务管理器，提交事务



# 七、扩展原理

## 1.BeanFactoryPostProcessor

BeanFactory初始化之后（Bean定义信息已经被记载），所有要加入到IOC容器的Bean对象创建之前，执行该后置处理器——BeanFactory对象的后置处理器。

  BeanFactoryPostProcessor原理:
  1)、ioc容器创建对象

  2)、refresh()-》invokeBeanFactoryPostProcessors(beanFactory);
  		如何找到所有的BeanFactoryPostProcessor并执行他们的方法；
  			1）、直接在BeanFactory中找到所有类型是BeanFactoryPostProcessor的组件，并执行他们的方法
  			2）、在初始化创建其他组件前面执行

## 2.BeanDefinitionRegistryPostProcessor 

  BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor
  		postProcessBeanDefinitionRegistry();
  		在所有bean定义信息将要被加载，bean实例还未创建的；

  		优先于BeanFactoryPostProcessor执行；
  		可以利用BeanDefinitionRegistryPostProcessor给容器中再额外添加一些组件；

原理：
  		1）、ioc创建对象

  		2）、refresh()-》invokeBeanFactoryPostProcessors(beanFactory);

  		3）、从容器中获取到所有的BeanDefinitionRegistryPostProcessor组件。
  			1、依次触发所有的postProcessBeanDefinitionRegistry()方法
  			2、再来触发postProcessBeanFactory()方法BeanFactoryPostProcessor；

  		4）、再来从容器中找到BeanFactoryPostProcessor组件；然后依次触发postProcessBeanFactory()方法

## 3.事件监听机制

### 1）自定义监听器

写一个自定义监听器

~~~java
@Component   //添加到容器中
//泛型中的值代表需要监听的事件类型，必须是ApplicationEvent及其实现类
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {
    @Override   //打印每次监听到的事件
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("监听到事件："+event);
    }

}
~~~

只要容器中有相关事件的发布，我们就能监听到这个事件；
​		ContextRefreshedEvent：容器刷新完成（所有bean都完全创建）会发布这个事件；
​		ContextClosedEvent：关闭容器会发布这个事件；

### 2）发布一个事件

1.applicationContext.publishEvent()；使用spring应用程序发布

```java
	@Test
    public void testInsert() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MainOfConfigTx.class);
		context.publishEvent("自定义事件1");//传入一个Object
    }
```

2.注入ApplicationEventMulticaster对象，应用事件多播器（派发器），使用多播器发布事件

~~~java
@Service
public class UserService {

    @Autowired
    private ApplicationEventMulticaster eventMulticaster;

    @Transactional
    public void insert(){   //发布一个事件，传入ApplicationEvent（抽象类）事件对象
      //这里直接实现类，传入一个字符串对象， 入参是一个Object对象
   eventMulticaster.multicastEvent(new ApplicationEvent(new String("自定义事件")) {});
    }

}
~~~

### 3）原理

 1）、ContextRefreshedEvent事件：
 	1）、容器创建对象：refresh()；
 	2）、finishRefresh();容器刷新完成会发布ContextRefreshedEvent事件
 2）、自己发布事件；
 3）、容器关闭会发布ContextClosedEvent；

 【事件发布流程】：
 	3）、publishEvent(new ContextRefreshedEvent(this));
 			1）、获取事件的多播器（派发器）：getApplicationEventMulticaster()
 			2）、multicastEvent派发事件：
 			3）、获取到所有的ApplicationListener；
 				for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
 				1）、如果有Executor，可以支持使用Executor进行异步派发；
 					Executor executor = getTaskExecutor();
 				2）、否则，同步的方式直接执行listener方法；invokeListener(listener, event);
 				 拿到listener回调onApplicationEvent方法；

 【事件多播器（派发器）初始化过程】
 	1）、容器创建对象：refresh();
 	2）、initApplicationEventMulticaster();初始化ApplicationEventMulticaster；
 		1）、先去容器中找有没有id=“applicationEventMulticaster”的组件；
 		2）、如果没有this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
 			并且加入到容器中，我们就可以在其他组件要派发事件，自动注入这个applicationEventMulticaster；

【注册监听器过程】
 	1）、容器创建对象：refresh();
 	2）、registerListeners();
 		从容器中拿到所有的监听器，把他们注册到applicationEventMulticaster中；
 		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
 		//将listener注册到ApplicationEventMulticaster中
 		getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);

###  4）*注解方式监听

使用**@EventListener**注解，在任意方法中都可以使用这个注解，不影响其他方法的流程，更加灵活，方便

~~~java
	@EventListener(classes = {ApplicationEvent.class})
    public void listen(ApplicationEvent event){
        System.out.println("监听到事件："+event);
    }
~~~

原理：

使用EventListenerMethodProcessor处理器来解析方法上的@EventListener；

~~~java
//该后置处理器实现了两个接口，ApplicationContextAware为注入spring全局对象
//重点关注SmartInitializingSingleton接口
public class EventListenerMethodProcessor implements SmartInitializingSingleton, ApplicationContextAware {
    //操作。。。
}
~~~

SmartInitializingSingleton接口

~~~java
public interface SmartInitializingSingleton {

	/**
	 * 所有的单实例Bean创建完成后（所有步骤全部结束，事件发布之前），调用
	 */
	void afterSingletonsInstantiated();

}
~~~

  SmartInitializingSingleton 原理：->afterSingletonsInstantiated();
  		1）、ioc容器创建对象并refresh()；
  		2）、finishBeanFactoryInitialization(beanFactory);初始化剩下的单实例bean；
  			1）、先创建所有的单实例bean；getBean();
  			2）、获取所有创建好的单实例bean，判断是否是SmartInitializingSingleton类型的；
  				如果是就调用afterSingletonsInstantiated();

## 4.spring容器整个流程

xmind图





# 八、servlet3.0

## 1.原生注册三大组件

传统的xml文件方式不再说，这里主要是其他两种方式

### 1）注解方式注册

注册servlet：使用**@WebServlet("/user")**注解

```java
@WebServlet("/user")
public class UserServlet extends HttpServlet {
```

注册filter：使用**@WebFilter("/user")**注解

```java
@WebFilter("/user")
public class UserFilter implements Filter {
```

注册listener：使用**@WebListener**注解

```java
@WebListener
public class UserListener implements ServletContextListener {
```

### 2）配置类

Shared libraries（共享库） / runtimes pluggability（运行时插件能力）

1、Servlet容器启动会扫描，当前应用里面每一个jar包的
​	ServletContainerInitializer的实现
2、提供ServletContainerInitializer的实现类；
​	必须绑定在，META-INF/services/javax.servlet.ServletContainerInitializer
​	文件的内容就是ServletContainerInitializer实现类的全类名；

总结：容器在启动应用的时候，会扫描当前应用每一个jar包里面
META-INF/services/javax.servlet.ServletContainerInitializer
指定的实现类，启动并运行这个实现类的方法；传入感兴趣的类型；

ServletContainerInitializer；
@HandlesTypes；

**使用步骤：**

写一个类，实现ServletContainerInitializer接口

servlet容器在启动时，会扫描所有jar包下META-INF下的services下javax.servlet.ServletContainerInitializer文件

加载文件里写的ServletContainerInitializer实现类的全限定名

#### 1.写一个实现类

```java
@HandlesTypes(UserService.class)  //配合onStartup方法的参数Set<Class<?>> c 使用
public class MyServletContainerInitializer implements ServletContainerInitializer {

    @Override
    //Set<Class<?>> c  ：需要和类上的@HandlesTypes(UserService.class)注解配合使用
    //可以获取@HandlesTypes()中参数的所有后代（如果是接口，也会获取实现类或者子接口）
    //ServletContext ctx  ：代表一个servlet容器（Tomcat），可以用来注册三大组件
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("MyServletContainerInitializer. . . onStartup. . . ");
        //遍历@HandlesTypes(UserService.class)，中参数类型的所有后代
        for (Class<?> aClass : c) {
            System.out.println(aClass);
        }
        //添加servlet，用名字，servlet对象做参数方式
        ServletRegistration.Dynamic userServlet = ctx.addServlet("userServlet", new UserServlet());
        //添加映射关系
        userServlet.addMapping("/user");

        //添加filter，用名字，过滤器类型全限定名做参数的方式
        FilterRegistration.Dynamic userFilter = ctx.addFilter("userFilter", "xyz.thishome.filter.UserFilter");
        //添加过滤级别，是否匹配之后，拦截路径
        userFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/user");

        //添加listener，无需添加映射关系
        ctx.addListener(UserListener.class);
    }
}
```

#### 2.创建加载文件

这里为了模拟，需要在类路径（资源文件夹resources）下创建：

META-INF/services/javax.servlet.ServletContainerInitializer

在文件里写上ServletContainerInitializer实现类的全限定名

```javax.servlet.ServletContainerInitializer
xyz.thishome.bean.MyServletContainerInitializer
```

### 3）补充一种

ServletContextListener实现类也可以来注册组件，

**ServletContextListener**实现类的**contextInitialized**方法和**contextDestroyed**方法，有一个参数**ServletContextEvent**，可以获取**ServletContext**对象，注册三大组件

```java
public class UserListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("UserListener. . . contextInitialized. . . ");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //获取ServletContext对象，可以注册组件
        ServletContext servletContext = sce.getServletContext();
        System.out.println("UserListener. . . contextDestroyed. . . ");
    }
}
```

##  2.整合springMVC

1、web容器在启动的时候，会扫描每个jar包下的META-INF/services/javax.servlet.ServletContainerInitializer
2、加载这个文件指定的类SpringServletContainerInitializer
3、spring的应用一启动会加载感兴趣的WebApplicationInitializer接口的下的所有组件；
4、并且为WebApplicationInitializer组件创建对象（组件不是接口，不是抽象类）
​	1）、AbstractContextLoaderInitializer：创建根容器；createRootApplicationContext()；
​	2）、AbstractDispatcherServletInitializer：
​			创建一个web的ioc容器；createServletApplicationContext();
​			创建了DispatcherServlet；createDispatcherServlet()；
​			将创建的DispatcherServlet添加到ServletContext中；
​				getServletMappings();
​	3）、AbstractAnnotationConfigDispatcherServletInitializer：注解方式配置的DispatcherServlet初始化器
​			创建根容器：createRootApplicationContext()
​					getRootConfigClasses();传入一个配置类
​			创建web的ioc容器： createServletApplicationContext();
​					获取配置类；getServletConfigClasses();
​	
总结：
​	以注解方式来启动SpringMVC；继承AbstractAnnotationConfigDispatcherServletInitializer；
实现抽象方法指定DispatcherServlet的配置信息；



**实现：**

导入spring-webmvc包

这里以注解方式为例

### 1.写两个配置类

父子容器，spring容器包含业务方面的配置，springMVC容器包含页面相关配置

spring配置类

```java
@ComponentScan(value = {"xyz.thishome"},excludeFilters = {
        //根容器，父容器，不包含controller
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
})
public class RootConfig {
}
```



springMVC配置类

```java
@ComponentScan(value = {"xyz.thishome"}, includeFilters = {
        //web容器，子容器，只包含controler
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
}, useDefaultFilters = false)
public class WebConfig {
}
```



### 2.写一个"web.xml配置类"

继承AbstractAnnotationConfigDispatcherServletInitializer抽象类

~~~java
public class MyWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    //全局根容器（spring容器，父容器）
    protected Class<?>[] getRootConfigClasses() {
        //这里返回配置类的数组
        return new Class[]{RootConfig.class};
    }

    @Override
    //web容器(springMVC容器，子容器)
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebConfig.class};
    }

    @Override
    //dispatcherServlet的映射路径配置
    protected String[] getServletMappings() {
        //所有但不包括.jsp请求
        return new String[]{"/"};
    }
}
~~~

然后所有的配置就都生效了。

## 3.定制mvc

1）、@EnableWebMvc:开启SpringMVC定制配置功能；
​	<mvc:annotation-driven/>；

2）、配置组件（视图解析器、视图映射、静态资源映射、拦截器。。。）

​	extends WebMvcConfigurerAdapter  

（也可以实现WebMvcConfigurer接口，但是需要实现很多mvc功能的配置，不是我们都需要的，WebMvcConfigurerAdapter  类实现了WebMvcConfigurer接口，把所有需要实现的方法写成空方法，需要哪个功能重写对应方法即可）

```java
@ComponentScan(value = {"xyz.thishome"}, includeFilters = {
        //web容器，子容器，只包含controler
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
}, useDefaultFilters = false)
@Configuration
//相当于mvc:annotation-driven，开启SpringMVC高级功能
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
    @Override
    //配置视图解析器
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver resourceViewResolver = new InternalResourceViewResolver();
        resourceViewResolver.setPrefix("/WEB-INF/pages/");
        resourceViewResolver.setSuffix(".jsp");
        registry.viewResolver(resourceViewResolver);
    }

    @Override
    //相当于mvc:default-servlet-handler，SpringMVC处理不了的请求交给tomcat处理（静态资源）
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    //........
    
}
```

## 4.异步处理

​	在浏览器向服务器发出一个请求时，服务器会从线程池中获取一个线程去响应，如果有一个业务逻辑需要执行很长时间才能完成，那么这个线程就会一直被占用，无法得到释放，无法响应更多的请求。异步请求就是，响应的线程可以新开一个线程，把比较耗时的线程交给这个线程执行，而主线程直接返回，可以直接去响应其他的请求，副线程执行结束后返回结果即可。可以用我们自己的线程池去维护它，不用Tomcat的线程，这里为了方便演示，就只展示异步处理。

### 1）原生的异步处理

==一定要在@webServlet注解上把asyncSupported属性设置为true开启异步处理==，否则会报异常

```java
@WebServlet(value = "/book", asyncSupported = true)
public class BookServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("主线程开始" + Thread.currentThread().getName() +
                "==>" + System.currentTimeMillis());
		//获取一个异步处理对象
        AsyncContext asyncContext = req.startAsync();
		//开启异步处理的线程
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                System.out.println("副线程开始" + Thread.currentThread().getName() +
                        "==>" + System.currentTimeMillis());
                try {
                    //业务逻辑
                    sayHello();
                    //在请求中获取异步处理全局对象
                    AsyncContext context = req.getAsyncContext();
                    //表示异步处理调用结束
                    context.complete();
                    //获取HttpServletResponse对象
                    context.getResponse();
                    //返回需要返回的结果
                    context.getResponse().getWriter().write("success");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("副线程结束" + Thread.currentThread().getName() +
                        "==>" + System.currentTimeMillis());
            }
        });

        System.out.println("主线程结束" + Thread.currentThread().getName() +
                "==>" + System.currentTimeMillis());
    }

    public void sayHello() throws Exception {

        Thread.sleep(3000);

    }

}
```

执行结果，控制台打印：
主线程立即结束，副线程去执行业务逻辑，这主副线程使用的时同一个线程池，SpringMVC为我们提供了一个线程池

~~~
主线程开始http-bio-8080-exec-1==>1545299017876
主线程结束http-bio-8080-exec-1==>1545299017889
副线程开始http-bio-8080-exec-4==>1545299017889
副线程结束http-bio-8080-exec-4==>1545299020900
~~~

### 2）整合springMVC

~~~java
@Controller
public class AsyncController {

    @RequestMapping("/async")
    @ResponseBody    
    //返回值是Callable类型，SpringMVC会特殊处理，String类型为请求具体返回值类型
    public Callable<String> hello() {
        System.out.println("主线程开始" + Thread.currentThread().getName() +
                "==>" + System.currentTimeMillis());
        //创建了一个执行异步请求的对象，实现其异步执行的方法，返回值为真正的请求返回类型
        //仅仅创建这个异步执行对象，并未执行
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("副线程开始" + Thread.currentThread().getName() +
                        "==>" + System.currentTimeMillis());
                try {
                    sayHello();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("副线程结束" + Thread.currentThread().getName() +
                        "==>" + System.currentTimeMillis());
                return "sucess";
            }
        };
        System.out.println("主线程结束" + Thread.currentThread().getName() +
                "==>" + System.currentTimeMillis());
        //返回这个异步执行对象给SpringMVC，主线程退出，SpringMVC自行执行副线程
        return callable;
    }

    public void sayHello() throws Exception {

        Thread.sleep(3000);

    }
}
~~~

执行结果：

~~~
主线程开始http-bio-8080-exec-1==>1545358526025
主线程结束http-bio-8080-exec-1==>1545358526026
副线程开始MvcAsync1==>1545358526042
副线程结束MvcAsync1==>1545358529042
~~~

执行流程

~~~java
1、控制器返回Callable
	  2、Spring异步处理，将Callable 提交到 TaskExecutor 使用一个隔离的线程进行执行
	  3、DispatcherServlet和所有的Filter退出web容器的线程，但是response 保持打开状态；
	  4、Callable返回结果，SpringMVC将请求重新派发给容器，恢复之前的处理；
	  5、根据Callable返回的结果。SpringMVC继续进行视图渲染流程等（从收请求-视图渲染）。
	  
	  preHandle.../springmvc-annotation/async01
		主线程开始...Thread[http-bio-8081-exec-3,5,main]==>1513932494700
		主线程结束...Thread[http-bio-8081-exec-3,5,main]==>1513932494700
		=========DispatcherServlet及所有的Filter退出线程============================
		
		================等待Callable执行==========
		副线程开始...Thread[MvcAsync1,5,main]==>1513932494707
		副线程开始...Thread[MvcAsync1,5,main]==>1513932496708
		================Callable执行完成==========
		
		================再次收到之前重发过来的请求========
		preHandle.../springmvc-annotation/async01
		postHandle...（Callable的之前的返回值就是目标方法的返回值）
		afterCompletion...
		
		异步的拦截器:
			1）、原生API的AsyncListener
			2）、SpringMVC：实现AsyncHandlerInterceptor；
~~~

### 3）开发中的使用

在开发中，可能响应请求的应用，和处理业务逻辑的应用是分离的，比如门户系统，和服务系统

这种情况就会需要使用另一种解决方式，在这里只是模拟

```java
@Controller
public class DeferredResultController {

    @RequestMapping("/createOrder")
    @ResponseBody
    //泛型中类型是请求实际返回类型
    public DeferredResult<Object> createOrder() {
        //设置响应时间，和如果超过响应时间发出的错误对象，这里为一个字符串
        DeferredResult deferredResult = new DeferredResult(3000L, "服务器响应超时！");
        //保存到容器（队列）中
        DeferredResultQueue.save(deferredResult);
        //返回给springMVC，主线程结束，等待setResult()方法触发，再次请求，返回结果
        return deferredResult;
    }

    @RequestMapping("/create")
    @ResponseBody
    public String create() {
        String s = UUID.randomUUID().toString();
        //从队列中取出，设置请求响应结果，一旦执行这个操作，/createOrder请求就会结束，返回结果
        DeferredResultQueue.getResult().setResult(s);
        return "sucess：" + s;
    }

}
```

创建的临时容器类DeferredResultQueue

```java
public class DeferredResultQueue {
    private static Queue<DeferredResult> queue = new ConcurrentLinkedQueue();
    public static void save(DeferredResult result) {
        queue.add(result);
    }
    public static DeferredResult getResult() {
        return queue.poll();
    }
}
```

结果：

先发出createOrder请求，三秒内，发出create请求，会得到结果：

```
/createOrder：
5a8eda26-470b-499e-a802-c9f3e101d078
```

```
/create：
sucess：5a8eda26-470b-499e-a802-c9f3e101d078
```

如果发出createOrder请求，三秒内，create请求未响应，会得到结果：

```
服务器响应超时！
```



# 补充

## XXX组件

1. **@Enable**XXX：注解方式开启spring的某一项功能，如**@EnableAspectJAutoProxy**，开启注解版的aop模式
2. XXX**Aware**：注入某一个spring底层组件，需要实现的接口，如**ApplicationContextAware**，注入IOC容器
3. ==XXX**Processor**==：后置处理器，如**AutowiredAnnotationBeanPostProcessor**，自动装配底层就是使用后置处理器。Aware也都是用后置处理器来实现功能的。
4. **@Enable**XXX：开启某一项功能，如**@EnableTransactionManagement**开启基于注解的事务功能

## 装配spring底层组件

如BeanFactory

1.可以需要装配的组件类实现 BeanFactoryAware，在实例化该组件之后，初始化之前，会调用该方法，把BeanFactory传入，在组件内定义一个属性进行接收并使用即可。

```java
public class RedService implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }
}
```

2.可以直接@Autowired自动装配

```java
public class RedService implements BeanFactoryAware {

    @Autowired
    private BeanFactory beanFactory;

}
```

二者区别

​	使用实现**BeanFactoryAware**方式，**缺点**：复杂，**优点**：可以在方法中写一些其他逻辑，该方法是在所有Bean初始化之前被调用的，可以对BeanFactory进行一些操作。

​	使用@**Autowired**自动装配，**缺点**：给了一个完全成型的BeanFactory，不能进行特殊处理。**优点**，简单，灵活，更方便，满足大多需求

### 可以@Autowired的组件：

1、**BeanFactory**       //Bean工厂

2、**ResourceLoader**   //资源加载器

3、**ApplicationEventPublisher**  ->  **applicationEventMulticaster**    //容器事件多播器

4、**ApplicationContext**    //全局应用程序

5、**ConfigurableEnvironment**  --> **environment**     //环境信息对象，可以获取系统和java环境变量

​	6、**Map<String, Object>** --> **systemProperties**       //java属性变量

​	7、**Map<String, Object>** --> **systemEnvironment**     //系统环境变量

8、**MessageSource  -->  messageSource**      //获取国际化配置文件的值

