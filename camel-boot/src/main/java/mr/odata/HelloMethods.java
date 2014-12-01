package mr.odata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import mr.functional.FuncAnnotation;

public class HelloMethods {

	@Autowired
	private Logger log;

	private String uri = "http://services.odata.org/V4/OData/OData.svc/";

	@Autowired
	private RestTemplate restTemplate;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@FuncAnnotation(name = "productCategoryID", mappedVars = { "productId" })
	private Integer getProductCategoryId(String productId) {
		String categoriesUrl = uri + String.format("Products(%s)/Categories", productId);
		String categoriesJson = restTemplate.getForObject(categoriesUrl, String.class);
		HashMap<String, Object> result;
		try {
			result = OBJECT_MAPPER.readValue(categoriesJson, HashMap.class);
		} catch (IOException e) {
			log.error("Could not map categories json to map", e);
			return null;
		}
		ArrayList<HashMap<String, Object>> categoryList = (ArrayList<HashMap<String, Object>>) result.get("value");
		if (categoryList.iterator().hasNext()) {
			return (Integer) categoryList.iterator().next().get("ID");
		}
		return null;
	}

	@FuncAnnotation(name = "productSyblings", mappedVars = { "productCategoryID" })
	private List<Product> getProductSyblings(Integer categoryId) {
		String categoryProducts = uri + String.format("Categories(%s)/Products", categoryId);
		String products = restTemplate.getForObject(categoryProducts, String.class);
		HashMap<String, Object> productsMap;
		try {
			productsMap = OBJECT_MAPPER.readValue(products, HashMap.class);
		} catch (IOException e) {
			log.error("Could not map category products json to map", e);
			return null;
		}
		ArrayList<HashMap<String, Object>> syblingList = (ArrayList<HashMap<String, Object>>) productsMap.get("value");
		List<Product> syblingProductList = new ArrayList<Product>();
		for (HashMap<String, Object> sybling : syblingList) {
			Integer otherId = (Integer) sybling.get("ID");
			log.debug(otherId.toString());
			String otherProductUrl = uri + String.format("Products(%s)", otherId);
			String syblingJson = restTemplate.getForObject(otherProductUrl, String.class);
			try {
				HashMap productMap = OBJECT_MAPPER.readValue(syblingJson, HashMap.class);
				Product p = new Product();
				p.setName((String) productMap.get("Name"));
				Integer id = (Integer) productMap.get("ID");
				p.setId(Long.valueOf(id));
				syblingProductList.add(p);
			} catch (IOException e) {
				log.error("Could not map single product json to map", e);
			}
		}
		return syblingProductList;
	}
}
