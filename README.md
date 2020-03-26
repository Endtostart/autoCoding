# **_Auto Coding_**
**_用于自动生成代码的 idea plugin_**
  
**自动生成 bean copy 代码**
-
> **诞生**
  
由于项目中分层结构，每层需要大量的实体数据转换。项目组规定不能用beancopy之类的工具类，只能手动get set.耗费大量的时间和精力.这个插件就诞生了.
> **安装插件**
  
- 直接编译  
build -> prepare plugin moudle 'xxx' for deployment  
然后设置增加plugin 选择从本地导入，导入打包生成的jar就好了  
- 或者直接下载release中jar
插件本地导入即可
> **如何使用**
  
假设输入如下代码：  
```  
java User user = new User();  
source
 ```  
 当前光标位置在***source***所在行，如何快捷键 **ctrl alt K** 即可生成如下代码  
 ```java
User user = new User();
user.setName(source.getName());
user.setAge(source.getAge());
```
> **个性化**
  
插件的快捷键和功能按钮在 resources/META-INF/plugin.xml中可以修改  
默认功能项展示为： <em>Tools -> beanCopy</em>  
默认快捷键：ctrl alt K  

