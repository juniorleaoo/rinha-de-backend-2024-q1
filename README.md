# Rinha de Backend 2024 - Q1

Implementações da [Rinha de Backend 2024/Q1](https://github.com/zanfranceschi/rinha-de-backend-2024-q1)

---

## 1. Rinha Kotlin

O projeto [rinha-kotlin-http4k](./rinha-kotlin-http4k) implementa uma API em Kotlin usando o framework HTTP4k. 
Ele utiliza PostgreSQL como banco de dados, Hikari para o pool de conexões, Nginx para roteamento de solicitações,
e é compilado com GraalVM.

### Stack
- Kotlin
- HTTP4k
- PostgreSQL
- Hikari
- GraalVM

### Como Executar

```shell
make build
```
```shell
make run
```

---

## 2. Rinha GO

O [rinha-go](./rinha-go) implementa as mesmas especifícações da rinha, mas usando GO

### Stack
- GO
- PostgreSQL
- PGX

### Como Executar

```shell
make build
```
```shell
make run
```

---

## 3. Rinha LB (Load Balancer)

O [rinha-lb](./rinha-lb) é um load balancer extremamente minimalista escrito em Kotlin. Ele utiliza sockets para gerenciar as conexões e foi compilado para nativo usando GraalVM.

### Tecnologias Principais
- Kotlin
- Sockets
- GraalVM

### Como Executar
```shell
make dbuild
```
```shell
make run
```

## Licença
Este projeto está licenciado sob a [MIT License](LICENSE).
