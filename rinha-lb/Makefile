build-app:
	./gradlew clean shadowJar

docker-build:
	docker build --platform=linux/amd64 -t rinha-lbk -f Dockerfile .

docker-tag:
	docker tag rinha-lbk:latest juniorleaoo/rinha-lbk:latest

docker-push:
	docker push juniorleaoo/rinha-lbk:latest

build: build-app docker-build

push: build docker-tag docker-push
