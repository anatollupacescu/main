package com.graph.test.controller;

import java.util.Arrays;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.graph.TestApplication;
import com.graph.model.Person;
import com.graph.repository.PersonRepository;
import com.jayway.restassured.RestAssured;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class PersonControllerTest {

    @Autowired
    PersonRepository repository;

    Person mickey;
    Person minnie;
    Person pluto;

    @Value("${local.server.port}")
    int port;

    @Before
    public void setUp() {
        mickey = new Person("Mickey Mouse");
        minnie = new Person("Minnie Mouse");
        pluto = new Person("Pluto");

        repository.deleteAll();
        repository.save(Arrays.asList(mickey, minnie, pluto));

        RestAssured.port = port;
    }

    @Test
    public void canFetchPluto() {
        Long mickeyId = mickey.getId();
        Long plutoId = pluto.getId();
        RestAssured.when()
            .get("/viewProfile?id={id}&visitorId={vid}", mickeyId, plutoId)
            .then().statusCode(200)
            .body("name", Matchers.is("Mickey Mouse"))
            .body("id", Matchers.is(mickeyId.intValue()));

        RestAssured.when()
            .get("/getProfileViewers?id={id}", mickeyId)
            .then().statusCode(HttpStatus.OK.value())
            .content(Matchers.containsString("Pluto"));
    }

}
