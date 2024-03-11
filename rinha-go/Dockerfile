FROM golang:1.22.1-alpine3.19 AS build

WORKDIR /app
COPY . /app
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o api main.go

FROM scratch
WORKDIR /app
COPY --from=build /app/api ./
CMD [ "./api" ]