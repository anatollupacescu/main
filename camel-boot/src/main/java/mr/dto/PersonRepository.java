package mr.dto;

import javax.transaction.Transactional;

import org.springframework.data.repository.Repository;

public interface PersonRepository extends Repository<Person, Integer> {

    public Person findByName(String name);

    @Transactional
    Person save(Person bean);
}
