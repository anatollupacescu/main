package com.graph.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.conversion.Result;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.graph.model.Person;

@Service
public class ProfileViewService {

    private static final Logger log = LoggerFactory.getLogger(ProfileViewService.class);

    private static final String query = "START n=node({id}) MATCH (n: Person) <- [r:VISITED] -> (v) WHERE r.visitDate < {visitDate} RETURN r,v LIMIT 10";

    @Autowired
    private Neo4jOperations template;

    public void registerVisit(Person profileOwner, Person profileVisitor) {
        log.debug("Person {} visited {}", profileVisitor, profileOwner);
        try (Transaction tx = template.getGraphDatabase().beginTx()) {
            Node n1 = template.getNode(profileOwner.getId());
            Node n2 = template.getNode(profileVisitor.getId());
            Relationship saved = template.createRelationshipBetween(n1, n2, "VISITED", ImmutableMap.of("visitDate", (new Date().getTime())));
            log.debug("ProfileView saved: {}", saved);
            tx.success();
        }
    }

    public Map<Date, Person> getProfileVisitors(long ownerId) {
        final Map<Date, Person> visitorsToReturn = new HashMap<Date, Person>();
        final Map<String, Object> paramMap = ImmutableMap.of("id", new Long(ownerId), "visitDate", (new Date()).getTime());
        try (Transaction tx = template.getGraphDatabase().beginTx()) {
            Result<Map<String, Object>> result = template.query(query, paramMap);
            result.forEach(new Consumer<Map<String, Object>>() {
                public void accept(Map<String, Object> visitorMap) {
                    log.debug("Found visitor");
                    Node visitorNode = (Node) visitorMap.get("v");
                    Relationship relationship = (Relationship) visitorMap.get("r");

                    Long vd = (Long) relationship.getProperty("visitDate");
                    Date visitDate = new Date(vd);

                    String name = (String) visitorNode.getProperty("name");
                    Person visitor = new Person(name);

                    visitorsToReturn.put(visitDate, visitor);
                }
            });
        }
        return visitorsToReturn;
    }
}
