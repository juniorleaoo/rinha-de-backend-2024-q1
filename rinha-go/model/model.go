package model

import "time"

type Client struct {
	Id      int32
	Limit   int32
	Balance int32
}

type Transaction struct {
	Id          int32     `json:"-"`
	Value       int32     `json:"valor"`
	Type        string    `json:"tipo"`
	Description string    `json:"descricao"`
	Date        time.Time `json:"realizada_em"`
}
