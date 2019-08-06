# 一、Docker介绍

## 1.什么是Docker

Docker是基于Go语言实现的云开源项目。
Docker的主要目标是“Build，Ship and Run Any App,Anywhere”，也就是通过对应用组件的封装、分发、部署、运行等生命周期的管理，使用户的APP（可以是一个WEB应用或数据库应用等等）及其运行环境能够做到“一次封装，到处运行”。

Linux 容器技术的出现就解决了这样一个问题，而 Docker 就是在它的基础上发展过来的。将应用运行在 Docker 容器上面，而 Docker 容器在任何操作系统上都是一致的，这就实现了跨平台、跨服务器。只需要一次配置好环境，换到别的机子上就可以一键部署好，大大简化了操作

==解决了运行环境和配置问题软件容器，方便做持续集成并有助于整体发布的容器虚拟化技术。==

## 2.Docker能做什么

### 1）传统的虚拟化技术

​	虚拟机（virtual machine）就是带环境安装的一种解决方案。
​	它可以在一种操作系统里面运行另一种操作系统，比如在Windows 系统里面运行Linux 系统。应用程序对此毫无感知，因为虚拟机看上去跟真实系统一模一样，而对于底层系统来说，虚拟机就是一个普通文件，不需要了就删掉，对其他部分毫无影响。这类虚拟机完美的运行了另一套系统，能够使应用程序，操作系统和硬件三者之间的逻辑不变。  

虚拟机的缺点：
1    资源占用多               2    冗余步骤多                 3    启动慢

### 2）Docker虚拟化技术

由于前面虚拟机存在这些缺点，Linux 发展出了另一种虚拟化技术：Linux 容器（Linux Containers，缩写为 LXC）。

Linux 容器不是模拟一个完整的操作系统，而是对进程进行隔离。有了容器，就可以将软件运行所需的所有资源打包到一个隔离的容器中。容器与虚拟机不同，不需要捆绑一整套操作系统，只需要软件工作所需的库资源和设置。系统因此而变得高效轻量并保证部署在任何环境中的软件都能始终如一地运行。

比较了 Docker 和传统虚拟化方式的不同之处：

- 传统虚拟机技术是虚拟出一套硬件后，在其上运行一个完整操作系统，在该系统上再运行所需应用进程；
- 而容器内的应用进程直接运行于宿主的内核，容器内没有自己的内核，而且也没有进行硬件虚拟。因此容器要比传统虚拟机更为轻便。

每个容器之间互相隔离，每个容器有自己的文件系统 ，容器之间进程不会相互影响，能区分计算资源。

## 3.Docker优点

- 更快速的应用交付和部署

- 更便捷的升级和扩缩容

- 更简单的系统运维

- 更高效的计算资源利用



#二、Docker安装

## 1.Docker三要素

### 1）镜像

Docker 镜像（Image）就是一个只读的模板。镜像可以用来创建 Docker 容器，一个镜像可以创建很多容器。

### 2）容器

Docker 利用容器（Container）独立运行的一个或一组应用。容器是用镜像创建的运行实例。

它可以被启动、开始、停止、删除。每个容器都是相互隔离的、保证安全的平台。

可以把容器看做是一个简易版的 Linux 环境（包括root用户权限、进程空间、用户空间和网络空间等）和运行在其中的应用程序。

容器的定义和镜像几乎一模一样，也是一堆层的统一视角，唯一区别在于容器的最上面那一层是可读可写的。

### 3）仓库


仓库（Repository）是集中存放镜像文件的场所。
仓库(Repository)和仓库注册服务器（Registry）是有区别的。仓库注册服务器上往往存放着多个仓库，每个仓库中又包含了多个镜像，每个镜像有不同的标签（tag）。

仓库分为公开仓库（Public）和私有仓库（Private）两种形式。
最大的公开仓库是 Docker Hub(https://hub.docker.com/)，
存放了数量庞大的镜像供用户下载。国内的公开仓库包括阿里云 、网易云 等

### 4）总结

需要正确的理解仓储/镜像/容器这几个概念:

 Docker 本身是一个容器运行载体或称之为管理引擎。我们把应用程序和配置依赖打包好形成一个可交付的运行环境，这个打包好的运行环境就似乎 image镜像文件。只有通过这个镜像文件才能生成 Docker 容器。image 文件可以看作是容器的模板。Docker 根据 image 文件生成容器的实例。同一个 image 文件，可以生成多个同时运行的容器实例。

*  image 文件生成的容器实例，本身也是一个文件，称为镜像文件。

*  一个容器运行一种服务，当我们需要的时候，就可以通过docker客户端创建一个对应的运行实例，也就是我们的容器

*  至于仓储，就是放了一堆镜像的地方，我们可以把镜像发布到仓储中，需要的时候从仓储中拉下来就可以了。

## 2.Docker安装步骤

### 1）前提条件

目前，CentOS 仅发行版本中的内核支持 Docker。

Docker 运行在 CentOS 7 上，要求系统为64位、系统内核版本为 3.10 以上。

Docker 运行在 CentOS-6.5 或更高的版本的 CentOS 上，要求系统为64位、系统内核版本为 2.6.32-431 或者更高版本。

**查看自己的内核**

**uname -r**

### 2）安装docker的依赖

安装epel-release

**yum install -y epel-release**

### 3）安装docker

**yum install -y docker-io**

### 4）配置文件位置

**/etc/sysconfig/docker**

### 5）开启Docker服务

**service docker start**

### 6）验证

**docker version**

## 3.阿里云镜像配置

### 1）登录阿里云官网

找到容器镜像服务，==必须要登录阿里云账号==

![1545738330227](images\阿里云镜像服务位置.png)

**复制自己的加速器网址**

![1545738445858](images\镜像加速器网址位置.png)

### 2）修改配置文件

修改docker配置文件

`vim   /etc/sysconfig/docker`

添加一行代码`other_args="--registry-mirror=https://h2fcz5hh.mirror.aliyuncs.com"`

![1545738235786](images\阿里云镜像加速配置.png)

## 4.HelloWorld

docker run hello-world

出现

省略。 。 。 

Hello from Docker!
This message shows that your installation appears to be working correctly.

省略。 。 。

# 三、Docker常用命令

## 1.帮助命令

|      命令       |         含义         |
| :-------------: | :------------------: |
|  docker   info  | 显示docker的信息描述 |
| docker  version | 显示docker的版本信息 |
| docker   --help |   docker的帮助命令   |

## 2.镜像命令

|             命令              |            含义            |
| :---------------------------: | :------------------------: |
|         docker images         | 查看当前docker中所有的镜像 |
|    docker  search  镜像名     |   搜索DockerHub上的镜像    |
| docker  pull  镜像名【:版本】 |          下载镜像          |
|     docker rmi 镜像名/ID      |          删除镜像          |

### 1）docker images

查看当前docker中所有的镜像

![1545744662560](images\5C1545744662560.png)

#### 1、各个选项说明:

REPOSITORY：表示镜像的仓库源

TAG：镜像的标签

IMAGE ID：镜像

IDCREATED：镜像创建时间

SIZE：镜像大小

同一仓库源可以有多个 TAG，代表这个仓库源的不同个版本，我们使用 REPOSITORY:TAG 来定义不同的镜像。
如果你不指定一个镜像的版本标签，例如你只使用 ubuntu，docker 将默认使用 ubuntu:latest 镜像

#### 2、OPTIONS说明：

可以有四个参数

-a :列出本地所有的镜像（含中间映像层）

![1545745141921](images\5C1545745141921.png)

`-q` :只显示镜像ID。

![1545745210223](images\C1545745210223.png)可以复合参数

`--digests` :显示镜像的摘要信

![1545745345758](images\1545745345758.png)

`--no-trunc `:显示完整的镜像信息

![1545745434801](images\5C1545745434801.png)

### 2）docker  search  镜像名

搜索DockerHub上的镜像

![1545746000426](images\1545746000426.png)

#### OPTIONS说明：

**`-s `: 列出收藏数不小于指定值的镜像。重要**

![1545746118409](images\1545746118409.png)

`--no-trunc `: 显示完整的镜像描述。

![1545746224786](images\1545746224786.png)

`--automated` : 只列出 automated build类型的镜像；

![1545746276443](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1545746276443.png)

### 3）docker  pull  镜像名

`docker  pull  tomcat:8.5`

下载镜像，如果不指定版本号，默认为latest（最新版）

![1545746837851](images\1545746837851.png)



### 4）docker rmi

`rmi代表remove image  移除镜像`

#### 1、删除单个

`docker rmi 镜像名/ID`

![1545785524825](images\1545785524825.png)  -f参数表示强制删除

#### 2、删除多个

`docker rmi 镜像名1/ID1  镜像名2/ID2   `

空格隔开

![1545785570695](images\1545785570695.png) 

#### 3、删除全部

`docker  rmi  $(docker  images  -qa)`

类似于一个子命令（-q也可以，-q表示所有镜像ID，-qa表示所有镜像及分层ID）

## 3.容器基础命令

| 命令                     | 含义                   |
| :----------------------- | :--------------------- |
| docker run  镜像名       | 利用镜像创建一个容器   |
| docker  ps 【参数】      | 查看docker中运行的容器 |
| docker start 容器id/名   | 启动容器               |
| docker restart 容器id/名 | 重启容器               |
| docker stop 容器id/名    | 停止容器               |
| docker kill 容器id/名    | 强制停止容器           |
| docker rm 容器id/名      | 删除容器               |

### 1）docker run 镜像名

创建一个容器，如果没有该镜像，会先到云端下载一个镜像，再创建容器，如果云端镜像不存在，报错

```linux
docker run [OPTIONS] 镜像名 [COMMAND] [ARG...]
```

#### 1、 OPTIONS说明（重要）

有些是一个减号，有些是两个减号

`--name="容器新名字" ` :  为容器指定一个名称；
`-d ` :   后台运行容器，并返回容器ID，也即启动守护式容器；
`-i ` ： 以交互模式运行容器，通常与 -t 同时使用；
`-t  `： 为容器重新分配一个伪输入终端，通常与 -i 同时使用；
`-P ` :   随机端口映射（大写P）；
`-v`  ： 添加数据容器卷；
`-p`  :   指定端口映射，有以下四种格式
​	 `ip:hostPort:containerPort`
​	 `ip::containerPort`
​         `hostPort:containerPort`
​	 `containerPort`

下载一个centos作为实验

![1545787569178](images\1545787569178.png) 

创建了一个centos容器，以交互模式，并进入伪终端

![1545787446619](images\1545787446619.png)

**在docker上启动centos系统，并进入终端**



docker打开Tomcat

`docker run --name tomcat1 -p 8081:8080 tomcat:8.5`

需要加上p参数，指定在宿主机上的docker占用端口号，映射到docker中容器的端口号

8081使用的是宿主机的端口号，被docker占用

8080是docker的Tomcat容器中系统的端口号，被docker中tomcat容器占用

#### 2、交互式容器

使用镜像centos:latest以交互模式启动一个容器,在容器内执行/bin/bash命令。

`docker run -it centos /bin/bash `

### 2）docker  ps 【参数】

查看当前docker运行的容器，通过添加参数，可以查看已经停止运行的容器，和之前运行的容器

#### OPTIONS说明（重要）

-a   :  列出当前所有正在运行的容器+历史上运行过的
-l    :  显示最近创建的容器。
-n ： 显示最近n个创建的容器。 后面跟数量

![1545788476387](images\1545788476387.png) 

-q  :   静默模式，只显示容器编号。

![1545788437316](images\1545788437316.png) 

--no-trunc   :  不截断输出。

### 3）退出容器

1.exit

直接退出容器

2.ctrl+P+Q

容器不停止退出

### 4）启动/重启/停止容器

启动容器：docker start 容器ID或者容器名

重启容器：docker restart 容器ID或者容器名

停止容器：docker stop 容器ID或者容器名

强制停止容器：docker kill 容器ID或者容器名

### 5）删除容器

单个删除

docker rm 容器ID

批量删除，两种方式

docker rm -f $(docker ps -a -q)

docker ps -a -q | xargs docker rm

## 4.容器重要命令

| 命令                                                         | 含义                     |
| :----------------------------------------------------------- | :----------------------- |
| docker run -d 容器名                                         | 启动守护进程             |
| docker logs -f -t --tail 容器ID                              | 查看容器日志             |
| docker top 容器ID                                            | 查看容器中的进程         |
| docker inspect 容器ID                                        | 查看容器内部细节         |
| docker attach 容器ID                                         | 进入正在运行容器终端     |
| docker exec  -it 容器ID bashShell                            | 不进入终端直接对容器操作 |
| docker cp 容器id : 容器路径  主机路径                        | 把容器中文件拷贝到主机上 |
| docker commit -m=“描述信息” -a=“作者” 容器ID 镜像名:[标签名] | 提交容器为镜像           |
| docker build -f  Dockerfile文件路径 -t 新镜像名 .            | 构建容器                 |

### 1）启动守护进程

`docker run -d 容器名`

使用镜像centos:latest以后台模式启动一个容器

`docker run -d centos`

问题：然后`docker ps -a `进行查看, 会发现容器已经退出
很重要的要说明的一点: Docker容器后台运行,就必须有一个前台进程.
容器运行的命令如果不是那些一直挂起的命令（比如运行top，tail），就是会自动退出的。

这个是docker的机制问题，比如你的web容器，我们以nginx为例，正常情况下，我们配置启动服务只需要启动响应的service即可。例如
`service nginx start`
但是，这样做，nginx为后台进程模式运行，就导致docker前台没有运行的应用,
这样的容器后台启动后，会立即自杀因为他觉得他没事可做了.
所以，最佳的解决方案是，将你要运行的程序以前台进程的形式运行

### 2）查看容器日志

`docker logs -f -t --tail 容器ID`

*   -t 是加入时间戳
*   -f 跟随最新的日志打印
*   --tail 数字 显示最后多少条

### 3）查看容器中的进程/内部细节

查看进程：docker top 容器ID

查看内部细节：docker inspect 容器ID

### 4）进入正在运行的容器

#### 1、进入容器终端attach 

`docker attach f4d5c5e9c53e`

#### 2、获取终端结果exec 

可以不进入终端就对容器进行终端操作

`docker exec -it 容器ID bashShell`

后面可以跟一个shell编程（可以启动新进程），也可以跟linux指令

在这里我们并没有进入终端，并直接拿出结果返回给宿主机

![1545792797608](images\1545792797608.png) 

也可以使用exec 直接进入终端

![1545792948325](images\1545792948325.png) 

### 5）拷贝容器中文件到主机上

拷贝f4d5c5e9c53e容器中的/root/bai/bai.txt文件到宿主机/root/目录下

`docker cp f4d5c5e9c53e:/root/bai/bai.txt /root/`



## 5.总结

| 命令    | 含义                                                         |
| ------- | ------------------------------------------------------------ |
| build   | 通过 Dockerfile 定制镜像                                     |
| commit  | 提交当前容器为新的镜像                                       |
| attach  | 当前 shell 下 attach 连接指定运行镜像                        |
| cp      | 从容器中拷贝指定文件或者目录到宿主机中                       |
| create  | 创建一个新的容器，同 run，但不启动容器                       |
| diff    | 查看 docker 容器变化                                         |
| events  | 从 docker 服务获取容器实时事件                               |
| exec    | 在已存在的容器上运行命令                                     |
| export  | 导出容器的内容流作为一个 tar 归档文件[对应 import ]          |
| history | 展示一个镜像形成历史                                         |
| images  | 列出系统当前镜像                                             |
| import  | 从tar包中的内容创建一个新的文件系统映像[对应export]          |
| info    | 显示系统相关信息                                             |
| inspect | 查看容器详细信息                                             |
| kill    | kill 指定 docker 容器                                        |
| load    | 从一个 tar 包中加载一个镜像[对应 save]                       |
| login   | 注册或者登陆一个 docker 源服务器                             |
| logout  | 从当前 Docker registry 退出                                  |
| logs    | 输出当前容器日志信息                                         |
| port    | 查看映射端口对应的容器内部源端口                             |
| pause   | 暂停容器                                                     |
| ps      | 列出容器列表                                                 |
| pull    | 从docker镜像源服务器拉取指定镜像或者库镜像                   |
| push    | 推送指定镜像或者库镜像至docker源服务器                       |
| restart | 重启运行的容器                                               |
| rm      | 移除一个或者多个容器                                         |
| rmi     | 移除一个或多个镜像[无容器使用该镜像才可删除，否则需删除相关容器才可继续或 -f 强制删除] |
| run     | 创建一个新的容器并运行一个命令                               |
| save    | 保存一个镜像为一个 tar 包[对应 load]                         |
| search  | 在 docker hub 中搜索镜像                                     |
| start   | 启动容器                                                     |
| stop    | 停止容器                                                     |
| tag     | 给源中镜像打标签                                             |
| top     | 查看容器中运行的进程信息                                     |
| unpause | 取消暂停容器                                                 |
| version | 查看 docker 版本号                                           |
| wait    | 截取容器停止时的退出状态值                                   |



# 四、Docker镜像

## 1.Docker镜像介绍

镜像是一种轻量级、可执行的独立软件包，用来打包软件运行环境和基于运行环境开发的软件，它包含运行某个软件所需的所有内容，包括代码、运行时、库、环境变量和配置文件。

### 1）UnionFS（联合文件系统）

UnionFS（联合文件系统）：Union文件系统（UnionFS）是一种分层、轻量级并且高性能的文件系统，它支持**对文件系统的修改作为一次提交来一层层的叠加**，同时可以将不同目录挂载到同一个虚拟文件系统下(unite several directories into a single virtual filesystem)。Union 文件系统是 Docker 镜像的基础。镜像可以通过分层来进行继承，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像。

特性：一次同时加载多个文件系统，但从外面看起来，只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录

### 2） Docker镜像加载原理

   docker的镜像实际上由一层一层的文件系统组成，这种层级的文件系统UnionFS。
bootfs(boot file system)主要包含bootloader和kernel, bootloader主要是引导加载kernel, Linux刚启动时会加载bootfs文件系统，在Docker镜像的最底层是bootfs。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核。当boot加载完成之后整个内核就都在内存中了，此时内存的使用权已由bootfs转交给内核，此时系统也会卸载bootfs。

rootfs (root file system) ，在bootfs之上。包含的就是典型 Linux 系统中的 /dev, /proc, /bin, /etc 等标准目录和文件。rootfs就是各种不同的操作系统发行版，比如Ubuntu，Centos等等。 
。 
 平时我们安装进虚拟机的CentOS都是好几个G，为什么docker这里才200M？？

对于一个精简的OS，rootfs可以很小，只需要包括最基本的命令、工具和程序库就可以了，因为底层直接用Host的kernel，自己只需要提供 rootfs 就行了。由此可见对于不同的linux发行版, bootfs基本是一致的, rootfs会有差别, 因此不同的发行版可以公用bootfs。

### 3）分层的镜像

**tomcat镜像为例：**

![1545797080081](images\1545797080081.png)

### 4）为什么采用分层结构？

最大的一个好处就是 - 共享资源

比如：有多个镜像都从相同的 base 镜像构建而来，那么宿主机只需在磁盘上保存一份base镜像，
同时内存中也只需加载一份 base 镜像，就可以为所有容器服务了。而且==镜像的每一层都可以被共享。==

## 2.镜像特点

镜像的每一层都被共享，多个镜像可以用同一层镜像。

Docker镜像都是只读的
**当容器启动时，一个新的可写层被加载到镜像的顶部。**
**这一层通常被称作“容器层”，“容器层”之下的都叫“镜像层”。**

我们操作的都是最外层的容器层，容器层被分开存储，每个容器对应一份，做出写操作只会影响某一个容器

## 3.镜像commit

`docker commit -m=“提交的描述信息” -a=“作者” 容器ID 要创建的目标镜像名:[标签名]`

# 五、Docker容器数据卷

## 1.是什么

先来看看Docker的理念：
*  将运用与运行的环境打包形成容器运行 ，运行可以伴随着容器，但是我们对数据的要求希望是持久化的
*  容器之间希望有可能共享数据

Docker容器产生的数据，如果不通过docker commit生成新的镜像，使得数据做为镜像的一部分保存下来，
那么当容器删除后，数据自然也就没有了。

为了能保存数据在docker中我们使用卷。

==一句话：有点类似我们Redis里面的rdb和aof文件==

## 2.能干啥

 卷就是目录或文件，存在于一个或多个容器中，由docker挂载到容器，但不属于联合文件系统，因此能够绕过Union File System提供一些用于持续存储或共享数据的特性：

 卷的设计目的就是数据的持久化，完全独立于容器的生存周期，因此Docker不会在容器删除时删除其挂载的数据卷

特点：
1：数据卷可在容器之间共享或重用数据
2：卷中的更改可以直接生效
3：数据卷中的更改不会包含在镜像的更新中
4：数据卷的生命周期一直持续到没有容器使用它为止

==容器的持久化    容器间继承+共享数据==

## 3.怎么用

容器内添加

### 1）直接命令添加

命令：
​	 `docker run -it -v /宿主机绝对路径目录:/容器内目录 镜像名`

查看数据卷是否挂载成功

​	`docker inspect 容器ID`

容器和宿主机之间数据共享

容器停止退出后，主机修改后数据依然同步

命令(带权限) 

​	`docker run -it -v /宿主机绝对路径目录:/容器内目录:ro 镜像名`

例：

​	`docker run -it -v /myDataVolume:/DataVolumeContainer:ro centos:6.8`

==`-v`可以有多个==

### 2）DockerFile添加

1，根目录下新建mydocker文件夹并进入

2，写一个Dockerfile文件

可在Dockerfile中使用VOLUME指令来给镜像添加一个或多个数据卷

`VOLUME["/dataVolumeContainer","/dataVolumeContainer2","/dataVolumeContainer3"]`

说明：

出于可移植和分享的考虑，用`-v 主机目录:容器目录`这种方法不能够直接在Dockerfile中实现。
由于宿主机目录是依赖于特定宿主机的，并不能够保证在所有的宿主机上都存在这样的特定目录。

~~~dockerfile
FROM centos    #继承于centos镜像（在centos镜像外又包一层）
VOLUME ["/dataVolumeContainer1","/dataVolumeContainer2"]  #添加数据卷
CMD echo "finished,--------success1"        #打印刷新成功到控制台
CMD /bin/bash     #进入到终端
~~~

##### 3，使用Dockerfile构建一个镜像

`docker build -f  Dockerfile文件路径 -t 新镜像名 .`

例：

`docker build -f /mydocker/DockerFile -t dockerfile/centos:1.0 .`

（注意最后面有个点）

构建后，就可以使用`docker images`命令来查看该镜像

![1545815176925](images\1545815176925.png) 

4，创建一个容器，并进入控制台

`docker run -it dockerfile/centos:1.0`

![1545815244834](images\1545815244834.png) 

发现数据卷已经生成，但是还不知道数据卷在主机的对应位置

5，查看数据卷在宿主机的位置

查看容器的详细信息

`docker inspect d5615e579af0`

![1545815460289](images\1545815460289.png)



补充：Docker挂载主机目录Docker访问出现cannot open directory .: Permission denied
解决办法：在挂载目录后多加一个--privileged=true参数即可

`docker run -it -v /myDataVolume:/DataVolumeContainer centos:6.8  --privileged=true`

## 4.数据卷容器

命名的容器挂载数据卷，其它容器通过挂载这个(父容器)实现数据共享，挂载数据卷的容器，称之为数据卷容器

简言之，就是数据卷能够被继承，且有传递性 

`--volumes-from`表示继承一个容器的数据卷

```linux
创建一个有数据卷的容器doc1，a673ac8e4d54镜像为上面Dockerfile创建的镜像
[root@hadoop /]# docker run -it --name doc1 a673ac8e4d54
继承doc1的数据卷
[root@hadoop /]# docker run -it --name doc2 --volumes-from doc1 a673ac8e4d54
继承doc2的数据卷
[root@hadoop /]# docker run -it --name doc3 --volumes-from doc2 a673ac8e4d54
```

数据卷可以一直传递下去，所有继承数据卷的容器都会有数据卷对应的文件夹，并且全部共享

![1545819781200](images\1545819781200.png)

==结论：容器之间配置信息的传递，数据卷的生命周期一直持续到没有容器使用它为止==

# 六、DockerFile解析

## 1.是什么？

Dockerfile是用来构建Docker镜像的构建文件，是由一系列命令和参数构成的脚本。

每一个镜像都会对应一个Dockerfile文件。

**构建三步骤**

1. 编写Dockerfile文件

2. docker build

3. docker run 

以我们熟悉的CentOS为例 

~~~dockerfile
FROM scratch
MAINTAINER The CentOS Project <cloud-ops@centos.org>
ADD c68-docker.tar.xz /
LABEL name="CentOS Base Image" \
    vendor="CentOS" \
    license="GPLv2" \
    build-date="2016-06-02"

# Default command
CMD ["/bin/bash"]
~~~

## 2.DockerFile构建过程解析

###  1）DockerFIle基础知识

​	1：每条保留字指令都必须为大写字母且后面要跟随至少一个参数

​	2：指令按照从上到下，顺序执行

​	3：#表示注释

​	4：每条指令都会创建一个新的镜像层，并对镜像进行提交

### 2）Docker执行Dockerfile的大致流程

​	（1）docker从基础镜像运行一个容器

​	（2）执行一条指令并对容器作出修改

​	（3）执行类似docker commit的操作提交一个新的镜像层

​	（4）docker再基于刚提交的镜像运行一个新容器

​	（5）执行dockerfile中的下一条指令直到所有指令都执行完成

### 3）总结

从应用软件的角度来看，Dockerfile、Docker镜像与Docker容器分别代表软件的三个不同阶段，
*  Dockerfile是软件的原材料
*  Docker镜像是软件的交付品
*  Docker容器则可以认为是软件的运行态。
  Dockerfile面向开发，Docker镜像成为交付标准，Docker容器则涉及部署与运维，三者缺一不可，合力充当Docker体系的基石。

1 Dockerfile，需要定义一个Dockerfile，Dockerfile定义了进程需要的一切东西。Dockerfile涉及的内容包括执行代码或者是文件、环境变量、依赖包、运行时环境、动态链接库、操作系统的发行版、服务进程和内核进程(当应用进程需要和系统服务和内核进程打交道，这时需要考虑如何设计namespace的权限控制)等等;

2 Docker镜像，在用Dockerfile定义一个文件之后，docker build时会产生一个Docker镜像，当运行 Docker镜像时，会真正开始提供服务;

3 Docker容器，容器是直接提供服务的。

## 3.保留字指令

|    指令    | 含义                                                         |
| :--------: | :----------------------------------------------------------- |
|    FROM    | 基础镜像，当前新镜像是基于哪个镜像的<br>`FROM scratch`    scratch为所有镜像的跟镜像，类似java的Object类 |
| MAINTAINER | 镜像维护者的姓名和邮箱地址<br>`MAINTAINER The CentOS Project <cloud-ops@centos.org>` |
|    RUN     | 容器构建时需要运行的命令（linux指令）<br>`RUN mkdir /dat`    |
|   EXPOSE   | 当前容器对外暴露出的端口<br>`EXPOSE 6379`                    |
|  WORKDIR   | 指定在创建容器后，终端默认登陆的进来工作目录<br>`WORKDIR /data` |
|    ENV     | 用来在构建镜像过程中设置环境变量<br>`ENV MY_PATH /usr/mytest`<br/>这个环境变量可以在后续的任何RUN指令中使用，这就如同在命令前面指定了环境变量前缀一样；也可以在其它指令中直接使用这些环境变量，<br>比如：`WORKDIR $MY_PATH` |
|    ADD     | 将宿主机目录下的文件拷贝进镜像且ADD命令会**自动**处理URL和**解压tar压缩包**<br/>`ADD c68-docker.tar.xz /usr/local` |
|    COPY    | 类似ADD，拷贝文件和目录到镜像中。（不会自动解压压缩包）<br/>将从构建上下文目录中 <源路径> 的文件/目录复制到新的一层的镜像内的 <目标路径> 位置**语法：**`COPY src des`  或`COPY ["src", "dest"]` |
|   VOLUME   | 容器数据卷，用于数据保存和持久化工作<br/>`VOLUME ["/dataVolumeContainer1","/dataVolumeContainer2"]` |
|    CMD     | 指定一个容器启动时要运行的命令，Dockerfile 中可以有多个 CMD 指令，但只有最后一个生效，CMD 会被 `docker run `之后的参数替换（==在`docker run`指令执行后，会自动执行最后一个CMD指令==，如`CMD ["/bin/bash"]`run后进入到终端，==`docker run`后面也可以跟一个参数，用于run后执行的指令，会覆盖DockerFIle中的CMD指令==） |
| ENTRYPOINT | 指定一个容器启动时要运行的命令，ENTRYPOINT 的目的和 CMD 一样，都是在指定容器启动程序及参数，但是`docker run`后面的指令不会将其覆盖，会追加带ENTRYPOINT指令后例如：`ENTRYPOINT ls`   运行时加载-l参数`docker run ... -l`  就会出现`ls -l`的结果 |
|  ONBUILD   | 当构建一个被继承的Dockerfile时运行命令，父镜像在被子继承后父镜像的onbuild被触发 |
## 4.使用案例

### 1）自定义centos

官方版的centos是精简版的，非常精简，并没有vim编辑器，也没有ifconfig配置，默认登录路径是/（根目录）

自定义mycentos目的使我们自己的镜像具备如下：
​         登陆后的默认路径
​         vim编辑器
​         查看网络配置ifconfig支持

1，编写dockerfile2文件

~~~dockerfile
FROM centos:6.8  #继承centos:6.8

ENV MYPATH /tmp  #设置环境变量
WORKDIR $MYPATH  #设置进入终端默认目录

RUN yum -y install vim  #安装vim
RUN yum -y install net-tools  #安装net-tools

EXPOSE 80  #暴露80端口
CMD /bin/bash   #结束                        
~~~

2，利用dockerfile文件构建镜像

`docker build -f /mydocker/dockerfile2 -t mycentos:1.0 .`

### 2）CMD/ENTRYPOINT区别

`docker run`之后的参数会直接覆CMD，作为完整指令执行

`docker run `之后的参数会被当做参数传递给 ENTRYPOINT，之后形成新的命令组合

1，编写dockerfile3文件

~~~dockerfile
FROM centos:6.8  #继承centos:6.8
RUN yum -y install curl   #下载curl指令
CMD ["curl","-s","http://ip.cn"]   #相当于curl -s http://ip.cn
~~~

2，构建镜像

`docker build -f /mydocker/dockerfile3 -t getIp:1.0 .`

3，运行镜像

`docker run getIp:1.0`

就会自动执行`curl -s http://ip.cn`指令

**如果需要添加一个参数-i**（执行`curl -s -i http://ip.cn`）

尝试追加命令`docker run getIp:1.0 -i`

会报错，直接把`-i`当做一条指令来执行，覆盖dockerfile中CMD指令，相当于`CMD -i`

4，修改dockerfile，使用ENTRYPOINT

```dockerfile
FROM centos:6.8  #继承centos:6.8
RUN yum -y install curl   #下载curl指令
#这里改成ENTRYPOINT
ENTRYPOINT ["curl","-s","http://ip.cn"]   #相当于curl -s http://ip.cn
```

5，构建镜像

`docker build -f /mydocker/dockerfile3 -t getIp:1.1 .`

6，运行镜像

`docker run getIp:1.1 -i`

`-i`会被当做参数传递给 `ENTRYPOINT`，之后形成新的命令组合（追加到最后）

形成`curl -s http://ip.cn  -i`



### 3）实现Tomcat9

1，编写dockerfile文件

~~~dockerfile
FROM centos:6.8

#作者和邮箱
MAINTAINER baibai<baibai@qq.com>

#设置默认文件夹
ENV MYPATH /usr/local
WORKDIR $MYPATH

#解压Jdk，Tomcat    ADD，CP的文件必须和Dockerfile在同一目录下
ADD apache-tomcat-9.0.14.tar.gz /usr/local
ADD jdk-8u191-linux-x64.tar.gz /usr/local

#复制c.txt文件
COPY c.txt /usr/local/COPY.txt

#配置环境变量
ENV JAVA_HOME=/usr/local/jdk1.8.0_191
ENV CLASS_PATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV CATALINA_HOME=/usr/local/apache-tomcat-9.0.14
ENV CATALINA_BASE=/usr/local/apache-tomcat-9.0.14
ENV PATH $PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin

#暴露8080端口
EXPOSE 8080

#运行Tomcat，三种方式
#ENTRYPOINT ["usr/local/apache-tomcat-9.0.14/bin/startup.sh"]
#CMD ["usr/local/apache-tomcat-9.0.14/bin/catalina.sh","run"]
CMD /usr/local/apache-tomcat-9.0.14/bin/startup.sh && tail -f /usr/local/apache-tomcat-9.0.14/logs/catalina.out

~~~

2，构建

如果当前目录下有名为Dockerfile的文件，就可以省略`-f Dockerfile`

`docker build -t baibai/tomcat9:1.0 .`

3，创建容器

`docker run -d -p 9090:8080 --name tomcat9 baibai/tomcat9:1.0 `



### 4）Tomcat构建说明

添加一个数据卷，把Tomcat的webapp文件夹挂在到宿主机上，就可以实现不进入docker容器，直接部署项目到容器中。也可以在添加一个数据卷，logs文件夹也挂载到宿主机上，方便查看日志

## 5.总结

![1545891752344](images\1545891752344.png)

# 七、Docker安装常用软件

## 1.安装Mysql

### 1）拉取mysql镜像

`docker pull mysql:5.7`

### 2）创建mysql容器

~~~
 docker run -d -p 3306:3306 --name mysql 
 -v /bai/mysql/conf:/etc/mysql/conf.d 
 -v /bai/mysql/logs:/logs 
 -v /bai/mysql/data:/var/lib/mysql 
 -e MYSQL_ROOT_PASSWORD=123456 
 mysql:5.7
~~~

`/bai/mysql/`目录必须存在，否则报错，`-d`的位置不能错

 命令说明：

`-p 12345:3306`：将主机的12345端口映射到docker容器的3306端口。

`--name mysql`：运行服务名字

`-v /zzyyuse/mysql/conf:/etc/mysql/conf.d `：将主机/zzyyuse/mysql录下的conf/my.cnf 挂载到容器的 /etc/mysql/conf.d

`-v /zzyyuse/mysql/logs:/logs`：将主机/zzyyuse/mysql目录下的 logs 目录挂载到容器的 /logs。

`-v /zzyyuse/mysql/data:/var/lib/mysql `：将主机/zzyyuse/mysql目录下的data目录挂载到容器的 /var/lib/mysql 

`-e MYSQL_ROOT_PASSWORD=123456`：初始化 root 用户的密码。

`-d mysql:5.7 `: 后台程序运行mysql5.7

### 3）进入容器

docker exec -it MySQL运行成功后的容器ID     /bin/bash

### 补充

备份mysql所有数据库

`docker exec myql服务容器ID sh -c ' exec mysqldump --all-databases -uroot -p"123456" ' > /zzyyuse/all-databases.sql`

## 2.安装redis

### 1）拉取redis镜像

`docker pull redis:3.2`

### 2）创建容器

~~~
 docker run -p 6379:6379 
 -v /zzyyuse/myredis/data:/data 
 -v /zzyyuse/myredis/conf/redis.conf:/usr/local/etc/redis/redis.conf  
 -d --name redis redis:3.2 redis-server /usr/local/etc/redis/redis.conf 
 --appendonly yes
~~~

` -v /zzyyuse/myredis/data:/data`   创建数据卷，把容器`/data`挂载到/zzyyuse/myredis/data目录，在主机上就可以读取持久化信息（默认情况下redis容器的aof信息会被存到`/data`目录）

` -v /zzyyuse/myredis/conf/redis.conf:/usr/local/etc/redis/redis.conf `   创建数据卷，把容器的`/usr/local/etc/redis/redis.conf  `目录挂载到主机的`/zzyyuse/myredis/conf/redis.conf  `目录，==redis.config是一个文件夹==，默认情况下redis容器在启动redis时，自动读取放在`/usr/local/etc/redis/redis.conf  `文件夹内的配置文件（redis.conf）

### 3）连接redis

` docker exec -it 运行着Rediis服务的容器ID redis-cli`

默认使用`/usr/local/etc/redis/redis.conf/redis.conf`

# 八、本地镜像推送到阿里云

![1545898930419](images\1545898930419.png) 

## 1.进入阿里云官网

创建仓库

![1545900174139](images\1545900174139.png)

选择本地仓库

![1545900255229](images\1545900255229.png)

管理仓库

![1545900316223](images\1545900316223.png)



## 2. 登录阿里云Docker Registry

```
$ sudo docker login --username=17136054200 registry.cn-hangzhou.aliyuncs.com
```

用于登录的用户名为阿里云账号全名，密码为开通服务时设置的密码。

您可以在产品控制台首页修改登录密码。

## 3. 从Registry中拉取镜像

```
$ sudo docker pull registry.cn-hangzhou.aliyuncs.com/baibai/tomcat9:[镜像版本号]
```

## 4. 将镜像推送到Registry

```
$ sudo docker login --username=17136054200 registry.cn-hangzhou.aliyuncs.com
$ sudo docker tag [ImageId] registry.cn-hangzhou.aliyuncs.com/baibai/tomcat9:[镜像版本号]
$ sudo docker push registry.cn-hangzhou.aliyuncs.com/baibai/tomcat9:[镜像版本号]
```

请根据实际镜像信息替换示例中的[ImageId]和[镜像版本号]参数。



















