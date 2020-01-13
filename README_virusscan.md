# Virus scanner configuration in InGrid Editor

A virus scanner installed on the server's operating system can be used to scan file uploads in the InGrid Editor. The scan is executed during file validation and before the file is finally stored in the file upload storage. The upload is rejected in case of an infection.

NOTE: Since all uploaded files are temporarily stored inside a [temporary directory](https://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempFile-java.lang.String-java.lang.String-java.nio.file.attribute.FileAttribute...-) and then scanned on demand, the file scanner should not be installed with the `on-access` scan option turned on (the temporary directory could be excluded from on-access protection alternatively).

To add the virus scanner into the list of validators, the configuration file `ingrid-portal/ingrid-portal-mdek-application/src/main/resources/mdek.properties` has to contain the `virusscan` entry inside the `upload.validators` property:

```
upload.validators=filename,virusscan
```

The `virusscan` instance is configured together with all other validators in the `upload.validators.config` property. Each validator configuration requires the following properties:

- `impl` The implementation class (`de.ingrid.mdek.upload.storage.validate.impl.VirusScanValidator` for the virus scanner)
- `properties` Configuration properties passed to the initializer of the validator

The `properties` value must contain the following properties defining the virus scanner to be used:

- `command` The command to invoke the virus scanner. This string must contain the literal `%FILE%` that will be replaced by the real file path to scan. The scanner output must inform about the file's status and archive support should be activated.
- `virusPattern` A regular expression pattern that matches infections reported in the scan command result. The pattern must contain a capturing group for the virus name (#1) and one for the infected resource (#2)
- `cleanPattern` A regular expression pattern applied to the command result that matches, if no virus infection is found

The following sections show typically configurations for some free virus scanners:

## SAVScan (Sophos)

    upload.validators.config={\
        ...
        "virusscan":{\
            "impl":"de.ingrid.mdek.upload.storage.validate.impl.VirusScanValidator",\
            "properties":{\
                "command":"\\\\path\\\\to\\\\sophos\\\\savscan -f -archive %FILE%",\
                "virusPattern":"(?m)^>>> Virus '([^']+)' found in file (.+)$",\
                "cleanPattern":"(?m)^No viruses were discovered.$"\
            }\
        }\
        ...
    }

## ClamAV

```
upload.validators.config={\
    ...
    "virusscan":{\
        "impl":"de.ingrid.mdek.upload.storage.validate.impl.VirusScanValidator",\
        "properties":{\
            "command":"\\\\path\\\\to\\\\clamav\\\\clamscan %FILE%",\
            "virusPattern":"(?m)^(?=.+: (.+) FOUND$)(.+): .+ FOUND$",\
            "cleanPattern":"(?m)^Infected files: 0$"\
        }\
    }\
    ...
}
```

# Anti-Virus Test File

https://en.wikipedia.org/wiki/EICAR_test_file

# Setting up Sophos SAVScan (Linux) in a Docker container for testing on Windows

1. Download `sav-linux-free-9.tgz`

2. Create the following directory structure

   ```
   ├── res/
   │   ├── sav-linux-free-9.tgz
   ├── uploads/
   │   ├── // directory for files to scan (will be shared with container)
   ├── docker-compose.yml
   ├── Dockerfile
   ```

3. Copy the following content into the `Docker` file

   ```
   FROM ubuntu:latest
   ARG RES_DIR
   
   # Create app directory
   WORKDIR /app
   
   # Extract installer
   COPY ./res/sav-linux-free-9.tgz .
   RUN tar -xzvf ./sav-linux-free-9.tgz
   ```

   Copy the following content into the `docker-compose.yml` file

   ```
   version: '3.3'
   
   services:
     sophos-av:
       build:
         context: ./
         dockerfile: ./Dockerfile
       command: tail -f /dev/null
       volumes:
         - ./uploads:/uploads
   ```

4. Set up the container

   ```
   docker-compose up -d
   ```

5. SSH into the container

   ```
   docker exec -it docker_sophos-av_1 /bin/bash
   ```

6. Install sophos av inside the container (interactive installation required)

   ```
   ./sophos-av/install.sh
   ```

7. To scan a `<File>` call

   ```
   docker exec -it docker_sophos-av_1 savscan -f -archive /uploads/<File>
   ```

# Example scan reports 

## SAVScan (Sophos)

### Virus detected

```
> docker exec -it docker_sophos-av_1 savscan -f -archive /uploads/virus.bat
SAVScan virus detection utility
Version 5.53.0 [Linux/AMD64]
Virus data version 5.55, September 2018
Includes detection for 25676226 viruses, Trojans and worms
Copyright (c) 1989-2018 Sophos Limited. All rights reserved.

System time 12:32:06, System date 12 September 2019
Command line qualifiers are: -f -archive

Useful life of Scan has been exceeded

Full Scanning

>>> Virus 'EICAR-AV-Test' found in file /uploads/virus.bat

1 file scanned in 5 seconds.
1 virus was discovered.
1 file out of 1 was infected.
If you need further advice regarding any detections please visit our
Threat Center at: http://www.sophos.com/en-us/threat-center.aspx
End of Scan.
```

### No virus detected

```
> docker exec -it docker_sophos-av_1 savscan -f -archive /uploads/ok.txt
SAVScan virus detection utility
Version 5.53.0 [Linux/AMD64]
Virus data version 5.55, September 2018
Includes detection for 25676226 viruses, Trojans and worms
Copyright (c) 1989-2018 Sophos Limited. All rights reserved.

System time 12:31:01, System date 12 September 2019
Command line qualifiers are: -f -archive

Useful life of Scan has been exceeded

Full Scanning


1 file scanned in 4 seconds.
No viruses were discovered.
End of Scan.
```

### File not existing

```
> docker exec -it docker_sophos-av_1 savscan -f -archive /uploads/not-existing.txt
SAVScan virus detection utility
Version 5.53.0 [Linux/AMD64]
Virus data version 5.55, September 2018
Includes detection for 25676226 viruses, Trojans and worms
Copyright (c) 1989-2018 Sophos Limited. All rights reserved.

System time 12:32:46, System date 13 September 2019
Command line qualifiers are: -f -archive

Useful life of Scan has been exceeded

Full Scanning

Could not open /uploads/not-existing.txt

0 files scanned in 4 seconds.
1 error was encountered.
No viruses were discovered.
End of Scan.
```

## ClamAV

### Virus detected

```
> /viren-scan/clamav/clamav-0.101.4-win-x64-portable/clamscan /uploads/virus.bat
/uploads/virus.bat: Eicar-Test-Signature FOUND

----------- SCAN SUMMARY -----------
Known viruses: 6307667
Engine version: 0.101.4
Scanned directories: 0
Scanned files: 1
Infected files: 1
Data scanned: 0.00 MB
Data read: 0.00 MB (ratio 0.00:1)
Time: 33.660 sec (0 m 33 s)
```

### No virus detected

```
> /viren-scan/clamav/clamav-0.101.4-win-x64-portable/clamscan /uploads/ok.txt
/uploads/ok.txt: OK

----------- SCAN SUMMARY -----------
Known viruses: 6307667
Engine version: 0.101.4
Scanned directories: 0
Scanned files: 1
Infected files: 0
Data scanned: 0.00 MB
Data read: 0.00 MB (ratio 0.00:1)
Time: 33.550 sec (0 m 33 s)
```

### File not existing

```
> /viren-scan/clamav/clamav-0.101.4-win-x64-portable/clamscan /uploads/not-existing.txt
/uploads/not-existing.txt: No such file or directory
WARNING: /uploads/not-existing.txt: Can't access file

----------- SCAN SUMMARY -----------
Known viruses: 6307667
Engine version: 0.101.4
Scanned directories: 0
Scanned files: 0
Infected files: 0
Data scanned: 0.00 MB
Data read: 0.00 MB (ratio 0.00:1)
Time: 37.007 sec (0 m 37 s)
```