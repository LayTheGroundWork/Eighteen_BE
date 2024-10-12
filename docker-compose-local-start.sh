# docker compose --env-file .env.local up -d 을 수행하는 스크립트 -- 서버는 뺀다.
docker compose --env-file .env.local up -d --scale server=0 --remove-orphans
