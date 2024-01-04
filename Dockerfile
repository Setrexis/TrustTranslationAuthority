FROM tomcat:jre8-alpine

RUN rm -rf /usr/local/tomcat/webapps/*
ADD target/ttaFM.war /usr/local/tomcat/webapps/ttaFM.war

ADD rest_log4j.properties /usr/local/tomcat/conf/rest_log4j.properties
ADD ttaFM.properties /usr/local/tomcat/conf/ttaFM.properties

RUN mkdir -p /usr/local/tomcat/lightest
RUN mkdir -p /usr/local/tomcat/lightest/fileStorage
RUN mkdir -p /usr/local/tomcat/lightest/tta/files/signed

RUN ln -s /usr/local/tomcat/lightest/tta/files/ /usr/local/tomcat/lightest/tta/files/signed/


ADD tta.pfx /usr/local/tomcat/lightest/tta.pfx

#ADD server.xml /usr/local/tomcat/conf/server.xml

RUN echo "1LqNpNE2MGRlPIxtT5CKNLzYN-QuSeju" >> /usr/local/tomcat/lightest/dnsToken



EXPOSE 8080
EXPOSE 8443
CMD ["catalina.sh", "run"]
