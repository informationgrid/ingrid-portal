pipeline {
    agent any
    triggers{ cron( getCronParams() ) }

    stages {
        stage('Build image') {
            when { not { buildingTag() } }
            steps {
                // get submodules with commit id
                withCredentials([gitUsernamePassword(credentialsId: 'ae3a7670-c4c8-413c-9df2-45373f1723a2', gitToolName: 'git')]) {
                    sh 'git submodule update --init --recursive'
                }
                script {
                    def versions = readProperties file: 'versions.props'
                    // remove build dir containing previous RPM
                    sh 'if [ -d build ]; then rm -rf build; fi'

                    def version = computeVersion()

                    docker.withRegistry('https://registry.opencode.de', 'registry-opencode') {
                        def customImage = docker.build("registry.opencode.de/informationgrid/ingrid-portal:${version}", "--pull --build-arg GRAV_VERSION=${versions['GRAV_VERSION']} --build-arg MVIS_VERSION=${versions['MVIS_VERSION']} .")

                        /* Push the container to the custom Registry */
                        customImage.push()
                    }
                }
            }
        }

        stage ('Base-Image Update') {
            when {
                allOf {
                    buildingTag()
                    expression { return currentBuild.number > 1 && shouldBuildDockerImage() }
                }
            }
            steps {
                // get submodules with commit id
                withCredentials([gitUsernamePassword(credentialsId: 'ae3a7670-c4c8-413c-9df2-45373f1723a2', gitToolName: 'git')]) {
                    sh 'git submodule update --init --recursive'
                }
                script {
                    def versions = readProperties file: 'versions.props'
                    docker.withRegistry('https://registry.opencode.de', 'registry-opencode') {
                        def customImage = docker.build("registry.opencode.de/informationgrid/ingrid-portal:${env.TAG_NAME}", "--pull --build-arg GRAV_VERSION=${versions['GRAV_VERSION']} --build-arg MVIS_VERSION=${versions['MVIS_VERSION']} .")

                        /* Push the container to the custom Registry */
                        customImage.push()
                    }
                }
            }
        }

        stage('Build SBOM') {
            when {
                anyOf {
                    branch 'main'
                    buildingTag()
                }
            }
            steps {
                echo 'Generating Software Bill of Materials (SBOM)'

                script {
                    def version = computeVersion()
                    def baseImageToScan = "php:8.3-fpm-trixie"
                    def imageToScan = "registry.opencode.de/informationgrid/ingrid-portal:${version}"
                    def sbomFilename = "ingrid-portal-${determineVersion()}.sbom.json"

                    docker.withRegistry('https://registry.opencode.de', 'registry-opencode') {
                        sh """
                            # Clean up any previous extraction
                            rm -rf ${WORKSPACE}/build/app-extracted
                            mkdir -p ${WORKSPACE}/build/app-extracted

                            # Extract application directory from container image
                            CONTAINER_ID=\$(docker create ${imageToScan})
                            docker cp \${CONTAINER_ID}:/usr/share/grav-admin ${WORKSPACE}/build/app-extracted/
                            docker rm \${CONTAINER_ID}

                            # Run Syft specifically against the extracted application directory
                            docker run --rm --pull=always --volumes-from jenkins \\
                                anchore/syft:latest dir:${WORKSPACE}/build/app-extracted/grav-admin \\
                                --output cyclonedx-json=${WORKSPACE}/build/${sbomFilename}

                            # Cleanup extracted files to save disk space
                            rm -rf ${WORKSPACE}/build/app-extracted

                            # Create SBOM for base image
                            docker run --rm --pull=always --volumes-from jenkins anchore/syft:latest ${imageToScan} --output cyclonedx-json=${WORKSPACE}/build/sbom-docker-image.json
                        """
                    }
                }
            }
        }

        stage ('Merge SBOMs') {
            when {
                anyOf {
                    branch 'main'
                    buildingTag()
                }
            }
            agent {
                docker {
                    image 'cyclonedx/cyclonedx-cli:latest'
                    // CRITICAL: Overrides the entrypoint so Jenkins can run it as a regular shell agent
                    args '-u root --entrypoint=""'
                    reuseNode true
                }
            }
            steps {
                script {
                    sh """
                        mkdir -p build/reports
                        cyclonedx merge --input-files build/ingrid-portal-${determineVersion()}.sbom.json build/sbom-docker-image.json --output-file build/sbom-docker-final.json --output-format json --output-version v1_6 --hierarchical --group de.ingrid --name ingrid-editor --version ${determineVersion()}
                    """
                }
            }
        }

        stage ('Upload SBOM') {
            when {
                anyOf {
                    branch 'main'
                    buildingTag()
                }
            }
            steps {
                script {
                    def parentId = '6acb0635-1981-4688-aa32-fa36cbf6d116'
                    def componentName = 'ingrid-portal'

                    withCredentials([string(credentialsId: 'api-token-dependency-track', variable: 'API_KEY')]) {
                        def baseVersion = determineVersion()
                        def sbomConfigs = [
                            [artifact: "build/ingrid-portal-${baseVersion}.sbom.json", suffix: '', tag: 'ingrid_deps_prod'],
                            [artifact: 'build/sbom-docker-final.json', suffix: '-docker-image', tag: 'ingrid_deps_docker']
                        ]

                        sbomConfigs.each { cfg ->
                            def props = [group: 'InGrid', parentId: parentId, tags: ['ingrid', cfg.tag]]
                            dependencyTrackPublisher(artifact: cfg.artifact, projectName: componentName, projectVersion: baseVersion + cfg.suffix, synchronous: true, dependencyTrackApiKey: API_KEY, projectProperties: props)
                        }
                    }

                    def repoType = env.TAG_NAME ? "rpm-ingrid-releases" : "rpm-ingrid-snapshots"
                    withCredentials([usernamePassword(credentialsId: '9623a365-d592-47eb-9029-a2de40453f68', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                        sh '''
                            curl -f --user $USERNAME:$PASSWORD --upload-file build/*.sbom.json https://nexus.informationgrid.eu/repository/''' + repoType + '''/
                        '''
                    }
                }
            }
        }

        stage('Build RPM') {
            when { expression { return shouldBuildRPM() } }
            agent {
                docker {
                    image 'docker-registry.wemove.com/ingrid-rpmbuilder-php8'
                    reuseNode true
                }
            }
            steps {
                echo 'Starting to build RPM package'

                script {
                    def versions = readProperties file: 'versions.props'
                    sh "sed -i 's/^Version:.*/Version: ${determineVersion()}/' rpm/ingrid-portal.spec"
                    sh "sed -i 's/^Release:.*/Release: ${determineRpmReleasePart()}/' rpm/ingrid-portal.spec"
                    sh "sed -i 's/^%define version_grav .*/%define version_grav ${versions['GRAV_VERSION']}/' rpm/ingrid-portal.spec"
                    sh "sed -i 's/^%define version_mvis .*/%define version_mvis ${versions['MVIS_VERSION']}/' rpm/ingrid-portal.spec"

                    sh """
                        cp ${WORKSPACE}/rpm/ingrid-portal.spec /root/rpmbuild/SPECS/ingrid-portal.spec &&
                        rpmbuild -bb /root/rpmbuild/SPECS/ingrid-portal.spec
                    """

                    withCredentials([
                            file(credentialsId: 'ingrid-rpm-public', variable: 'RPM_PUBLIC_KEY'),
                            file(credentialsId: 'ingrid-rpm-private', variable: 'RPM_PRIVATE_KEY'),
                            string(credentialsId: 'ingrid-rpm-passphrase', variable: 'RPM_SIGN_PASSPHRASE')
                        ]) {
                        sh 'gpg --batch --import $RPM_PUBLIC_KEY'
                        sh 'gpg --batch --import $RPM_PRIVATE_KEY'
                        sh "mkdir -p ./build"
                        sh "mkdir -p ./build/ingrid"
                        sh "cp -r /root/rpmbuild/RPMS/noarch/* ${WORKSPACE}/build/ingrid/"
                        sh "expect /rpm-sign.exp ${WORKSPACE}/build/ingrid/*.rpm"

                        archiveArtifacts artifacts: 'build/ingrid/ingrid-portal-*.rpm', fingerprint: true
                    }
                }
            }
        }

        stage('Deploy RPM') {
            when { expression { return shouldBuildRPM() } }
            steps {
                script {
                    def repoType = env.TAG_NAME ? "rpm-ingrid-releases" : "rpm-ingrid-snapshots"
                    withCredentials([usernamePassword(credentialsId: '9623a365-d592-47eb-9029-a2de40453f68', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                        sh '''
                            curl -f --user $USERNAME:$PASSWORD --upload-file build/ingrid/*.rpm https://nexus.informationgrid.eu/repository/''' + repoType + '''/
                        '''
                    }
                }
            }
        }
    }
}

def getCronParams() {
    String tagTimestamp = env.TAG_TIMESTAMP
    long diffInDays = 0
    if (tagTimestamp != null) {
        long diff = "${currentBuild.startTimeInMillis}".toLong() - "${tagTimestamp}".toLong()
        diffInDays = diff / (1000 * 60 * 60 * 24)
        echo "Days since release: ${diffInDays}"
    }

    def versionMatcher = /\d\.\d\.\d(.\d)?/
    if( env.TAG_NAME ==~ versionMatcher && diffInDays < 180) {
        // every Sunday between midnight and 6am
        return 'H H(0-6) * * 0'
    }
    else {
        return ''
    }
}

def determineVersion() {
    if (env.TAG_NAME) {
        if (env.TAG_NAME.startsWith("RPM-")) { // e.g. RPM-8.0.0-0.1SNAPSHOT
            def lastDashIndex = env.TAG_NAME.lastIndexOf("-")
            return env.TAG_NAME.substring(4, lastDashIndex)
        }
        return env.TAG_NAME
    } else {
        return env.BRANCH_NAME.replaceAll('/', '_')
    }
}

def determineRpmReleasePart() {
    if (env.TAG_NAME) {
        if (env.TAG_NAME.startsWith("RPM-")) {
            return env.TAG_NAME.substring(env.TAG_NAME.lastIndexOf("-") + 1)
        }
        return currentBuild.number
    } else {
        return 'SNAPSHOT'
    }
}

def computeVersion() {
    if (env.TAG_NAME) {
        if (env.TAG_NAME.startsWith("RPM-")) {
            return 'latest'
        }
        return env.TAG_NAME
    } else if (env.BRANCH_NAME == 'main') {
        return 'latest'
    } else {
        return env.BRANCH_NAME.replaceAll('/', '-')
    }
}

def shouldBuildDevOrRelease() {
    // If no tag is being built OR it is the first build of a tag
    boolean isTag = env.TAG_NAME != null && env.TAG_NAME.trim() != ''
    return !isTag || (isTag && currentBuild.number >= 1)
}

def shouldBuildDockerImage() {
    if (env.TAG_NAME && env.TAG_NAME.startsWith("RPM-")) {
        return false
    } else return true
}

def shouldBuildRPM() {
    if (env.BRANCH_NAME && env.BRANCH_NAME.startsWith("feature/")) {
        return false
    }
    return shouldBuildDevOrRelease()
}
