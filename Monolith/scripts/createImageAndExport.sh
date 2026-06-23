#!/usr/bin/sh

imageName=$1
tag=$2

docker build -t "${imageName}:${tag}" .
docker save "${imageName}:${tag}" -o "${imageName}".tar
sudo k3s ctr images import "${imageName}".tar
sudo k3s ctr images list | grep "${imageName}"
exit $? 
