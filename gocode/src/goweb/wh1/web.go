package wh1

import (
	"database/sql"
	"html/template"
	"log"
	"net/http"
	"os"
	"path/filepath"
)

var pattern = filepath.Join("bwhweb", "*.html")
var templates = template.Must(template.ParseGlob(pattern))
var dbpool chan *sql.DB

func Serve() {
	db, err := openConnection(const_db_connection_string)
	if err != nil {
		panic(err.Error())
	}
	dbpool = make(chan *sql.DB, 1)
	dbpool <- db
	http.HandleFunc("/product", ProductList)
	http.HandleFunc("/product/add", ProductAdd)
	http.HandleFunc("/product/remove", ProductRemove)
	http.HandleFunc("/bundle", BundleList)
	http.HandleFunc("/bundle/save", BundleSave)
	http.HandleFunc("/bundle/edit", BundleEdit)
	dir, _ := os.Getwd()
	http.Handle("/css/", http.StripPrefix("/css/", http.FileServer(http.Dir(dir+"/css"))))
	log.Fatal(http.ListenAndServe(":8080", nil))
}
