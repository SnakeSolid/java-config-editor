# Libraries directory

Must contain SIU libraries:

* siuloader.jar
* siuutils.jar

You should create a specific directory structure in project lib:

mkdir -p ${project.basedir}/lib/com/hp/siu/loader/${sui.version}
mkdir -p ${project.basedir}/lib/com/hp/siu/utils/${sui.version}

And place libraries named loader-${sui.version}.jar and
utils-${sui.version}.jar into appropriate directories.

To each jar folder place pom.xml file like this:

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.hp.siu</groupId>
    <artifactId>loader</artifactId>
    <version>${sui.version}</version>
</project>
```
