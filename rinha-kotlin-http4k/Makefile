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

build-docker-native:
	docker build --platform=linux/amd64 -t rinha-http4k-native -f Dockerfile-native .

run-native:
	docker run --platform=linux/amd64 -p 8080:8080 rinha-http4k-native

build-native: build-app-native build-docker-native