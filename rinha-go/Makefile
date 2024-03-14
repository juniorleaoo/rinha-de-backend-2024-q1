dbuild:
	docker build --platform=linux/amd64 -t rinha-go -f Dockerfile .

dtag:
	docker tag rinha-go:latest juniorleaoo/rinha-go:latest

dpush:
	docker push juniorleaoo/rinha-go:latest

dpublish: dbuild dtag dpush

build:
	docker compose build

run:
	docker compose up -d