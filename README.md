# Setup guide
1. [Local run with Gradle](#local-run-with-gradle)
2. [OpenAPI definition](#openapi-definition)
3. [Connect to server via SSH](#connect-to-server-via-ssh)
    - [Config setup](#config-setup)
4. [Mini-DCP on server](#mini-dcp-on-server)
    - [Accessing the server via NodePorts](#accessing-the-server-via-nodeports)
    - [Debugging](#debugging)

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
