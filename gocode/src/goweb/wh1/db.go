package wh1

import (
	"database/sql"
	_ "github.com/bmizerany/pq"
)

func openConnection(connection_string string) (*sql.DB, error) {
	conn, err := sql.Open("postgres", const_db_connection_string)
	if err != nil {
		return nil, err
	}
	return conn, nil
}
