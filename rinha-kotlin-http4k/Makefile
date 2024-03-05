build-docker-compose:
	docker compose build
build-app:
	./gradlew clean build
unzip:
	unzip -o build/distributions/rinha-kotlin-http4k-0.0.1.zip -d build
perf:
	./load-test/executar-test-local.sh

build: build-app unzip build-docker-compose