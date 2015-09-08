package wh1

import (
	_ "github.com/lib/pq"
	"strconv"
)

type bundle struct {
	Id   int
	Name string
}

func (p *bundle) validate() string {
	if len(p.Name) < 3 {
		return const_validation_error_bundle_name_too_short
	}
	return ""
}

func (b *bundle) populate() string {
	db := <-dbpool
	r, err := db.Query("SELECT bundle_name FROM \"bundle\" WHERE id = ($1)", b.Id)
	dbpool <- db
	if err != nil {
		return err.Error()
	}
	if r.Next() {
		err = r.Scan(&b.Name)
		if err != nil {
			return err.Error()
		}
	}
	return ""
}

func (b *bundle) update() string {
	db := <-dbpool
	_, err := db.Exec("UPDATE \"bundle\" SET bundle_name = ($1) WHERE id = ($2)", b.Name, b.Id)
	dbpool <- db
	if err != nil {
		return err.Error()
	}
	return ""
}

func (p *bundle) store() string {
	db := <-dbpool
	r, err := db.Exec("INSERT INTO \"bundle\"(bundle_name) VALUES ($1)", p.Name)
	dbpool <- db
	if err != nil {
		return err.Error()
	}
	if n, _ := r.RowsAffected(); n < 1 {
		return "No rows inserted"
	}
	return ""
}

func getBundleList() ([]bundle, string) {
	db := <-dbpool
	r, err := db.Query("SELECT id, bundle_name FROM \"bundle\"")
	dbpool <- db
	if err != nil {
		return nil, err.Error()
	}
	var bs []bundle
	for r.Next() {
		b := bundle{}
		err = r.Scan(&b.Id, &b.Name)
		if err != nil {
			return nil, err.Error()
		}
		bs = append(bs, b)
	}
	return bs, ""
}

func (p *bundle) setId(id string) string {
	pid, err := strconv.Atoi(id)
	if err != nil {
		return "Bad id"
	}
	p.Id = pid
	return ""
}
