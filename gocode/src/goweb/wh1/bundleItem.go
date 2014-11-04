package wh1

import (
	_ "github.com/bmizerany/pq"
	"strconv"
)

type bundleItem struct {
	Id int
	//id
	Pid         int
	ProductName string
	//bundle
	Bid int
	Qty int
}

func newBundleItem(pid string, bid string, qty string) (*bundleItem, string) {
	b := &bundleItem{}
	var err error
	b.Pid, err = strconv.Atoi(pid)
	if err == nil {
		b.Bid, err = strconv.Atoi(bid)
		if err == nil {
			b.Qty, err = strconv.Atoi(qty)
			if err == nil {
				return b, ""
			}
		}
	}
	return nil, err.Error()
}

func (b *bundleItem) remove() string {
	db := <-dbpool
	_, err := db.Exec("DELETE FROM \"bundle_item\" WHERE product_id = ($1) and bundle_id = ($2)", b.Pid, b.Bid)
	dbpool <- db
	if err != nil {
		return err.Error()
	}
	return ""
}

func (b *bundleItem) store() string {
	db := <-dbpool
	_, err := db.Exec("INSERT INTO \"bundle_item\" (product_id, bundle_id, qty) VALUES($1, $2, $3)", b.Pid, b.Bid, b.Qty)
	dbpool <- db
	if err != nil {
		return err.Error()
	}
	return ""
}

func getAllBundleItems(bid int) ([]bundleItem, string) {
	db := <-dbpool
	sql := `SELECT p.product_name, b.qty, b.product_id, b.bundle_id 
		FROM bundle_item b, product p WHERE b.bundle_id = ($1) AND b.product_id = p.id`
	r, err := db.Query(sql, bid)
	dbpool <- db
	if err != nil {
		return nil, err.Error()
	}
	var bs []bundleItem
	for r.Next() {
		b := bundleItem{}
		err = r.Scan(&b.ProductName, &b.Qty, &b.Pid, &b.Bid)
		if err != nil {
			return nil, err.Error()
		}
		bs = append(bs, b)
	}
	return bs, ""
}

func getAllProductsForBundleItem(bid int) ([]product, string) {
	sql := `SELECT p.id, p.product_name FROM product p 
			WHERE p.id NOT IN (SELECT product_id FROM bundle_item WHERE bundle_id = ($1))`
	db := <-dbpool
	r, err := db.Query(sql, bid)
	dbpool <- db
	var ps []product
	for r.Next() {
		p := product{}
		err = r.Scan(&p.Id, &p.Name)
		if err != nil {
			return nil, err.Error()
		}
		ps = append(ps, p)
	}
	return ps, ""
}
