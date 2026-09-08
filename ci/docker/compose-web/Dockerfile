# Catalog 静态站：Gradle 编 wasm+JS 兼容包，nginx 提供 SPA。
# 必须打 linux/amd64（GitLab runner / 集群节点都是 x64）。

FROM eclipse-temurin:21-jdk AS build

ARG DEBIAN_FRONTEND=noninteractive
ARG BUILDKONFIG_FLAVOR=product

RUN apt-get update \
    && apt-get install -y --no-install-recommends libatomic1 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /src
COPY . .
RUN chmod +x gradlew \
    && echo "kotlin.native.ignoreDisabledTargets=true" >> gradle.properties \
    && ./gradlew :app:webApp:composeCompatibilityBrowserDistribution \
        --no-daemon \
        --no-scan \
        -Pbuildkonfig.flavor=${BUILDKONFIG_FLAVOR}

FROM nginx:1.27-alpine
COPY ci/docker/compose-web/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /src/app/webApp/build/dist/composeWebCompatibility/productionExecutable/ /usr/share/nginx/html/
EXPOSE 80
