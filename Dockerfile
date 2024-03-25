#FROM adoptopenjdk/openjdk11:alpine-slim
#需要先通过本地maven打包具体jar包，account版本与pom中version保持一致
#该文件为部署本地docker中
FROM openjdk:22
ADD target/stock-and-fund-*.jar /stock-and-fund.jar
RUN bash -c 'touch /stock-and-fund.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","-Duser.timezone=GMT+08","-Xmx256m","-Xms256m","/stock-and-fund.jar"]