package wh1

import "net/http"

type page struct {
	Title   string
	Message string
}

type productPage struct {
	page
	Products []product
}

func viewPage(w http.ResponseWriter, r *http.Request, file string, page interface{}) {
	err := templates.ExecuteTemplate(w, file, page)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}

func redirect(w http.ResponseWriter, r *http.Request, path string) {
	http.Redirect(w, r, path, http.StatusFound)
}

func viewErrorPage(w http.ResponseWriter, r *http.Request, path string) {
	http.Redirect(w, r, path+"?e=true", http.StatusFound)
}

type bundlePage struct {
	page
	//Bundle
	Name string
	Id   string
	//Names
	Bundles     []bundle
	Products    []product
	BundleItems []bundleItem
	//Form
	Action string
}

type bundleItemPage struct {
	page
}
