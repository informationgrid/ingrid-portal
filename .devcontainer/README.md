# Development

* Start tomcat: `cd /tomcat && bin/start.sh`
* visit http://localhost:8080/
* after a change in the portal code run `mvn package war:exploded` in directory `ingrid-portal-apps` and reload page

## Set up: IntelliJ IDEA
Limitations:
* Dev Containers on Windows are not supported.
* For remote Dev Containers the password authentication is not supported.

Instructions:
* Open the devcontainer.json file
* Click on the cube icon on the left and select Create Dev Container and Mount Sources (or Clone Sources)
* Wait for the dev container to be created and the project is opened.

For further documentation see [Dev Container](https://www.jetbrains.com/help/idea/connect-to-devcontainer.html#create_dev_container_inside_ide)
