package main

import (
	"encoding/json"
	"fmt"
	routing "github.com/qiangxue/fasthttp-routing"
	"github.com/valyala/fasthttp"
	"os"
	"rinha-go/database"
	"rinha-go/model"
	"strconv"
	"time"
)

type TransactionRequest struct {
	Value       int32  `json:"valor"`
	Type        string `json:"tipo"`
	Description string `json:"descricao"`
}

type TransactionResponse struct {
	Limit   int32 `json:"limite"`
	Balance int32 `json:"saldo"`
}

type ExtractResponse struct {
	Balance          BalanceResponse     `json:"saldo"`
	LastTransactions []model.Transaction `json:"ultimas_transacoes"`
}

type BalanceResponse struct {
	Total int32     `json:"total"`
	Date  time.Time `json:"data_extrato"`
	Limit int32     `json:"limite"`
}

func (req *TransactionRequest) IsInvalid() bool {
	return len(req.Description) == 0 || len(req.Description) > 10 || (req.Type != "c" && req.Type != "d") || req.Value <= 0
}

func transacaoHandler(ctx *routing.Context) error {
	clientId, _ := strconv.Atoi(ctx.Param("id"))
	if clientId < 1 || clientId > 5 {
		ctx.SetStatusCode(fasthttp.StatusNotFound)
		return nil
	}

	var transactionRequest TransactionRequest
	_ = json.Unmarshal(ctx.PostBody(), &transactionRequest)

	if transactionRequest.IsInvalid() {
		ctx.SetStatusCode(fasthttp.StatusUnprocessableEntity)
		return nil
	}

	var client = database.CreateTransaction(clientId, model.Transaction{
		Value:       transactionRequest.Value,
		Description: transactionRequest.Description,
		Type:        transactionRequest.Type,
		Date:        time.Now(),
	})

	ctx.SetStatusCode(fasthttp.StatusOK)
	body, _ := json.Marshal(TransactionResponse{
		Limit:   client.Limit,
		Balance: client.Balance,
	})
	ctx.SetBody(body)
	return nil
}

func extratoHandler(ctx *routing.Context) error {
	clienteId, _ := strconv.Atoi(ctx.Param("id"))
	if clienteId < 1 || clienteId > 5 {
		ctx.SetStatusCode(fasthttp.StatusNotFound)
		return nil
	}

	client, err := database.FindById(clienteId)
	if err != nil {
		fmt.Println(err)
		ctx.SetStatusCode(fasthttp.StatusNotFound)
		return nil
	}
	transactions := database.FindLast10Transactions(clienteId)

	ctx.SetStatusCode(fasthttp.StatusOK)
	ctx.SetContentType("application/json")
	body, _ := json.Marshal(ExtractResponse{
		Balance: BalanceResponse{
			Total: client.Balance,
			Date:  time.Now(),
			Limit: client.Limit,
		},
		LastTransactions: transactions,
	})
	ctx.SetBody(body)
	return nil
}

func main() {
	database.Connect()

	router := routing.New()

	router.Post("/clientes/<id>/transacoes", transacaoHandler)
	router.Get("/clientes/<id>/extrato", extratoHandler)

	port := os.Getenv("SERVER_PORT")
	err := fasthttp.ListenAndServe(":"+port, router.HandleRequest)
	if err != nil {
		_ = fmt.Errorf("deu ruim", err)
		return
	}
}
