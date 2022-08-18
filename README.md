Микросервис авторизации

Сборка и установка в minikube
1) gradle build
2) docker build -t gurok/arch_auth .
3) docker push gurok/arch_auth
4) kubectl create namespace arch-gur   
5) helm install arch-auth ./deployment/
   kubectl get pods -n arch-gur

---
Для генерации jwk:
keytool -genkeypair -alias mytest -keyalg RSA -keypass mypass -keystore mytest.jks -storepass mypass
---
helm uninstall arch-auth
kubectl delete namespace arch-gur

kubectl port-forward -n arch-gur arch-auth-postgresql-deployment-0 5433:5432