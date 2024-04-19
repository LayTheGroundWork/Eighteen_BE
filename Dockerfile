##
FROM gradle:8.6-jdk17 AS build

WORKDIR /app
# 2) copy source code and gradle setting
COPY --chown=gradle:gradle . /app
# 3) application build and execute test
ADD  wait-for-it.sh /app
RUN chmod +x gradlew && ./gradlew clean --no-daemon && ./gradlew copyYaml --no-daemon && ./gradlew copyEnv --no-daemon && ./gradlew build --no-daemon -x test

FROM amazoncorretto:17.0.10
WORKDIR /app
# build jar copy
COPY --from=build /app/build/libs/*.jar eighteen.jar
COPY --from=build /app/wait-for-it.sh wait-for-it.sh
RUN chmod +x wait-for-it.sh
# run app
CMD ["java" , "-jar" , "eighteen.jar"]