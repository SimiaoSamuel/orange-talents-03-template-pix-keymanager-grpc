FROM openjdk:11
ADD /build/libs/keymanagergrpc-0.1-all.jar keymanagergrpc.jar
EXPOSE 50051
ENTRYPOINT ["java","-jar","keymanagergrpc.jar"]