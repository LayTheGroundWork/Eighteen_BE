##
FROM gradle:8.6-jdk17 AS build

WORKDIR /app
# 2) copy source code and gradle setting
COPY --chown=gradle:gradle . /app
# 3) application build and execute test
RUN chmod +x gradlew
RUN ./gradlew clean --no-daemon
RUN ./gradlew copyPrivate --no-daemon
RUN ./gradlew build --no-daemon -x test

FROM amazoncorretto:17.0.10
WORKDIR /app
# build jar copy
COPY --from=build /app/build/libs/*.jar eighteen.jar
# run app
CMD ["java" , "-jar" , "eighteen.jar"]