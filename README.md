panda-merge
├── panda-common -- 工具类及通用代码
├── panda-repo -- MyBatisGenerator生成的数据库操作代码(不要修改生成的类)
├── panda-api -- 给外部暴露的接口
├── panda-odds-admin -- 赔率服务启动项目
├── panda-nonrealtime-admin -- 非实时服务启动项目
├── panda-realtime-admin -- 实时服务启动项目
├── panda-dubbo-consumer -- dubbo服务接口调用测试



日志查看堡垒机（找不到服务在那台机器上面的可以找运维咨询）

    开发，测试地址：http://lan-jumperserver.sportxxxr1pub.com/ui/#/workbench/assets 账号 / 密码：develop / sduih789@123ER  
        开发环境服务器：
            非实时服务：dev-k8s环境服务器 ==》dev-panda-data-service-01
            实时服务：dev-k8s环境服务器 ==》dev-panda-data-service-01
        测试环境服务器：
            非实时服务：test-k8s ==》test-k8s-data-01 到 test-k8s-data-05 随机部署
            实时服务：test-k8s ==》test-k8s-data-01 到 test-k8s-data-05 随机部署
    隔离环境地址：http://jp-yunwei-jumperserver.sportxxxr1pub.com/ui/#/workbench/assets 账号 / 密码：dev2 / 4pBjKhvMvC0Yi
        隔离环境服务器：
            非实时服务：隔离预发k8s集群服务器 ==》 lspre-k8s-ds-09-10.105.20.19,lspre-k8s-ds-10-10.105.20.20
            实时服务：隔离预发k8s集群服务器 ==》 lspre-k8s-ds-11-10.105.20.21,lspre-k8s-ds-12-10.105.20.22
    生产环境地址：http://idc-pro-jumpserver.sportxxxrok.com/luna/ 需要开VPN，账号密码找主管
        生产环境服务器：
            非实时服务：数据支撑组-服务器  ==》 pro-k8s-panda-ds-new-09,pro-k8s-panda-ds-new-10
            实时服务：数据支撑组-服务器  ==》 pro-k8s-panda-ds-new-11,pro-k8s-panda-ds-new-12
