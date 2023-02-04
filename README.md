# PepperoniBot
## A personal Discord bot for managing my Discord server.
### Only includes an order system for now.
## Dependencies:
- JDA (discord api)
- MongoDB Driver
- Morphia (ORM for MongoDB)
- Dotenv (for loading environment variables)
## Deployment with Docker:
### Simply run the following commands:
```bash
cd docker
docker compose up -d
```
### This will build the project and run it in a Docker container with a MongoDB instance.