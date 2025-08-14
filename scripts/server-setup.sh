#!/bin/bash
sudo apt-get update

# Install kubectl
printf "Installing kubectl...\n"
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl.sha256"
echo "$(cat kubectl.sha256)  kubectl" | sha256sum --check
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
kubectl version --client

# Install kubeadm
printf "Installing kubeadm...\n"
sudo apt-get install -y apt-transport-https ca-certificates curl gpg
curl -fsSL https://pkgs.k8s.io/core:/stable:/v1.33/deb/Release.key | sudo gpg --dearmor -o /etc/apt/keyrings/kubernetes-apt-keyring.gpg
echo 'deb [signed-by=/etc/apt/keyrings/kubernetes-apt-keyring.gpg] https://pkgs.k8s.io/core:/stable:/v1.33/deb/ /' | sudo tee /etc/apt/sources.list.d/kubernetes.list
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
sudo systemctl enable --now kubelet

# Install containerd (CRI)
printf "Installing containerd...\n"
apt-get update
apt-get install -y ca-certificates curl gnupg lsb-release
sudo apt install -y containerd

systemctl daemon-reload
systemctl enable --now containerd
systemctl restart containerd

# sysctl params required by setup, params persist across reboots
cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.ipv4.ip_forward = 1
EOF

# Apply sysctl params without reboot
sudo sysctl --system

sysctl net.ipv4.ip_forward

# Initialize control-plane
printf "Initializing control-plane...\n"
sudo kubeadm init --pod-network-cidr=192.168.0.0/16

export KUBECONFIG=/etc/kubernetes/admin.conf

printf "Waiting for control-plane node to be ready...\n"
kubectl wait --for=condition=Ready node/mini-dcp-server --timeout=300s

# Install tigera
printf "Installing Tigera...\n"
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.30.2/manifests/tigera-operator.yaml

# Install calico 
printf "Installing Calico...\n"
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.30.2/manifests/custom-resources.yaml

printf "Waiting for Calico pods to be ready...\n"
kubectl wait --for=condition=Ready pods --all -n calico-system --timeout=120s

# Remove taint from control-plane node
printf "Removing taint from control-plane node...\n"
kubectl taint nodes --all node-role.kubernetes.io/control-plane-
kubectl get nodes -o wide

printf "Installing ArgoCD...\n"
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

printf "Installing ArgoCD CLI...\n"
curl -sSL -o argocd-linux-amd64 https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
sudo install -m 555 argocd-linux-amd64 /usr/local/bin/argocd
rm argocd-linux-amd64

printf "Create NodePort service for ArgoCD...\n"
kubectl apply -f https://raw.githubusercontent.com/OliverJochum/mini-dcp/main/argo-config/argocd-nodeport-svc.yaml

printf "Creating flightsearch-app from manifest file...\n"
kubectl config set-context --current --namespace=argocd
kubectl apply -f https://raw.githubusercontent.com/OliverJochum/mini-dcp/main/gitops/flightsearch-app.yaml