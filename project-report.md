# Introduction

## Motivation

At the Digital Hangar the department that handles the infrastructure part of the backend is known as the Digital Core Platform(DCP). They mainly concern themselves with providing a repository and pipelines to allow backend developers to deploy and maintain their APIs via a ‘self-service’ approach. They do this by applying a modern GitOps pattern. The goal of this project is to learn about the underlying infrastructure and self-service they provide for developers and to demonstrate my knowledge of their DevOps processes and technologies by building a fully automated process myself to setup, deploy and maintain a hypothetical application.
 
## Concepts
The main concepts that are covered in this project are:
* Containerization
* Container Orchestration
* GitOps
* Technologies

The main technologies used to implement these concepts are:
* Docker - build images and register them in container registry
* Kubernetes - automate the deployment, scaling and management of containerized applications
* ArgoCD - automate the deployment and lifecycle management of Kubernetes clusters via Git repositories

## Goals
The goal of the final project is to develop an automated process to setup, deploy & maintain an application. The project should include:
* An automated setup script and deployment pipelines
* Fully scalable solution
* Failsafe 

The focus is on the infrastructure itself, therefore the demo application included in the project is trivial. The infrastructure should be designed in such a way that it could support the deployment of any application regardless of complexity.

## Architecture
A fully fledged self-service infrastructure will need to be set up in a repository in which developers working on the API can push changes to the source code. The repository must also contain folders to describe the deployment of the app and the resources it needs. The basic outline of the infrastructure is detailed below.

<img width="739" height="426" alt="Screenshot 2025-09-08 at 10 35 35" src="https://github.com/user-attachments/assets/32a19e2d-ff65-4e18-9d86-ba7de8622b8b" />

The app will be deployed in a Kubernetes cluster sitting on a rented x86 Server running Ubuntu from Hetzner. ArgoCD will be used to sync the state of the application at all times - if a change is made in the repo, Argo will apply the change to the deployment. To do this a container registry is necessary: somewhere where new images can be pushed to when changes are made, and then pulled from to deploy. The repository itself will contain the demo service, flightsearch-app, manifest files that describe the deployment of the app for ArgoCD, scripts to setup the Hetzner server and a CI pipeline to automate the process of testing, building and pushing a new image to the container registry. 

# Pre-Project Learning

## Containerization (Docker)
Containerization sits at the core of this project. The concept is to isolate applications and their dependencies in a self-contained unit that can run anywhere. This allows developers to build and ship applications regardless of the hardware they’re working on. To manage and run containers for this project Docker will be used. A Docker container is run from an image. An image of an application is a standardized package that includes all of the files, binaries, libraries, and configurations to run a container ([Docker Docs](https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-an-image/)) in which that application is contained. To build an image a Dockerfile must be defined in the repository of the application, which contains the instructions on pulling necessary dependencies, source files, development kits etc. to build an executable of the application which will then run in the container. 

For the pre-project learning, the goal was only to build a docker image and then run a container locally. To do this a simple Dockerfile for the flightsearch-app was defined, which basically only needed to specify the JDK for Java 21, the gradlew command responsible for building the executable and the entrypoint of the generated .jar file. 

To build image:

`docker build -t tagname:version`

To run container from image:

`docker run -p 5100:5100 tagname:version`

## Kubernetes (Kind)
To gain an understanding of Kubernetes and how to work with it, a local version, Kind, was deployed. Kind, or Kubernetes In Docker, deploys a Kubernetes cluster in Docker containers, with each node in its own container. Kubernetes is a vast technology with lots of concepts that one can get into endless depth over, but for the initial understanding two components stand above the rest. 

The control plane node is the brain of the operations in a K8s cluster. It communicates with and gives instructions to worker nodes, which will manage the containers that, among other things, applications we want to deploy sit in. A developer can communicate with the API server of the control plane node via kubectl in the CLI which allows him to manage and configure the cluster.

<img width="900" height="435" alt="68747470733a2f2f636f75727365732e6564782e6f72672f61737365742d76312533414c696e7578466f756e646174696f6e582532424c465331353878253242315432303234253242747970652534306173736574253242626c6f636b2f547261696e696e67496d6167652e706e67" src="https://github.com/user-attachments/assets/da82e43e-433b-4192-8321-ff980e8b67de" />

With the first exposition to Kubernetes clusters also comes the first exposition to manifest files. A YAML file can be used to define components in Kubernetes, creating an easy and manageable way to configure entire clusters without having to manually create each node through the CLI. This also allows us to repeat builds we create. Additionally, since using git, working with manifest files will create the ability to not only have a record of the cluster we’re building but also automatically apply version control to it. 

Setting up a local cluster that, for now, also pulls a local image provided to it instead of pulling one from a container registry can be done in a few simple steps.

The workflow is as follows:
1. Create cluster using `kind create cluster`
2. Write manifest file **flightsearch-app.yaml** to define Pod & Service 
3. Load docker image into cluster with `kind load docker-image flightsearch-app:latest` <- without this Kind won't find the local image
4. Apply manifest file to cluster using `kubectl apply -f flightsearch-app.yaml`
5. Port forward Service port to localhost `kubectl port-forward service/flightsearch-app-service 5100:80`

<img width="279" height="403" align="left" alt="Screenshot 2025-09-08 at 10 48 26" src="https://github.com/user-attachments/assets/ddc802d1-8474-4cfa-8f81-392cc37f5959" /> Following the [Quickstart Documentation](https://kind.sigs.k8s.io/docs/user/quick-start/) provided for Kind, a cluster can be set up with one command outlined in step 1. From there we need to define a manifest file that defines a Pod (smallest deployable unit that can be created in K8s), in which the container running the image of our flightsearch-app sits. Since the cluster will not be pulling an image from a repository we have to locally provide the image for the cluster using the kind command in step 3. The manifest file can be applied to the cluster using the command in step 4, which will configure the cluster to build the components defined.

Additionally a service is defined which exposes the container port. This is by default an internal port and IP called ClusterIP since for security reasons typically you don’t directly access the API from the outside. However for testing purposes there are a few ways the API can be exposed to a localhost port. The easiest way for this local experiment is to use kubectl’s port-forward command which forwards the port 80 which we’ve reserved for the Service to _localhost:5100_. From there we can confirm everything is running smoothly by using kubectl to check that the pod, control pane node and service are running and trying out the API at _localhost:5100_ via the OpenAPI specification.
<br clear="left"/>
## ArgoCD locally in Kind
ArgoCD will serve as the continuous delivery tool for this project but also represents the centerpiece of implementing the GitOps pattern within the infrastructure. It automates the deployment of the desired application states in the specified target environments. ([ArgoCD docs](https://argo-cd.readthedocs.io/en/stable/)). In a manual workflow, if a developer were to make changes to the cluster he was working on, he would update the manifest files with the desired change (for example, changing which port to expose in a service). He would then use kubectl apply to change the configuration in the cluster to the newly desired one. ArgoCD essentially automates this process as it watches a repository for changes and then syncs them to the deployment in the cluster. 

Once again, to gain an understanding of the technology it was deployed locally within a Kind cluster. To do this a Kind cluster was set up and a namespace for argo was defined within which applying the ArgoCD manifest files would run the setup for us. Then to access ArgoCD it’s Service was port-forwarded which allows access either via the UI on localhost or the CLI. 

To deploy the application within ArgoCD one must create an application either via the UI or command line. Initially this process was done via the UI. The most important detail here is that by creating an application you are pointing ArgoCD to your repository, more specifically the location of your manifest files from which it can deploy your application. 

<img width="556" height="360" alt="Screenshot 2025-09-08 at 10 53 09" src="https://github.com/user-attachments/assets/dda78e31-c8e9-47c4-bc7f-16877cd3f262" />

Once everything is set up the application can be viewed in the ArgoCD UI. In the local example a deployment of just a pod and a service was defined which can be seen in the image above. The health of the application is managed by ArgoCD, if an error occurs it will let you know while also trying to restart the container. By default, after a change is made in the repository it’s watching, the sync button can be used to sync any changes that may have occurred to the deployment. 

## Setup scripting
The key goal of the entire project is automation. By this point, local setup is tedious at best and a headache at worst. Bash scripts were developed to automate the setup process, initially also local but eventually for the entire server setup. Two scripts were developed initially, **init-argocd.sh** and **api-into-argo.sh**. The role of the first script was to run all the commands needed to install argo, port-forward the argo-server. The second script was supposed to login to argo, add the repo so that argo sees it and then use argocd app create to create the application. A later realization that things could be simplified refined the scripts into only using **init-argocd.sh** to also create the flightsearch-app application in argo via `kubectl apply` instead of manually in the UI or via the argocd command. This allowed one script to automate the entire local setup process.

# Project

Gaining a general understanding of the technologies is good, but deploying locally is far from the goal.
 
## Implementing the GitOps approach 
The GitOps pattern, in one sentence is: The repository acts as the single source of truth. In the scope of this project this means that everything is done through the repository. The structure of a deployment is present in the repository. If a deployment is to be configured, this is done through a commit. If something goes wrong we want a failsafe approach, what better way than to simply revert a commit that has done damage and let ArgoCD’s syncing handle the rest?

Since the repository is the single source of truth, the structure of the repository is extremely important.
 
<img width="721" height="409" alt="Screenshot 2025-09-08 at 10 58 27" src="https://github.com/user-attachments/assets/c4a13ef1-7adc-4b75-9364-9210cf5f7ce8" />

The **.github/workflows** folder contains YAML files that describe the Github Actions pipelines, in the case of this project this is the CI/CD pipeline. 

The **flightsearch-app** is the demo application, it contains a Dockerfile to build an image of the app. 

The **gitops** directory contains the resources folder which holds the manifest files of the application for Kubernetes. This folder is referenced in the application file (in **setup/argo**). The reason the application file is kept separate is to prevent an infinite loop. The resources folder is also referenced by the **kustomization.yaml**, which is the file that patches the resources. 

The **setup** directory contains the **argo** folder, which contains project and app YAML files and a Nodeport service for the argocd-server, so that it can be accessed on port 30007. The server setup scripts are contained in the **scripts** folder, so that they can be pulled onto the server easily via `curl` and executed.

## Hetzner Server
Unfortunately it was not possible to use Digital Hangar’s resources to deploy in Azure DevOps, so an alternative solution was found instead. Hetzner provides servers at a very cheap price (as low as 3.92 euros/month with 2 shared vCPUs). This solution also allowed a much more barebones playground environment to work with. Setting up the Hetzner server only takes a few clicks. To connect to the server an ssh keypair was generated. After that, an alias was created in the .ssh folder to allow me to connect to the server easily just using ssh mini-dcp-server. 

## Setup (Installing K8s + CNI callico) & Scripting
The project infrastructure will run in Kubernetes clusters, but everything now needs to sit on the mini-dcp-server. Following the [Documentation for the Kubeadm](https://kubernetes.io/docs/setup/production-environment/tools/kubeadm/install-kubeadm/) toolbox to setup a Kubernetes cluster these tools are needed:
1. containerd - the container run time, which is the software that will actually run the containers as instructed by K8s
2. Kubeadm, to configure the cluster and initialize the control plane node

     In addition to Kubeadm, Kubelet and Kubectl will be installed. A kubelet is an agent which runs on every node and is the bridge between the control plane and that node's container runtime. Kubectl is the command line tool already used while trying Kubernetes out locally, and will be necessary for general configuration.
3. Calico, a CNI, or Container Network Interface, which handles communication between Pods. Without it, no networking is possible 

After all the prerequisites are installed, the control plane needs to be initialized via kubeadm. 

Next Calico, the chosen CNI for this project is installed via kubectl apply. Since this project is a single-node cluster, in other words our control plane is sitting on the same machine as our worker nodes, taint has to be removed from the control plane([Calico docs](https://docs.tigera.io/calico/latest/getting-started/kubernetes/k8s-single-node)). Taint is a mechanism that controls pod scheduling. By default, taint prevents pod scheduling on a node. This is generally useful for the control plane (or master node) because typically in a production environment you would have the control plane sitting on its own machine and keep the worker nodes on separate machines (or “resources”, depending on whether you’re using a cloud environment like azure etc.). After the control-plane has been untainted and confirmed to be ready, the same steps as in the local environment could be followed to install ArgoCD and deploy the flightsearch-app application via kubectl apply and manifest files.

While initially trying out the setup and of course debugging the process along the way the process was very manual, but once most the issues had been ironed out the commands executed one by one were entered into a bash script so that setting up the server could be done more automatically, from configuration to installation to deployment of the application in ArgoCD.
 
## ArgoCD on server & port configuration
Now that ArgoCD is running on an actual server and is not just confined to a local environment a few extra things needed to happen that weren’t necessary when just trying to understand what was being worked with. For one, the flightsearch-app should have a more refined structure by this point than simply building a service and one pod from a manifest file. The application itself can be defined by a YAML file, containing the information that would otherwise have been entered into the UI or CLI. This file will point to the folder in which you have the manifest files that describe the application. At this point the flightsearch-app structure had been split into a service which remained pretty much unchanged from the local version and a deployment, which defines a ReplicaSet of three pods that are running containers of the flightsearch-app. While it wasn’t further delved into within the scope of this project, setting up the application using ReplicaSets sets it up for scalability, since you are no longer manually deploying pods and can dynamically change how many pods should be deployed based on traffic. 

In addition to the base service, a NodePort service was defined to expose the flightsearch-app in order to view the OpenAPI definition on the server as well. This was also done for ArgoCD to access the UI of ArgoCD.

Lastly, an AppProject was defined in another YAML file out of an organizational interest - if you were running an actual microservice architecture or had multiple ones that your company was managing they would all be seen in the same instance of ArgoCD but you would want to logically group them via AppProjects. 

## Continuous Integration
In order to streamline the process in which a developer produces production ready code, a Continuous Integration pipeline should be developed. This pipeline automates the process of testing new code, rebuilding the application and redeploying it. For this project the pipeline was set up using Github Actions (convenient since the repo is sitting on Github anyways). Github Actions allows one to define jobs in a YAML file which will run commands that execute the different parts of the pipeline. One important feature is the ability to have jobs depend on each other, so that you aren’t building a new image before you’ve tested the code or updating the deployment before you’ve pushed a new image for example.

The structure of the pipeline is detailed below:

<img width="685" height="334" alt="Screenshot 2025-09-08 at 11 05 29" src="https://github.com/user-attachments/assets/4ad702ea-438c-49f8-a140-18e8e7fd2dd0" />

The testing process is simple, perform unit testing (in this case via gradlew test). After the tests have passed the Build & Push job logs in to the container registry, builds a new image via the Dockerfile and pushes that image. The Dockerfile has been updated at this point to be multi-stage which wasn’t necessary when locally playing with things. Instead of simply defining the executable the Dockerfile now also builds the jar that it is using for the executable as well, which beforehand was being done manually. 

The deployment is then “patched” with a new tag and the changes made to the **kustomization.yaml** file, which handles the patching, are pushed. ArgoCD will detect the commit and apply the changes on the server when it syncs. 

Patching a manifest file is done using a tool called Kustomize. It allows you to define a **kustomization.yaml** which you can edit for Kubernetes to use to change active deployments. ArgoCD will detect the change to the kustomize file (since the change will be a commit) and then configure the deployment. By changing the image tag the pods will then find the new image tag and pull it from the container registry.

# Conclusion
Overall the project goal was to use the GitOps approach to create a self-serviceable infrastructure which aimed to be mainly automatic, but also failsafe and scalable. I believe the project succeeded in its main goals, the infrastructure that was built could easily replace the demo API with a different Spring application or even a different framework entirely, provided the Dockerfile and CI/CD pipeline was adapted to that framework. Otherwise the infrastructure could pretty much remain as is (not counting obvious replacements of names like “flightsearch-app”). 

The infrastructure should also support multiple APIs in theory, for example the CI/CD pipeline was already set up to run per service. Generally since my repo is set up in more of a monolith approach one could add a directory for an additional API and then adapt the manifest files but an approach that is more sound with what the DCP in the Digital Hangar also does is just providing the GitOps folders for each repo from which you can build your API in a more micro-service approach. 

It was also hoped initially that Infrastructure-as-Code could be explored through Terraform, however due to time constraints and more importantly due to shifting technologies it fell out of the scope of the project. IaC is especially useful for describing cloud resources, like on Azure, but since the solution switched to a single-node cluster sitting on a hetzner server, the need to describe the single resource was no longer as appropriate. 
