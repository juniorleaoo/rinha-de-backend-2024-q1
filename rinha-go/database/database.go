package database

import (
	"context"
	"fmt"
	"github.com/jackc/pgx/v5/pgxpool"
	"os"
	"strconv"
)

var conn *pgxpool.Pool

func Connect() {
	databaseURL := os.Getenv("DATABASE_URL")

	poolConfig, err := pgxpool.ParseConfig(databaseURL)
	if err != nil {
		fmt.Println(err)
	}
	maxCoons, _ := strconv.Atoi(os.Getenv("MAX_CONNECTION_POOL_SIZE"))
	poolConfig.MaxConns = int32(maxCoons)

	dbpool, err := pgxpool.NewWithConfig(context.Background(), poolConfig)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Unable to create connection pool: %v\n", err)
		os.Exit(1)
	}

	conn = dbpool
}

func Connection() *pgxpool.Pool {
	return conn
}
