##
FROM gradle:8.6-jdk17 AS build

FROM amazoncorretto:17.0.10
WORKDIR /app
# build jar copy
COPY --from=build /app/build/libs/*.jar eighteen.jar
# run app
CMD ["java" , "-jar" , "eighteen.jar"]