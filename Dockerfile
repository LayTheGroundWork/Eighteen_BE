
## BUILD PHASE

# 1 ) build environment setting
FROM gradle:8.6-jdk17 AS build
WORKDIR /app
# 2) copy source code and gradle setting
COPY --chown=gradle:gradle . /app
# 3) application build and execute test
RUN ./gradlew build --no-daemon


## RUNNER PHASE
# 4) run time environment settign
FROM amazoncorretto:17.0.10
WORKDIR /app
# build jar copy
COPY --from=build /app/build/libs/*.jar eighteen.jar
# run app
CMD ["java" , "-jar" , "eighteen.jar"]