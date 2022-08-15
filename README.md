#初始化表数据
##盈利汇总数据
```
CREATE TABLE DEPOSIT (
  DATE TEXT(10) PRIMARY KEY NOT NULL,
  FUND_DAY_INCOME CHAR(20) NOT NULL,
  STOCK_DAY_INCOME CHAR(20) NOT NULL,
  DAY_INCOME CHAR(20) NOT NULL,
  FUND_MARKET_VALUE CHAR(20) NOT NULL,
  STOCK_MARKET_VALUE CHAR(20) NOT NULL,
  TOTAL_MARKET_VALUE CHAR(20) NOT NULL
);
```
##基金数据
```
CREATE TABLE FUND (
  CODE TEXT(10) PRIMARY KEY NOT NULL,
  COST_PRICE CHAR(20) NOT NULL,
  BOUNDS CHAR(30) NOT NULL,
  APP CHAR(10) NOT NULL
);
```
##股票数据
```
CREATE TABLE STOCK (
  CODE TEXT(10) PRIMARY KEY NOT NULL,
  COST_PRICE CHAR(20) NOT NULL,
  BOUNDS INT NOT NULL,
  APP CHAR(10) NOT NULL
);
```
##字典数据
```
CREATE TABLE PARAM (
  TYPE TEXT(10) NOT NULL,
  CODE CHAR(20) NOT NULL,
  NAME INT NOT NULL,
  PRIMARY KEY (`TYPE`,`CODE`)
);
CREATE INDEX IDX_PARAM_TYPE ON PARAM(TYPE);
```
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
