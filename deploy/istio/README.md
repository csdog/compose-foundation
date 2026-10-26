# compose-foundation-web 独立 Gateway（2026-10-26 已迁移生产）

本项目使用 `istio-ingress/compose-foundation-web-gateway`，与其他项目共用 istio-edge Pod、80/443 和共享压缩实现。`gateway.yaml` 是实际 Istio Gateway，不再是 ConfigMap 分片；路由、证书、压缩及超时策略均在本项目维护。证书和 TLS Secret 名称保持不变，仍在 istio-ingress。

CI 直接执行必要入口发布步骤：发布业务 namespace 和 Certificate，等待 istio-ingress/web-foundation-compose-foundation-web-tls Ready（最多 600 秒），再发布项目 policies.yaml、gateway.yaml，以及业务资源和 VirtualService。镜像更新和 rollout 等待沿用各项目原有流程。证书申请和续签仍由 cert-manager 执行，证书等待失败即停止后续发布。

这些步骤直接放在现有 CI 发布命令或其已有的 scripts/k8s-deploy.sh 中，不再使用额外入口准备/检查脚本，不执行网关域名冲突检查、共享资源预检查或入口 server dry-run，也不为此新增公共仓库拉取。域名、证书、路由和策略仍由本项目清单维护。本次只修改本地源配置，不提交推送或触发生产发布。

2026-10-26 已完成本项目独立 Gateway 的生产迁移（重庆）。all-domains 已停放并保留回退；证书、DNS、应用镜像和网关 Pod 保持不变。迁移期间观测到长连接排空与重连，不能视为零中断。对应集群节点备份目录为 /backup/istio-independent-gateway-20261026/，回退使用 states/compose-001/state.json。源配置与 CI 的本地修改尚未提交推送。

rollback/ 中独立 Gateway 的回退配置只适用于完成交接之后。原 ConfigMap 分片和合并器保存在 rollback/history/*.snapshot，仅供历史恢复，不在正常发布中使用。
