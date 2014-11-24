package mr.odata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import mr.functional.FuncAnnotation;
import mr.functional.FuncContext;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
class HelloController {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Autowired
	private Logger log;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ProductRepository products;

	private String uri = "http://services.odata.org/V4/OData/OData.svc/";

	private final FuncContext context;

	public HelloController() {
		context = FuncContext.<HelloController> build(this);
	}

	@FuncAnnotation
	private final Function<FuncContext, Object> productCategoryID = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			String productId = t.get("productId", String.class);
			if (productId == null) {
				log.debug("Product id not found");
				return null;
			}
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
	};

	@FuncAnnotation
	private final Function<FuncContext, Object> productSyblings = new Function<FuncContext, Object>() {
		@Override
		public Object apply(FuncContext t) {
			Integer categoryId = t.get("productCategoryID", Integer.class);
			if (categoryId == null) {
				return null;
			}
			String categoryProducts = uri + String.format("Categories(%s)/Products", categoryId);
			String products = restTemplate.getForObject(categoryProducts, String.class);
			HashMap<String, Object> productsMap;
			try {
				productsMap = OBJECT_MAPPER.readValue(products, HashMap.class);
			} catch (IOException e) {
				log.error("Could not map category products json to map", e);
				return null;
			}
			ArrayList<HashMap<String, Object>> syblingList = (ArrayList<HashMap<String, Object>>) productsMap
					.get("value");
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
	};

	@RequestMapping(value = "/rs/get", produces = "application/json")
	public String get(@RequestParam String productName) {
		Product product = products.findByName(productName);
		if (product == null) {
			return "{}";
		}
		return String.format("{ 'id':'%s', 'name':'%s'}", product.getId(), product.getName());
	}

	@RequestMapping(value = "/rs/saveSyblings", produces = "application/json")
	public String hello(@RequestParam String productId) throws JsonParseException, JsonMappingException, IOException {

		context.run("productId", productId, "products", products, "productCategoryId");

		List<Product> syblingList = (List<Product>) context.get("productSyblings");

		for (Product product : syblingList) {
			products.save(product);
		}
		return OBJECT_MAPPER.writeValueAsString(syblingList);
	}
}
