package com.graph.repository;

import org.springframework.data.neo4j.repository.GraphRepository;

import com.graph.model.Person;


public interface PersonRepository extends GraphRepository<Person> {}
