package wh1

import (
	"log"
)

const (
	error_key = "error"
)

var store = map[string]string{}

func putError(value string) {
	put(error_key, value)
}

func getError() string {
	return get(error_key)
}

func put(key string, value string) {
	log.Printf("Saving to store %s - %s", key, value)
	store[key] = value
}

func get(key string) string {
	v := store[key]
	log.Printf("Extracted value for key %s %s", key, v)
	delete(store, key)
	return v
}
