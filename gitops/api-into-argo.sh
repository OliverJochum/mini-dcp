argocd admin initial-password -n argocd
argocd login localhost:8080 --insecure --username admin --password $(kubectl get secret -n argocd argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d)
kubectl config set-context --current --namespace=argocd