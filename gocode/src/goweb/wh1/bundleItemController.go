package wh1

import (
	"net/http"
)

const (
	BUNDLE_ITEM_PAGE       = "bundle_item.html"
	BUNDLE_ITEM_PAGE_TITLE = "Produsele retetei"
	BUNDLE_ERR_BAD_ID      = "Id-ul trebuie sa fie un numbar intreg"
)

func BundleItemView(w http.ResponseWriter, r *http.Request) {
	page := newBundleItemPage("")
	page.Id = r.FormValue("id")
	b := &bundle{}
	err := b.setId(page.Id)
	if err == "" {
		ps, err := getAllProductsForBundleItem(b.Id)
		if err == "" {
			err = b.populate()
			if err == "" {
				page.Name = b.Name
				page.Products = ps
				page.BundleItems, err = getAllBundleItems(b.Id)
				if err == "" {
					viewBundleItemPage(w, r, page)
					return
				}
			}
		}
	}
	putError(err)
	viewErrorPage(w, r, const_url_bundle_item)
}

func BundleItemSave(w http.ResponseWriter, r *http.Request) {
	pid := r.FormValue("pid")
	bid := r.FormValue("bid")
	qty := r.FormValue("qty")
	p, err := newBundleItem(pid, bid, qty)
	if err == "" {
		err = p.store()
		if err == "" {
			http.Redirect(w, r, const_url_bundle_item+"?id="+bid, http.StatusFound)
			return
		}
	}
	putError(err)
	viewErrorPage(w, r, err)
}

func BundleItemRemove(w http.ResponseWriter, r *http.Request) {
	pid := r.FormValue("pid")
	bid := r.FormValue("bid")
	b, err := newBundleItem(pid, bid, "0")
	if err == "" {
		err = b.remove()
		if err == "" {
			http.Redirect(w, r, const_url_bundle_item+"?id="+bid, http.StatusFound)
			return
		}
	}
	putError(err)
	viewErrorPage(w, r, err)
}

func newBundleItemPage(err string) (b *bundlePage) {
	return &bundlePage{page: page{Title: BUNDLE_ITEM_PAGE_TITLE, Message: err}}
}

func viewBundleItemPage(w http.ResponseWriter, r *http.Request, page *bundlePage) {
	err := templates.ExecuteTemplate(w, BUNDLE_ITEM_PAGE, page)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
