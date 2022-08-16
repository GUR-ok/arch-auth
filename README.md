Микросервис авторизации

Сборка и установка в minikube
1) gradle build
2) docker build -t gurok/arch_auth .
3) docker push gurok/arch_auth
4) kubectl create namespace arch-gur   
5) helm install arch-auth ./deployment/
   kubectl get pods -n arch-gur

---
helm uninstall arch-auth
kubectl delete namespace arch-gur