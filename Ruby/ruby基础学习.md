# Ruby

## 一、运行第一个ruby程序

### 1.ruby环境搭建

ruby官网：http://www.ruby-lang.org/zh_cn/

可以找到Windows版本的安装，直接安装即可

最好是devkit版

windows中配置环境变量

- RUBY_HOME=rubyinstaller_dir （实际的ruby所在路径）
- 在path中添加 %RUBY_HOME%\bin
- 在命令行下运行 ruby --version，如果显示ruby版本信息说明安装正常。

### 2.helloworld

打开vscode，新建helloworld.rb

~~~Ruby
puts "hello world!"
~~~

使用ruby helloworld.rb指令运行文件

## 二、ruby基础语法

### 1.函数

#### 1）无参数

~~~ruby
#定义一个函数
def sayHello
   puts "hello"
end
#调用函数
sayHello
~~~

#### 2）有参数

~~~ruby
def sayHello(name)
    puts "hello #{name}"
end
sayHello("小明")
~~~

#### 3）默认参数

如果调用方没有传入参数，给一个默认的参数值

~~~ruby
def sayHello(name = "小明")
    puts "hello #{name}"
end
sayHello
~~~

### 2.对象

#### 1）简单对象创建及使用

~~~ruby
#创建一个类
class Person
    #构造器
    def initialize(name = "小明")
       #@name表示实例变量，即对象属性
        @name = name
    end
    def showName
        puts "#{@name}"
    end
end

#创建一个对象
xiaoming = Person.new
#调用方法
xiaoming.showName

xiaohong = Person.new("小红")
xiaohong.showName

daming = Person.new("大明")
daming.showName
~~~

#### 2）对象通用方法使用

`instance_methods(all:bool)`：列出对象（类）内部的方法，参数为是否输出所有的方法（是否包含通用方法）

`respond_to?`：调用对象的方法/属性是否可用

`send`：执行对象的方法

#### 3）对象属性

attr_accessor：定义可存取对象属性

通过attr_accessor定义的属性相当于Java的public属性，可以被外部访问

不使用attr_accessor定义的属性相当于Java的private属性，只能在对象内部使用，不可以通过外部直接访问

普通属性可以定义在任何地方，@定义的属性为成员属性，@@定义的属性为静态属性

~~~ruby
class Person
    #这里定义两个对外公开的属性
    attr_accessor :name, :age
    def initialize(name , age , sex)
        @name = name
        @age = age
        @sex = sex
    end
    def show
        puts "name:#{name}"
        puts "age:#{age}"
        puts "sex:#{sex}"
    end
end

lucy = Person.new("lucy",23,"男")

puts lucy.name
puts lucy.age
#这里会报错，因为attr_accessor中为定义sex属性
puts lucy.sex
~~~

### 3.数组

~~~ruby
#定义一个数组
array = ["小红" ,"小黄" ,"小花"]
#打印数组
puts array
puts "-------------------"

#遍历数组
array.each do |name|
    puts "你好，#{name}"
end
puts "-------------------"

#遍历数组加索引，索引下标从0开始
array.each_with_index do |name,i|
    puts "#{i} - #{name}"
end
puts "-------------------"

#把数组转化成一行字符串，用指定字符分隔
puts array.join(",")

#范围遍历 输出10-15
#（范围 (1..5) 意味着它包含值 1, 2, 3, 4, 5，范围 (1...5) 意味着它包含值 1, 2, 3, 4 。）
(10..15).each do |n|
    print n, ' '
end
~~~

### 4.注释

~~~ruby
#单行注释

=begin
多行注释
=end

__end__
end下面的全是注释
~~~

### 5.操作符

\+     \-       *       /       %       \*\*  

|    符号    |           解释           |
| :--------: | :----------------------: |
| 运算操作符 |                          |
|     +      |           加法           |
|     -      |           减法           |
|     *      |           乘法           |
|     /      |           除法           |
|     %      |           求余           |
|     **     | 次方（2**3表示2的3次方） |
| 比较运算符 |                          |
|     ==     |           等于           |
|     !=     |          不等于          |
|     >      |           大于           |
|    \>=     |         大于等于         |
|     <      |           小于           |
|     <=     |         小于等于         |
| 逻辑运算符 |                          |
|     &&     |          逻辑与          |
|    \|\|    |          逻辑或          |
|     !      |          逻辑非          |

### 6.字符串运算

+：字符串拼接

<<：字符串带入

*：字符串循环

~~~ruby
a = "hello"
b = "小红"
c = " "

#字符串拼接
puts a + c + b #hello 小红

#字符串带入，拼接字符串，并把新字符串赋值到第一个变量，后面的字符串不会发生改变
a << c << b
puts a   #hello 小红

#字符串乘法
puts (a + "\n") * 3  #把a换行输出三遍
~~~

### 7.引号

双引号：中的特殊占位符会被转义

单引号：中的字符串原样输出

大括号使用（运算式）：在双引号中可使用，单引号无效

### 8.键值对

hash和类似于json，两种方式

~~~ruby
#哈希
xiaoming = {
    "name" => "小明",
    "age" => 18,
    "sex" => "男"
}

puts xiaoming
puts "name：" + xiaoming["name"]
puts "age：" + xiaoming["age"].to_s
puts "sex：" + xiaoming["sex"]

puts "-----------"

#类似json的使用，常用
xiaohong = {
    name: "小红",
    age: 18,
    sex: "女"
}
puts xiaohong 
puts "name：" + xiaohong[:name]
puts "age：" + xiaohong[:age].to_s
puts "sex：" + xiaohong[:sex]
~~~

### 9.类型转换

to_i：转化为整数

to_s：转化为字符串

to_f：转化为浮点数

to_i和to_f可以直接操作数值

~~~ruby 
a = "1"
puts a.to_i
~~~

### 10.class

#### 1、静态方法

self表示静态方法，ruby中的静态方法只能通过类调用，不能通过对象调用，调用方式为，类.方法，或者类::方法

~~~ruby
class Person
    def initialize(name ,age)
        @name = name
        @age = age
    end

    def show
        puts "name：#{@name}\nage：#{@age}"
    end

    #静态方法，只能通过类名调用
    def self.say
        puts "I am Person"
    end
end

xiaoming = Person.new("小明" ,18)
xiaoming.show

#调用静态方法的两种方式
Person.say
Person::say
~~~

#### 2、类继承

~~~ruby
class Person
  #静态属性
  @@version = 1.0
  #普通属性
  @name = ""
  @age = 0

  def initialize(name, age)
    @name = name
    @age = age
  end

  def paramPuts
    puts "name：#{@name}"
    puts "age：#{@age}"
  end

  #静态方法
  def self.show
    puts "I am a person"
  end

  def out
    puts "this is a method"
  end

end


class Student < Person

  def initialize(name, age, sex)
    #调用父类构造方法
    super(name, age)
    @sex = sex
  end

  def paramPuts
    #调用父类重载方法
    super
    puts "sex：#{@sex}"
    #调用父类普通方法
    out
  end

end

xiaoming = Student.new("小明", 19, "男")

xiaoming.paramPuts

Student.show
~~~

### 11.模板

模板不能实例化

模板方法关键字：module_function，和self静态方法作用一致，模板的静态方法，include模板的类不能够使用

~~~ruby
module Animal
  #静态属性，类似public static
  Version = 1.0

  def hello(name)
    "hello " + name
  end

  def self.run
    "我会跑"
  end

  def eat
    "我会吃"
  end
  #把eat方法升级为静态方法
  module_function :eat

end

class Pig
  include Animal

end

puts Animal::run
puts Animal.run

puts Animal::Version
#错误写法 puts Animal.Version

puts Animal.eat
puts Animal::eat

#可调用模板静态属性，不能调用模板中的静态方法
puts Pig::Version

pig = Pig.new
puts pig.hello("小明")
~~~



