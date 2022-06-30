bmp = "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules"

modules={
"org.apache.commons.codec": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\apache\\commons\\codec\\main\\commons-codec-1.13.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\apache\\commons\\codec\\main\\module.xml"],
"org.apache.commons.collection": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\apache\\commons\\collections4\\main\\commons-collections4-4.2.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\apache\\commons\\collections4\\main\\module.xml"],
"org.apache.commons.lang3": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\apache\\commons\\lang3\\main\\commons-lang3-3.8.1.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\apache\\commons\\lang3\\main\\module.xml"],
"org.drools.api": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\drools\\api\\main\\kie-api-7.11.0.Final.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\drools\\api\\main\\module.xml"],
"org.drools.compiler": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\drools\\compiler\\main\\drools-compiler-7.11.0.Final.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\drools\\compiler\\main\\module.xml"],
"org.postgresql.jdbc": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\postgresql\\jdbc\\main\\postgresql-9.3-1102-jdbc3.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\org\\postgresql\\jdbc\\main\\module.xml"],
"com.force.api": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\force\\api\\main\\force-partner-api-47.0.0.jar;C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\force\\api\\main\\force-wsc-47.0.0.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\force\\api\\main\\module.xml"],
"com.dell.software.casino.dib.themes": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\themes\\main\\themes-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\themes\\main\\module.xml"],
"com.dell.software.casino.dib.common": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\common\\main\\common-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\common\\main\\module.xml"],
"com.dell.software.casino.dib.cbas": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\cbas\\main\\cbas-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\cbas\\main\\module.xml"],
"com.dell.software.casino.dib.microsoftservice": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\microsoftservice\\main\\microsoftservice-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\microsoftservice\\main\\module.xml"],
"com.dell.software.casino.dib.profileservice": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\profileservice\\main\\profileservice-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\profileservice\\main\\module.xml"],
"com.dell.software.casino.dib.salesforce": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\salesforce\\main\\salesforce-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\salesforce\\main\\module.xml"],
"com.dell.software.casino.dib.spi-extensions": ["C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\spi-extensions\\main\\spi-extensions-6.0.0-SNAPSHOT.jar", "C:\\dev\\dibv2\\server-dist\\target\\jboss-modules\\com\\dell\\software\\casino\\dib\\spi-extensions\\main\\module.xml"]
}


print("rm -rf /c/dev/opt/keycloak && tar -xf /c/dev/opt/keycloak-6.0.0.tar.gz -C /c/dev/opt/ && mv /c/dev/opt/keycloak-6.0.0 /c/dev/opt/keycloak")
print("./keycloak/bin/standalone.sh &")
print("./keycloak/bin/add-user.sh admin Admin123$ --silent")
print("./keycloak/bin/jboss-cli.sh -c --command=\"/subsystem=logging/logger=com.dell.software:add\"")
print("./keycloak/bin/jboss-cli.sh -c --command=\"/subsystem=logging/logger=com.dell.software:write-attribute(name=\"level\", value=\"DEBUG\")\"")

for k in modules:
    print("./keycloak/bin/jboss-cli.sh -c --command=\"module add --name={} --resources={} --module-xml={}\"".format(k, modules[k][0], modules[k][1]))

# print("./keycloak/bin/jboss-cli.sh -c --command=\"/subsystem=datasources/jdbc-driver=postgres:add(driver-name=\"postgres\",driver-module-name=\"org.postgresql.jdbc\",driver-class-name=org.postgresql.Driver)\"")
# print("./keycloak/bin/jboss-cli.sh -c --command=\"data-source add --jndi-name=java:jboss/datasources/Keycloak_DS --name=Keycloak_DS --connection-url=jdbc:postgresql://localhost:5432/keycloak --driver-name=postgres --user-name=keycloak --password=password\"")

print("./keycloak/bin/jboss-cli.sh -c command=\"/system-property=cbas.config:add(value=\"C:\\dev\\dibv2-configuration\\beanstalk\\dev\\config\\cbas.config\")\"")
print("./keycloak/bin/jboss-cli.sh -c command=\"/system-property=charon.config:add(value=\"C:\\dev\\dibv2-configuration\\beanstalk\\dev\\config\\charon.config\")\"")
print("./keycloak/bin/jboss-cli.sh -c command=\"/system-property=microsoftservice.config:add(value=\"C:\\dev\\dibv2-configuration\\beanstalk\\dev\\config\\microsoftservice.config\")\"")
print("./keycloak/bin/jboss-cli.sh -c command=\"/system-property=profileservice.config:add(value=\"C:\\dev\\dibv2-configuration\\beanstalk\\dev\\config\\profileservice.config\")\"")
print("./keycloak/bin/jboss-cli.sh -c command=\"/system-property=salesforce.config:add(value=\"C:\\dev\\dibv2-configuration\\beanstalk\\dev\\config\\salesforce.config\")\"")

print("./keycloak/bin/jboss-cli.sh -c command=:reload &")
print("./keycloak/bin/jboss-cli.sh -c command=:shutdown")
print("./keycloak/bin/standalone.sh -b 0.0.0.0 --debug -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=C:\\dev\\dibv2\\server-dist\\src\\server\\keycloak\\keycloak-exported-data.json")