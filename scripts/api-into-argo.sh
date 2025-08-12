# deprecated, since we can use kubectl apply to create the ArgoCD application directly

argocd admin initial-password -n argocd
argocd login localhost:8080 --insecure --username admin --password $(kubectl get secret -n argocd argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d)
kubectl config set-context --current --namespace=argocd
argocd repo add https://github.com/OliverJochum/mini-dcp.git
argocd app create flightsearch-app --repo https://github.com/OliverJochum/mini-dcp.git --path gitops --dest-server https://kubernetes.default.svc --dest-namespace default