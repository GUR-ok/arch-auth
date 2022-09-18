Микросервис авторизации

Сборка и установка в minikube
1) `gradle build`
2) `docker build -t gurok/arch_auth:project .`
3) `docker push gurok/arch_auth:project`
4) `kubectl create namespace arch-gur`
5) `helm install arch-auth ./deployment/app/`
   `kubectl get pods -n arch-gur`
6) `helm install gorelov-redis ./deployment/redis/`

---

Для генерации jwk:
`keytool -genkeypair -alias mytest -keyalg RSA -keypass mypass -keystore mytest.jks -storepass mypass`

Для локального запуска redis: `docker-compose up`

Для установки redis manager
`winget install qishibo.AnotherRedisDesktopManager`

`kubectl port-forward -n arch-gur arch-auth-postgresql-deployment-0 5433:5432`

---
### Очистка пространства:

- `helm uninstall arch-auth`
- `helm uninstall gorelov-redis`
- `kubectl delete namespace arch-gur`
