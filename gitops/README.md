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