package mr.odata;

import java.io.IOException;
import java.util.List;

import mr.odata.methods.MethodsContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

@RestController
class HelloController {

	@Autowired
	private ProductRepository products;

	private MethodsContext mc;

	@Autowired
	public void setUpContext(HelloMethods helloMethods) {
		mc = new MethodsContext(helloMethods);
	}

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

		Preconditions.checkNotNull(productId);

		mc.run(ImmutableMap.<String, Object> of("productId", productId));

		List<Product> syblingList = (List<Product>) mc.get("productSyblings");

		for (Product product : syblingList) {
			products.save(product);
		}

		return "ok";
	}
}
