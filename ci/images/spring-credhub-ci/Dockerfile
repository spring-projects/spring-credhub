FROM spring-credhub-docker-virtual.usw1.packages.broadcom.com/ubuntu:jammy

# Environment variables
ENV TERM dumb
ENV LC_ALL C.UTF-8

ARG CONCOURSE_JAVA_SCRIPTS_VERSION=0.0.4
ARG CONCOURSE_RELEASE_SCRIPTS_VERSION=0.3.4
ARG YTT_VERSION="0.45.4"

# Install packages required for bootstrapping
RUN apt-get -qy update \
    && apt-get -qy --no-install-recommends install \
	  apt-transport-https \
	  ca-certificates \
	  curl \
	  gnupg-agent \
	  software-properties-common \
    && apt-get clean

# Accept apt repository keys
RUN curl -q 'https://download.docker.com/linux/ubuntu/gpg' | apt-key add -

# Add custom apt repositories
RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

# Install packages
RUN apt-get -qy update \
    && apt-get -qy --no-install-recommends install \
      bsdextrautils \
      containerd.io \
      cgroupfs-mount \
      docker-ce \
      git \
      jq \
      make \
      openjdk-17-jdk \
      openssh-client \
      tcpdump \
    && apt-get clean

RUN curl -Lo ytt "https://github.com/vmware-tanzu/carvel-ytt/releases/download/v$YTT_VERSION/ytt-linux-amd64" \
  && chmod u+x ytt && mv ytt /usr/local/bin/

RUN curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" \
    -o /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose

# https://github.com/docker/cli/issues/4807#issuecomment-1903950217
RUN \
    echo "ulimits: $(ulimit -Sn):$(ulimit -Hn)"; \
    sed -i 's/ulimit -Hn/# ulimit -Hn/g' /etc/init.d/docker; \
    service docker start; \
    rm -rf /var/cache/apt;

WORKDIR /unpack

ADD "https://raw.githubusercontent.com/spring-io/concourse-java-scripts/v$CONCOURSE_JAVA_SCRIPTS_VERSION/concourse-java.sh" /opt/