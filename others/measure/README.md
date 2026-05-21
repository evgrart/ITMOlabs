wget https://github.com/wildfly/wildfly/releases/download/39.0.1.Final/wildfly-39.0.1.Final.zip
unzip -q wildfly-39.0.1.Final.zip
mv wildfly-39.0.1.Final wildfly
rm -rf wildfly-39.0.1.Final.zip
cd wildfly/bin
vim standalone.conf
cd ..
mv postgresql.jar wildfly/standalone/deployments/
unset _JAVA_OPTS
cd wildfly/
cd bin
./add-user.sh
./standalone.sh -Djboss.socket.binding.port-offset=2000
