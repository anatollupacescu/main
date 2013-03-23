package wh1

import "net/http"

const (
	BUNDLES_PAGE       = "bundle.html"
	BUNDLE_CREATE_PAGE = "bundle_item.html"
	BUNDLE_PAGE_TITLE  = "Retete"
	FORM_ACTION_UPDATE = "Update"
	FORM_ACTION_SAVE   = "Save"
)

func BundleList(w http.ResponseWriter, r *http.Request) {
	page := newBundlePage("")
	bs, err := getBundleList()
	if err == "" {
		page.Bundles = bs
		page.Action = "Save"
		viewBundlesPage(w, r, page)
		return
	}
	putError(err)
	viewErrorPage(w, r, const_url_bundle)
}

func BundleSave(w http.ResponseWriter, r *http.Request) {
	b := &bundle{Name: r.FormValue("name")}
	var err = b.validate()
	if err == "" {
		switch r.FormValue("act") {
		case FORM_ACTION_UPDATE:
			err := b.setId(r.FormValue("id"))
			if err == "" {
				err = b.update()
				if err == "" {
					http.Redirect(w, r, const_url_bundle, http.StatusFound)
					return
				}
			}
		case FORM_ACTION_SAVE:
			err := b.store()
			if err == "" {
				http.Redirect(w, r, const_url_bundle, http.StatusFound)
				return
			}
		default:
			err = "Action is nil or could not be executed"
		}
	}
	putError(err)
	viewErrorPage(w, r, const_url_bundle)
}

func BundleEdit(w http.ResponseWriter, r *http.Request) {
	page := newBundlePage("")
	page.Action = "Update"
	page.Id = r.FormValue("id")
	b := &bundle{}
	err := b.setId(page.Id)
	if err == "" {
		err = b.populate()
		if err == "" {
			page.Name = b.Name
			viewBundlesPage(w, r, page)
			return
		}
	}
	putError(err)
	viewErrorPage(w, r, const_url_bundle)
}

func newBundlePage(err string) (b *bundlePage) {
	return &bundlePage{page: page{Title: BUNDLE_PAGE_TITLE, Message: err}}
}

func viewBundlesPage(w http.ResponseWriter, r *http.Request, page *bundlePage) {
	err := templates.ExecuteTemplate(w, BUNDLES_PAGE, page)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
