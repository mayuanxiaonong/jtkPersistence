jtkPersistence
==============

Jtk Persistence, implements auto map/load/save between entity and properties file.

基于ORM技术和持久化技术，实现了实体类和Java配置文件之间的关系映射、自动加载配置文件内容到实体类、自动保存实体类对象到配置文件。

实现过程中参考了JPA的持久化思想，实体类上使用 @JtkEntity 注解指定配置文件的文件路径，变量上添加注解 @JtkKey 指定映射到配置文件的字段名（或不使用注解，默认以变量名映射），或者在变量上使用 @JtkNone 注解，表示变量不和配置文件进行映射。

使用反射机制获取实体类的注解，读取配置文件，并将键值对映射到实体类，实现配置文件的自动加载和保存。

依赖包：  jtk_util_0.1.jar              https://github.com/mayuanxiaonong/javaToolKit/
          commons-beanutils-1.9.1.jar   http://commons.apache.org/proper/commons-beanutils/
          commons-logging-1.1.2.jar     http://commons.apache.org/proper/commons-logging/
