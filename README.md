# 🛡️ Tokiomarine - Sistema Fullstack com Spring Boot e Angular

Este projeto é composto por:

- 🔙 **Backend:** Spring Boot + PostgreSQL
- 🌐 **Frontend:** Angular
- 🐳 **Ambiente:** Docker e Docker Compose

---

## 🚀 Como rodar o projeto com Docker Compose

> Requisitos: [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/) instalados.

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/tokiomarine.git
cd tokiomarine
```
2.Execute o projeto com um único comando:
 ```bash
docker-compose up --build
```

3.Acesse os serviços:

🧠 Frontend (Angular via Nginx): http://localhost:4200

📦 Backend (API Spring Boot): http://localhost:8080

📄 Swagger da API: http://localhost:8080/swagger-ui/index.html

🐘 PostgreSQL: Porta 5432 (usuário: docker, senha: docker)

⚙️ Rodando o projeto manualmente (sem Docker)
1. Banco de Dados
Instale o PostgreSQL

Crie um banco chamado tokiomarine com usuário docker e senha docker

2. Backend (Spring Boot)
Requisitos: Java 17+ e Maven

 ```bash
cd api
./mvnw clean package -DskipTests
java -jar target/*.jar
```
A API será executada em: http://localhost:8080

🔍 Swagger:
http://localhost:8080/swagger-ui/index.html

3. Frontend (Angular)
Requisitos: Node.js 18+ e Angular CLI

 ```bash
cd frontend
npm install
ng serve
```

A aplicação estará em: http://localhost:4200
📁 Estrutura do Projeto

```bash
/backend         # Projeto Spring Boot
/frontend        # Projeto Angular
/docker-compose.yml
```
🐞 Variáveis e Configurações
Backend - application.properties

```bash
spring.datasource.url=jdbc:postgresql://db:5432/tokiomarine
spring.datasource.username=docker
spring.datasource.password=docker
api.security.token.secret=tokiomarine-test
external.service.viacep.baseUrl=https://viacep.com.br/ws/
```

```bash
Frontend - environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

✨ Observações
- esse projeto tem um user admin criado no seeds quando o projeto é inicializado
- email: admin@mail.com e senha: Admin@123

📫 Contato
Dúvidas ou sugestões? Fique à vontade para abrir uma issue ou pull request.
