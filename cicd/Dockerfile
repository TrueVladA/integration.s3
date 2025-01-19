# First stage: complete build environment
FROM cprd-doc-reg01.sberins.ru:10019/platform/jdk-18-maven-3.8.8 AS builder

ENV MAVEN_OPTS "-Dhttps.protocols=TLSv1.2"
WORKDIR /build

COPY --chown=service . .

#RUN mvn -B -f ./pom.xml dependency:go-offline

RUN mvn install && mvn -B package -DskipTests

FROM cprd-doc-reg01.sberins.ru:10010/openjdk:18-slim-buster

ARG Env

USER root
COPY certs/SberIns_Root_CA.cer /usr/local/share/ca-certificates/
COPY certs/SberIns_Issuing_CA.cer /usr/local/share/ca-certificates/

RUN apt update && \
	apt-get install -y libfreetype6 fontconfig --no-install-recommends && \
	echo | openssl s_client -showcerts -connect $Env:443 2>/dev/null | \
  		openssl x509 -outform PEM > /usr/local/share/ca-certificates/sberins.cer && \
    update-ca-certificates

RUN keytool -keystore -cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias SberIns_Root_CA -file /usr/local/share/ca-certificates/SberIns_Root_CA.cer
RUN keytool -keystore -cacerts -storepass changeit -noprompt -trustcacerts -importcert -alias SberIns_Issuing_CA -file /usr/local/share/ca-certificates/SberIns_Issuing_CA.cer
RUN keytool -import -file /usr/local/share/ca-certificates/sberins.cer -trustcacerts -storepass changeit -noprompt -keystore ${JAVA_HOME}/lib/security/cacerts

WORKDIR /app
COPY --from=builder /build/modules/application/target/*SNAPSHOT.jar ./app-SNAPSHOT.jar
EXPOSE 8080
USER 1000
CMD [ "java", "--Xmx4096m -Xms1024m", "-jar", "/app/app-SNAPSHOT.jar" ]

