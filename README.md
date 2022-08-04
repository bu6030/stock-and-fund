# stocke-and-fund
自己写着玩的一个股票基金SpringBoot小项目
支持添加删除股票基金

## 本机Docker环境一键部署
1. 本地package出jar包
2. 修改localDeployment.sh文件中的本地sqllite文件绝对路径位置，也就是项目中的stock-and-fund.db文件的绝对路径
执行项目根目录下localDeployment.sh
./localDeployment.sh
3. 本机访问http://localhost:8080/main

## 本机Idea启动
1. 修改Run/Debug Configurations->Spring Boot中增加环境变量，sqllite文件绝对路径位置，也就是项目中的stock-and-fund.db文件的绝对路径
sqllite.db.file=/XXXX/XXXX/stock-and-fund.db
2. 本机通过SpringBoot start类直接run main方法启动
3. 本机访问http://localhost:8080/main
