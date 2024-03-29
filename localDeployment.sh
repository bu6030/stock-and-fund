#!/bin/bash
#部署前本地先maven package打包
#本地部署到本机docker
echo ========开始执行本地Docker部署========

CURRENT_V=$(echo $line | grep 'SNAPSHOT' pom.xml | awk '{split($0,a,"-"); print a[1]}'| awk '{split($0,a,">"); print a[2]}')
echo ========当前版本$CURRENT_V========

echo ========停止以及删除旧版本========
docker stop stock-and-fund-$CURRENT_V
docker rm stock-and-fund-$CURRENT_V
docker rmi stock-and-fund/$CURRENT_V

echo ========开始build新版本$NEW_V========
docker build -t stock-and-fund/$CURRENT_V .

echo ========开始部署新版本$NEW_V========
docker run -d -p 8080:8080  \
--env sqllite.db.file=/stock-and-fund.db \
--env key-cloak-server-address=YOUR_KEYCLOAK_SERVER_ADDRESS \
--env local-address=http://localhost:8080 \
-v /Users/XuesongBu/Documents/git_code/stock-and-fund/stock-and-fund.db:/stock-and-fund.db \
-v /Users/XuesongBu/Documents/git_code/bu6030.github.io/qr_code/:/qr_code \
--name stock-and-fund-$CURRENT_V -v /etc/localtime:/etc/localtime stock-and-fund/$CURRENT_V
echo ========本地Docker部署完成========