package database

import (
	"context"
	"fmt"
	"rinha-go/model"
	"time"
)

func FindById(clientId int) (model.Client, error) {
	connection := Connection()
	var client model.Client
	err := connection.QueryRow(
		context.Background(),
		"SELECT id, saldo, limite FROM cliente WHERE id = $1",
		clientId,
	).Scan(&client.Id, &client.Balance, &client.Limit)
	if err != nil {
		fmt.Errorf("deu erro", err)
		return client, err
	}
	return client, nil
}

func CreateTransaction(clientId int, transaction model.Transaction) model.Client {
	connection := Connection()
	var client model.Client
	err := connection.QueryRow(
		context.Background(),
		"SELECT * FROM criar_transacao($1, $2, $3, $4)",
		clientId,
		transaction.Value,
		transaction.Description,
		transaction.Type,
	).Scan(&client.Balance, &client.Limit)
	if err != nil {
		fmt.Errorf("deu erro", err)
	}
	return client
}

func FindLast10Transactions(clientId int) []model.Transaction {
	connection := Connection()
	var transactions []model.Transaction
	rows, err := connection.Query(
		context.Background(),
		"SELECT * FROM transacao WHERE cliente_id = $1 ORDER BY realizada_em DESC LIMIT 10",
		clientId,
	)
	//).Scan(&transactions)
	if err != nil {
		fmt.Errorf("deu erro", err)
		transactions = make([]model.Transaction, 0)
	}

	for rows.Next() {
		values, _ := rows.Values()

		transactions = append(transactions, model.Transaction{
			Id:          values[0].(int32),
			Value:       values[1].(int32),
			Type:        values[2].(string),
			Description: values[3].(string),
			Date:        values[4].(time.Time),
		})
	}

	return transactions
}
