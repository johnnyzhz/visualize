# How to deploy this

This applicaiton can be used online at https://webnetvis.psychstat.org/

## Compile the java classes

## Prepare the server

To use the app, we need to use headless Gephi, which again requires the use of Java 8

### Install Java (Gephi requires Java 8)

The app requires the Java 8 to run. To install it, use the code below:

```
sudo apt update
sudo apt install openjdk-8-jdk
java -version
```

Verify the installation:

```java -version```

It should output something like:

```openjdk version "1.8.0_xxx"```

### Compile Java class webnetvis

- Clone the folder "webnetvis" in this repo.
- Now we convert the class to a single jar file.
- We will need gephi-toolkit-0.9.2-all.jar, gson.jar, and uk-ac-ox-oii-sigmaexporter.jar as dependence. Download them to your computer first.
- Then first convert all java files to class using the code below. We save everything to the folder `out`.

```
javac -cp "/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/gephi-toolkit-0.9.2-all.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/gson.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/uk-ac-ox-oii-sigmaexporter.jar" -d out webnetvis/*.java
```
- After it, make a jar file
```
jar cvf webnetvis.jar -C out .
```
- Finally, copy the file `webnetvis.jar` to the folder `/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/`
- Note that we hard programmed the folder and files names in the class `RankingGraph.java` to assume the web app will be placed in the folder of `/var/www/webnetvis`. You may need to change it before pack the Jar file.

## JavaBridge for connecting PHP with Java

- We use JavaBridge to connect PHP with Java. To use it, you need to download JavaBridge.jar to your computer.
- You can download it from our website at https://webnetvis.psychstat.org/JavaBridge.jar
- Note that JavaBridge only support PHP 7 now. If you have PHP 8, it will not work.

## Start the java virtual machine

- To use it, start JVM using
 
```
nohup java -Djava.class.path="/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/gephi-toolkit-0.9.2-all.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/webnetvisa.jar:/var/www/html/webnetvis/JavaBridge.jar" -jar JavaBridge.jar SERVLET_LOCAL:8080 &
```

- Check to make sure it is running

```
netstat -tulnp | grep 8080
// you may also simply use this
nohup java -jar JavaBridge.jar SERVLET_LOCAL:8080 &
```

- You can add a script to start the JVM at the start of your computer. We created the file `rc.local` and the add the contents below.

```
sudo nano /etc/rc.local
sudo chmod +x /etc/rc.local
```

The content of the file can be

```
#!/bin/bash
nohup java -Djava.class.path="/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/gephi-toolkit-0.9.2-all.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/test_sigma.jar:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/webnetvis.jar:/var/www/html/webnetvis/JavaBridge.jar"  -jar /var/www/webnetvis/JavaBridge.jar SERVLET_LOCAL:8080 #
```
