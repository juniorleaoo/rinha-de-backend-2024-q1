build-docker-compose:
	docker compose build
build-app:
	./gradlew clean build
unzip:
	unzip -o build/distributions/rinha-kotlin-http4k-0.0.1.zip -d build
perf:
	./load-test/executar-test-local.sh

build: build-app unzip build-docker-compose

build-app-native:
	./gradlew clean shadowJar

docker-build:
	docker build --platform=linux/amd64 -t rinha-http4k-native -f Dockerfile-native .

docker-tag:
	docker tag rinha-http4k-native:latest juniorleaoo/rinha-http4k-native:latest

docker-push:
	docker push juniorleaoo/rinha-http4k-native:latest

docker-publish: build-app-native docker-build docker-tag docker-push