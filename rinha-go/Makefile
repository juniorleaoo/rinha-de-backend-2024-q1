build:
	docker build --platform=linux/amd64 -t rinha-go -f Dockerfile .

docker-tag:
	docker tag rinha-go:latest juniorleaoo/rinha-go:latest

docker-push:
	docker push juniorleaoo/rinha-go:latest

push: build docker-tag docker-push
