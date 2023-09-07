FROM harbor-repo.vmware.com/dockerhub-proxy-cache/library/ubuntu:bionic

ARG CONCOURSE_JAVA_SCRIPTS_VERSION=0.0.4
ARG CONCOURSE_RELEASE_SCRIPTS_VERSION=0.3.4

RUN apt-get update && apt-get install --no-install-recommends -y ca-certificates net-tools git curl jq gnupg

RUN rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /opt/openjdk
ENV PATH $JAVA_HOME/bin:$PATH
RUN mkdir -p /opt/openjdk && \
    cd /opt/openjdk && \
    curl -L https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.5%2B8/OpenJDK17U-jdk_x64_linux_hotspot_17.0.5_8.tar.gz | tar xz --strip-components=1

ADD "https://raw.githubusercontent.com/spring-io/concourse-java-scripts/v$CONCOURSE_JAVA_SCRIPTS_VERSION/concourse-java.sh" /opt/
ADD "https://repo.spring.io/ui/native/snapshot/io/spring/concourse/releasescripts/concourse-release-scripts/$CONCOURSE_RELEASE_SCRIPTS_VERSION/concourse-release-scripts-$CONCOURSE_RELEASE_SCRIPTS_VERSION.jar" /opt/
