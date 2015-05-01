package com.graph.controller;

import java.util.Date;
import java.util.Map;

import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.Neo4jTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.graph.model.Person;
import com.graph.service.ProfileViewService;

@RestController
public class PersonController {

    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private ProfileViewService pageViewService;

    @Autowired
    private Neo4jTemplate template;

    @RequestMapping(value = "/viewProfile", produces = "application/json")
    public Person viewProfile(@RequestParam(value = "id") Long id, @RequestParam(value = "visitorId") final Long visitorId) {

        Person targetProfile = findOne(id);
        log.debug("Found profile {}", targetProfile);

        if (targetProfile == null) {
            throw new RuntimeException("Profile not found");
        }

        Person visitor = findOne(visitorId);
        if (visitor != null && !targetProfile.equals(visitor)) {
            pageViewService.registerVisit(targetProfile, visitor);
        }

        return targetProfile;
    }

    @RequestMapping(value = "/getProfileViewers", produces = "application/json")
    public Map<Date, Person> getProfileViewers(@RequestParam(value = "id") Long ownerId) {
        log.debug("Looking up profile with id {}", ownerId);
        return pageViewService.getProfileVisitors(ownerId);
    }

    private Person findOne(long id) {
        Person targetProfile;
        try (Transaction tx = template.getGraphDatabase().beginTx()) {
            targetProfile = template.findOne(id, Person.class);
            tx.success();
        }
        return targetProfile;
    }
}
