package org.example.unittesting.mockito;

public class PersonService {

    private PersonDao dao;

    public PersonService(PersonDao dao) {
        this.dao = dao;
    }

    public Person add(String name) {
        return dao.insert(name);
    }

    public Person search(String name) {
        Person person = dao.select(name);
        Person personLike = dao.select("%" + name + "%");
        return person == null ? personLike : person;
    }
}
