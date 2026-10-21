#!/usr/bin/env bash
set -euo pipefail

ROOT="${ROOT:-${CI_PROJECT_DIR:-.}}"
K8S_NAMESPACE="${K8S_NAMESPACE:-web-foundation}"
K8S_DEPLOYMENT="${K8S_DEPLOYMENT:-compose-foundation-web}"
K8S_CONTAINER="${K8S_CONTAINER:-web}"

if [[ -z "${FULL_IMAGE:-}" ]]; then
  echo "FULL_IMAGE 未设置" >&2
  exit 1
fi

kubectl apply -f "${ROOT}/deploy/base/namespace.yaml"

if [[ -n "${DOCKER_USER:-}" && -n "${DOCKER_PASSWORD:-}" ]]; then
  kubectl create secret docker-registry harbor-registry-secret \
    --docker-server="${HARBOR_REGISTRY:-harbor.devops.toutiaoyan.com}" \
    --docker-username="${DOCKER_USER}" \
    --docker-password="${DOCKER_PASSWORD}" \
    -n "${K8S_NAMESPACE}" \
    --dry-run=client -o yaml | kubectl apply -f -
elif ! kubectl get secret harbor-registry-secret -n "${K8S_NAMESPACE}" >/dev/null 2>&1; then
  echo "namespace ${K8S_NAMESPACE} 缺少 harbor-registry-secret，且未提供 DOCKER_USER / DOCKER_PASSWORD" >&2
  exit 1
fi

kubectl apply -f "${ROOT}/deploy/base/web-service.yaml"
kubectl apply -f "${ROOT}/deploy/base/web-deployment.yaml"
kubectl apply -f "${ROOT}/deploy/base/ingress.yaml"

echo "Rolling image to ${FULL_IMAGE}"
kubectl set image "deployment/${K8S_DEPLOYMENT}" \
  "${K8S_CONTAINER}=${FULL_IMAGE}" \
  -n "${K8S_NAMESPACE}"
kubectl rollout status "deployment/${K8S_DEPLOYMENT}" -n "${K8S_NAMESPACE}" --timeout=300s
