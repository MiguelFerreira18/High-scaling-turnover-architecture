#!/usr/bin/sh

export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

kubectl apply -f ./k8s/
kubectl get pods -n app-namespace

kubectl describe pod -n app-namespace
