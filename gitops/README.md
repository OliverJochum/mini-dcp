### Documentation of technologies & processes

## Containers & VMs
https://www.freecodecamp.org/news/a-beginner-friendly-introduction-to-containers-vms-and-docker-79a9e3e119b

Similarities:
 - both aim to isolate an application & its dependencies in a self-contained unit that runs anywhere
 - remove need for physical hardware, allowing for more efficient & cost-effective use of resources

## VM
 - Emulation of real computer, executes programs like real computer
 - Runs on top of physical machine using a hypervisor
    - piece of software/firmware/hardware that VM runs on top of
    - runs on host machine
    - provides VMs with a platform to manage & execute guest OS & allows hosts to share their resources
 - host machine provides resources, including RAM & CPU
 - resources can be distributed at will among multiple VMs, if one needs more it can receive more
![alt text](https://cdn-media-1.freecodecamp.org/images/1%2ARKPXdVaqHRzmQ5RPBH_d-g.png)

## Container
 - Instead of providing hardware virtualization like VM, provides OS level virtualization by abstracting the "user space"
 - shares host systems kernel with other containers
 - packages just the user space, not kernel or virtual hardware like VM
 - each container gets its own isolated user space, allowing multiple to run on single host machine
 - OS level architecture is shared across containers
 - only bins and libs are created from scratch -> lightweight
 ![alt text](https://cdn-media-1.freecodecamp.org/images/1%2AV5N9gJdnToIrgAgVJTtl_w.png)

## Docker
Docker allows me to containerize my application. The manual barebones way of doing this is:

1. Create a Dockerfile. The Dockerfile defines an image. In it you can set what base image you will use (what jdk for example), what dependencies you want to pull in for you application, the entrypoint (what command to run on start), what ports to expose etc.

2. Build the image with `docker build -t tagname:version`

3. Start the container from an image with `docker run tagname:version`
3a. For some reason I need to specify the -p flag for localhost:5100 to run as expected: `docker run -p 5100:5100 flightsearch-app:latest`

## Kubernetes
To create a local kubernetes cluster and load my docker image in I use kind (kubernetes in Docker). In my gitops folder I've created a flightsearch-app.yaml that sets up a Pod which contains the docker image that should be loaded into it. The imagePullPolicy is set to never because using the latest tag will mean kind will try to pull the image from a container registry. Because the image is local I am currently manually loading it in and don't want it pulled from docker.io. 

The yaml also defines a Service, which allows me to expose the demo app. The current workflow is as follows:

1. Create cluster using `kind create cluster`
2. Write manifest file `flightsearch-app.yaml` to define Pod & Service (necessary for correct image and to expose container port)
3. Load docker image into cluster with `kind load docker-image flightsearch-app:latest` <- without this kind won't find the local image
4. Apply manifest file to cluster using `kubectl apply -f flightsearch-app.yaml` 
5. Port forward Service port to locahost `kubectl port-forward service/flightsearch-app-service 5100:80`

# Architecture
![alt text](https://courses.edx.org/asset-v1%3ALinuxFoundationX%2BLFS158x%2B1T2024%2Btype%40asset%2Bblock/TrainingImage.png)
