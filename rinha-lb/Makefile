app-build:
	./gradlew clean shadowJar

publish:
	make app-build
	docker build --platform=linux/amd64 -t rinha-lbk -f Dockerfile .
	docker tag rinha-lbk:latest juniorleaoo/rinha-lbk:latest
	docker push juniorleaoo/rinha-lbk:latest

dbuild:
	docker compose build --no-cache

run:
	docker compose up -d