# THIS DOCKERFILE IS NOT FOR DEPLOYMENT OR PROD USAGE BUT RATHER TO USE IN CASE WHERE GRADLE IS NOT AVAILABLE.
FROM node:lts-alpine as frontend
WORKDIR /workspace/app
COPY frontend ./

RUN npm ci && npm run test:unit:ci && npm run build

FROM amazoncorretto:17-alpine-jdk as backend
WORKDIR /workspace/app
COPY . .
RUN rm -rf backend/main/resources/static/** && \
    rm -f backend/main/resources/templates/error/404.html && \
    rm -f backend/main/resources/templates/error/error.html && \
    rm -f backend/main/resources/templates/index.html

COPY --from=frontend /workspace/app/dist/index.html backend/main/resources/templates/index.html
COPY --from=frontend /workspace/app/dist/index.html backend/main/resources/templates/error/404.html
COPY --from=frontend /workspace/app/dist/assets backend/main/resources/static/assets

RUN  file="index.$(ls frontend/dist/assets/index.*.css | sort -V | cut -d. -f2).css" && \
     mv backend/main/resources/templates/errorTemplate.html backend/main/resources/templates/error/error.html && \
     sed -i "s/__REPLACE__/$file/g" backend/main/resources/templates/error/error.html && \
     sed -i "s/\/\/DO_NOT_REMOVE/tasks.getByName<Jar>(\"jar\") { enabled = false }/g" build.gradle.kts && \
     chmod u+x gradlew &&  \
     apk add nodejs npm && \
     ./gradlew clean build &&  \
     cp build/libs/*.jar build/libs/app.jar

FROM amazoncorretto:17-alpine

ARG USER=appuser

ENV HOME /home/$USER

RUN apk add --no-cache tzdata && \
    ls /usr/share/zoneinfo && \
    cp /usr/share/zoneinfo/UTC /etc/localtime && \
    echo "UTC" >  /etc/timezone && \
    apk del tzdata && \
    adduser -D $USER && \
    mkdir -p /etc/sudoers.d && \
    echo "$USER ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers.d/$USER && \
    chmod 0440 /etc/sudoers.d/$USER

ENV TZ=UTC
ENV JAVA_TOOL_OPTIONS="-Xms1800M -Xmx2000M"

USER $USER
WORKDIR $HOME

COPY --from=backend /workspace/app/build/libs/app.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
