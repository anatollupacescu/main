package com.graph.test;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.graph.TestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=8081")
public class PersonControllerIntegrationTest {

    private RestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void health() {
		String entity = restTemplate.getForObject("http://localhost:8081/", String.class);
        org.junit.Assert.assertThat(entity, Matchers.equalTo("loaded"));
    }
}