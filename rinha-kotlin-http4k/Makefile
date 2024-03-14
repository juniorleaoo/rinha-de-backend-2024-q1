perf:
	./load-test/executar-test-local.sh

app-build:
	./gradlew clean shadowJar

dbuild:
	docker build --platform=linux/amd64 -t rinha-http4k-native -f Dockerfile-native .

dtag:
	docker tag rinha-http4k-native:latest juniorleaoo/rinha-http4k-native:latest

dpush:
	docker push juniorleaoo/rinha-http4k-native:latest

docker-publish: app-build dbuild dtag dpush

dcbuild:
	docker compose -f docker-compose-native.yml build

build: app-build dcbuild

run:
	docker compose -f docker-compose-native.yml up -d