> 2026-10-26 已完成本项目独立 Gateway 生产迁移，all-domains 已停放并保留回退。发布流程详见 deploy/istio/README.md；CI 本地修改尚未提交推送。

# Compose Foundation 入口

本项目即 KMP Web 部署项目，域名为 compose-foundation.devops.cn.toutiaoyan.com。现有 CI 的 scripts/k8s-deploy.sh 直接发布 namespace 和 Certificate，等待证书 Ready 后发布独立 Gateway、策略、业务资源及 base/istio-routes.yaml，不再调用额外入口准备或检查脚本。

旧 NGINX Ingress 已移动到 rollback/ingress.yaml，正常发布不引用，线上旧对象未删除。回退目录仅供明确安排的手动回退使用，请勿递归 apply 整个 deploy 目录。本次不操作集群或 DNS。
