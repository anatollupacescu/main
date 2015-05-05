package mr.odata;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;

import mr.dto.Person;
import mr.dto.PersonDTO;
import mr.dto.PersonRepository;
import mr.functional.MethodsContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

@RestController
class HelloController {

    private static final String NAME_MISSING_ERROR = "'name' query param expected";

    @Autowired
    private ProductRepository products;

    @Autowired
    private PersonRepository persons;

    private MethodsContext mc;

    @Autowired
    public void setUpContext(HelloMethods helloMethods) {
        mc = new MethodsContext(helloMethods);
    }

    @RequestMapping(value = "/persons", produces = "application/json", method = RequestMethod.GET)
    public PersonDTO getByName(@RequestParam String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException(NAME_MISSING_ERROR);
        }
        Person person = persons.findByName(name);
        if (person == null) {
            throw new EntityNotFoundException(String.format("Person with name '%s' has not been found", name));
        }
        return person;
    }

    @RequestMapping(value = "/persons", produces = "application/json", method = RequestMethod.POST)
    public String create(@RequestParam String name) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException(NAME_MISSING_ERROR);
        }
        Person p = new Person();
        p.setName(name);
        persons.save(p);
        return "ok";
    }

    @RequestMapping(value = "/rs/get", produces = "application/json")
    public Product get(@RequestParam String productName) {
        Product product = products.findByName(productName);
        if (product == null) {
            throw new IllegalArgumentException("Product name is mandatory");
        }
        return product;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/rs/saveSyblings", produces = "application/json")
    public String hello(@RequestParam String productId) throws JsonParseException, JsonMappingException, IOException {

        Preconditions.checkNotNull(productId);

        List<Product> syblingList = (List<Product>) mc.get("productSyblings", ImmutableMap.<String, Object> of("productId", productId));

        for (Product product : syblingList) {
            products.save(product);
        }

        return "ok";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    void handleBadRequests(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    void handleNotFoundException(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
