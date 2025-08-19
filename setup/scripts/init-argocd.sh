#!/bin/bash
kind delete cluster --name mini-dcp
printf "Initializing ArgoCD...\n"
printf "Creating mini-dcp cluster...\n"
kind create cluster --name mini-dcp

printf "Installing ArgoCD..."
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

printf "port-forwarding ArgoCD to localhost:8080\n"

while [[ $(kubectl get pods -n argocd -l "app.kubernetes.io/name=argocd-server" -o jsonpath='{.items[0].status.phase}') != "Running" ]]; do
  echo "⏳ Waiting for pod to be Running..."
  sleep 2
done
kubectl port-forward svc/argocd-server -n argocd 8080:443 &

printf "Creating flightsearch-app from manifest file...\n"
kubectl config set-context --current --namespace=argocd
kubectl apply -f https://raw.githubusercontent.com/OliverJochum/mini-dcp/main/gitops/flightsearch-app.yaml
