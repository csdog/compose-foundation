# Compose Foundation Desktop / Roborazzi 测试镜像。
# 对齐 [JetBrains compose-multiplatform ci/docker/linux-tests：Ubuntu 24.04](https://github.com/JetBrains/compose-multiplatform/tree/master/ci/docker/linux-tests)
# + OpenJDK 21 + xvfb + libgl/libEGL。不装 Android SDK / 浏览器（本仓库 GitLab 不跑那些）。
# Skiko 的 libskiko-linux-*.so 会 dlopen libEGL.so.1；只装 libgl-dev 不够。
#
# Harbor：harbor.devops.toutiaoyan.com/web-foundation/compose-linux-desktop:24.04
# 必须打 linux/amd64（GitLab runner），不要用本机默认的 arm64。

FROM ubuntu:24.04

ARG DEBIAN_FRONTEND=noninteractive
ARG JAVA_VERSION=21

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        openjdk-${JAVA_VERSION}-jdk \
        xvfb \
        libgl-dev \
        libegl1 \
        libgles2 \
        libx11-6 \
        libxext6 \
        libxrender1 \
        libxtst6 \
        libxi6 \
        libfontconfig1 \
        fontconfig \
        fonts-dejavu-core \
        fonts-liberation \
        locales \
        ca-certificates \
        unzip \
    && locale-gen en_US.UTF-8 \
    && JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")" \
    && ln -sfn "$JAVA_HOME" /usr/lib/jvm/default-java \
    && rm -rf /var/lib/apt/lists/*

ENV LANG=en_US.UTF-8 \
    LANGUAGE=en_US:en \
    LC_ALL=en_US.UTF-8 \
    JAVA_HOME=/usr/lib/jvm/default-java \
    PATH="/usr/lib/jvm/default-java/bin:${PATH}" \
    JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 \
    DISPLAY=:1.0
