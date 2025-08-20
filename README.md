# Setup guide
1. [What is the Mini-DCP?](#what-is-the-mini-dcp)
2. [Local run with Gradle](#local-run-with-gradle)
3. [OpenAPI definition](#openapi-definition)
4. [Connect to server via SSH](#connect-to-server-via-ssh)
    - [Config setup](#config-setup)
5. [Mini-DCP on server](#mini-dcp-on-server)
    - [Accessing the server via NodePorts](#accessing-the-server-via-nodeports)
    - [Debugging](#debugging)
### What is the Mini-DCP?
The Mini-DCP is a demonstration of DevOps/GitOps skills learned over the course of two months while shadowing a member of LHGDH's Digital Core Platform. It is meant to be a fully automated process to setup, deploy and maintain an application. In this case the demo-application is a small Spring Boot API that mocks searching for flights. The application is deployed using ArgoCD in a Kubernetes cluster on a server
### Local run with Gradle

```bash
# installation
./gradlew clean build

# development
./gradlew bootRun

# testing
./gradlew test
```

### OpenAPI definition
http://localhost:5100/api/swagger-ui/index.html#/

### Connect to server via SSH
## Config setup
```bash
nano .ssh/config

# .ssh/config
Host mini-dcp-server
  HostName <SERVER-PUBLIC-IP>
  User root
  IdentityFile ~/.ssh/<PRIVATE-KEY>
```
Connect to server (assuming ssh keypair already in place)
```bash
ssh-keygen -R <SERVER-PUBLIC-IP>

ssh mini-dcp-server
```
### Mini-DCP on server

To install the entire infrasture: Flightsearch-app API watched by ArgoCD within a K8s cluster, run the latest setup script (setup/scripts/server-setup-v3.sh)

```bash
curl -O https://raw.githubusercontent.com/OliverJochum/mini-dcp/refs/heads/main/setup/scripts/server-setup-v3.sh
chmod +x server-setup-v3.sh
bash server-setup-v3.sh
```
## Accessing the server via NodePorts
ArgoCD's UI can be found on port 30007:
```bash
<SERVER-PUBLIC-IP>:30007
```
The flightsearch-app is exposed on port 31000:
```bash
<SERVER-PUBLIC-IP>:31000/api/swagger-ui/index.html#/
```

## Debugging
If the setup script seems stuck waiting for the control plane to be ready you can finish running it via CTRL + C. 

Then 
```bash
# apply kubeconfig to use kubectl 
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config

# Reapply CNI installation 
kubectl create -f https://raw.githubusercontent.com/projectcalico/calico/v3.30.2/manifests/custom-resources.yaml

# check that argo pods are initializing
kubectl get pods -n argocd

#check on control plane
kubectl get nodes -o wide
```
