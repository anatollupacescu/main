package net.sandbox;

import java.util.Collection;

import com.google.gson.Gson;

public class SResponses {

	private final static Gson gson = new Gson();
	
	public static Object entity(Object input) {
		SingleEntityResponse obj = new SingleEntityResponse();
		obj.d = input;
		return gson.toJson(obj);
	}
	
	public static <T>Object entities(Collection<T> input) {
		MultiEntityResponse<T> obj = new MultiEntityResponse<T>();
		Results<T> results = new Results<T>();
		results.results = input;
		obj.d = results;
		return gson.toJson(obj);
	}
}

final class SingleEntityResponse {
	public Object d;
}

final class MultiEntityResponse<T> {
	public Results<T> d;
}

final class Results<T> {
	public Collection<T> results;
}