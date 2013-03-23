package wh1

import (
	_ "github.com/bmizerany/pq"
	"log"
)

type product struct {
	Id   int
	Name string
}

func (p *product) validate() string {
	log.Printf("[validate] Entering method %+v", p)
	if p == nil || p.Name == "" {
		log.Printf("[validate] Error encountered %s; leaving method", const_field_error_min_len)
		return const_field_error_min_len
	}
	log.Printf("[validate] Leaving method")
	return ""
}

func (p *product) remove() string {
	sql := "DELETE FROM product WHERE id = ($1)"
	db := <-dbpool
	_, err := db.Exec(sql, p.Id)
	dbpool <- db
	if err != nil {
		return err.Error()
	}
	return ""
}

func (p *product) store() string {
	sql := "INSERT INTO product(product_name) VALUES ($1)"
	db := <-dbpool
	_, err := db.Exec(sql, p.Name)
	dbpool <- db
	log.Printf("[store] error: %+v", err)
	if err != nil {
		return err.Error()
	}
	return ""
}

func (p *product) checkIfExists() (bool, string) {
	log.Printf("[checkIfExists] Entering method %+v", p)
	if p.Name == "" {
		log.Printf("[checkIfExists] Error: %s", const_validation_error_product_name_empty)
		return false, const_validation_error_product_name_empty
	}
	sql := "SELECT id FROM product WHERE product_name = ($1)"
	log.Printf("[checkIfExists] Executing query %s", sql)
	db := <-dbpool
	r, err := db.Query(sql, p.Name)
	dbpool <- db
	if err == nil {
		if r.Next() {
			log.Printf("[checkIfExists] Product exists")
			return true, ""
		}
		log.Printf("[checkIfExists] Leaving method")
		return false, ""
	}
	log.Printf("[checkIfExists] Leaving method with error: %s", err)
	return false, err.Error()
}

func getAllProducts() ([]product, string) {
	sql := "SELECT id, product_name FROM product"
	db := <-dbpool
	r, err := db.Query(sql)
	dbpool <- db
	if err != nil {
		return nil, err.Error()
	}
	var m = []product{}
	for r.Next() {
		p := product{}
		err = r.Scan(&p.Id, &p.Name)
		if err != nil {
			return nil, err.Error()
		}
		m = append(m, p)
	}
	return m, ""
}

func newProduct(name string) (*product, string) {
	log.Printf("[newProduct] Entering method; product name: %s", name)
	p := &product{Name: name}
	err := p.validate()
	log.Printf("[newProduct] leaving method with error: %+v", err)
	return p, err
}
