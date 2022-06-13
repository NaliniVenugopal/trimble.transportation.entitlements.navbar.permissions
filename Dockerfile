FROM maven:3.6.3-jdk-11-slim as build
WORKDIR /build
RUN apt-get update -y && apt-get install -y \	
    zip && apt-get clean

ARG SVCACCT_TCX_CI_USERNAME
ARG SVCACCT_TCX_CI_PASSWORD
COPY settings.xml-Docker-build /build/settings.xml
RUN test -n "${SVCACCT_TCX_CI_USERNAME}" && sed -i "s|{{SVCACCT_TCX_CI_USERNAME}}|${SVCACCT_TCX_CI_USERNAME}|g" /build/settings.xml && exit 0 || echo 'BUILD ERROR: SVCACCT_TCX_CI_USERNAME must be passed as a docker build --build-arg to populate value in settings.xml' && exit 1
RUN test -n "${SVCACCT_TCX_CI_PASSWORD}" && sed -i "s|{{SVCACCT_TCX_CI_PASSWORD}}|${SVCACCT_TCX_CI_PASSWORD}|g" /build/settings.xml && exit 0 || echo 'BUILD ERROR: SVCACCT_TCX_CI_PASSWORD must be passed as a docker build --build-arg to populate value in settings.xml' && exit 1

COPY pom.xml /build
RUN mvn dependency:resolve dependency:resolve-plugins -s /build/settings.xml

COPY src /build/src
RUN mvn clean package -s /build/settings.xml

WORKDIR /build
RUN zip -r app.zip target

FROM adoptopenjdk/openjdk11:alpine-slim as final
WORKDIR /app

ARG BUILD_VERSION
ENV BUILD_VERSION=$BUILD_VERSION

COPY --from=build /build/target/navbar-permissions-service-*.jar navbar-permissions-service.jar
COPY --from=build /build/app.zip /usr/app/app.zip

RUN apk update && apk add \
    vim \
    dos2unix \
    curl
COPY invokeServices.sh /usr/start/

RUN dos2unix /usr/start/invokeServices.sh
RUN chmod +x /usr/start/invokeServices.sh

EXPOSE 80
ENTRYPOINT ["/usr/start/invokeServices.sh"]
