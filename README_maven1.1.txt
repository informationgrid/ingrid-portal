With maven 1.1 there might be problems generating the classpath for eclipse COMPLAINING ABOUT MISSING DEPENDENCY VERSIONS (in portal-base / portal-mdek).
This is due to downloading of java sources and java docs which is enabled by default.
A simple workaround is to disable this download functionality, so add the according properties to the maven call
(see http://maven.apache.org/maven-1.x/plugins/eclipse/properties.html)

maven eclipse -Dmaven.eclipse.src.download=false -Dmaven.eclipse.javadoc.download=false

maven eclipse:generate-classpath -Dmaven.eclipse.src.download=false -Dmaven.eclipse.javadoc.download=false



If this properties aren't added, the missing dependencies are the following ones (just added here for completeness, better try the upper approach):

        <dependency>
            <groupId>jta</groupId>
            <artifactId>jta</artifactId>
            <version>1.0.1</version>
            <type>jar</type>
        </dependency>
        <dependency>
            <groupId>jdbc-se</groupId>
            <artifactId>jdbc-se</artifactId>
            <version>2.0</version>
            <type>jar</type>
        </dependency>

NOTICE: the jdbc-se jar is in the repo as jdbc-se2.0.jar (???) and has to be copied to jdbc-se-2.0.jar !
