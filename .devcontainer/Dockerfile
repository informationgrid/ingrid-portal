FROM mcr.microsoft.com/devcontainers/java:1-17-bullseye

ARG INSTALL_MAVEN="true"
ARG MAVEN_VERSION="3.5.4"

ARG INSTALL_GRADLE="false"
ARG GRADLE_VERSION=""

RUN if [ "${INSTALL_MAVEN}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven \"${MAVEN_VERSION}\""; fi \
    && if [ "${INSTALL_GRADLE}" = "true" ]; then su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install gradle \"${GRADLE_VERSION}\""; fi

RUN wget https://distributions.informationgrid.eu/ingrid-portal/6.1.1.1/ingrid-portal-6.1.1.1-installer.jar
RUN unzip ingrid-portal-6.1.1.1-installer.jar -d installer && mv /installer/distribution/apache-tomcat /tomcat
RUN chmod +x /tomcat/bin/*.sh && mkdir /tomcat/logs && chown -R vscode /tomcat && rm /tomcat/webapps/*.war

# [Optional] Uncomment this section to install additional OS packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends default-mysql-client

# TODO:
# add manager to tomcat users
# <role rolename="manager-gui"/>
# <user username="man" password="8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918" roles="admin-gui,manager-gui"/>

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1
