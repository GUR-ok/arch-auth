Микросервис авторизации

Сборка и установка в minikube
1) gradle build
2) docker build -t gurok/arch_auth_2 .
3) docker push gurok/arch_auth_2
4) kubectl create namespace arch-gur   
5) helm install arch-auth ./deployment/
   kubectl get pods -n arch-gur

---

Для генерации jwk:
keytool -genkeypair -alias mytest -keyalg RSA -keypass mypass -keystore mytest.jks -storepass mypass

Для локлаьного запуска redis: docker-compose up

Для установки redis manager
winget install qishibo.AnotherRedisDesktopManager

---
helm uninstall arch-auth
kubectl delete namespace arch-gur

kubectl port-forward -n arch-gur arch-auth-postgresql-deployment-0 5433:5432