package wh1

import (
	"log"
	"net/http"
	"strconv"
)

func ProductAdd(w http.ResponseWriter, r *http.Request) {
	log.Printf("[ProductAdd] Entering method: %+v", r.Form)
	productName := r.FormValue("productName")
	log.Printf("[ProductAdd] product name: %s", productName)
	if p, err := newProduct(productName); err != "" {
		putError(err)
		viewErrorPage(w, r, const_url_product)
		return
	} else if exists, err := p.checkIfExists(); err != "" {
		putError(err)
		viewErrorPage(w, r, const_url_product)
		return
	} else if exists {
		putError(const_page_error_product_exists)
		viewErrorPage(w, r, const_url_product)
	} else if err = p.store(); err == "" {
		redirect(w, r, const_url_product)
	}
}

func ProductRemove(w http.ResponseWriter, r *http.Request) {
	id := r.FormValue("id")
	pid, err := strconv.Atoi(id)
	if err == nil {
		p := &product{Id: pid}
		if errStr := p.remove(); errStr == "" {
			redirect(w, r, const_url_product)
			return
		} else {
			putError("Could not remove: " + errStr)
		}
	} else {
		putError("Could not convert to integer: " + id)
	}
	viewErrorPage(w, r, const_url_product)
}

func ProductList(w http.ResponseWriter, r *http.Request) {
	var errorString string
	if errorCode := r.FormValue("e"); errorCode == "true" {
		errorString = getError()
	}
	page := newProductPage(errorString)
	viewPage(w, r, const_product_html_path, page)
}

func newProductPage(err string) *productPage {
	ps, e := getAllProducts()
	if e != "" {
		log.Fatal(e)
	}
	return &productPage{
		page:     page{Title: const_page_title_product, Message: err},
		Products: ps}
}
