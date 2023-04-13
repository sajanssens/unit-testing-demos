package org.example.unittesting.mockito;

public class PersonService {

    private final PersonDao dao;

    public PersonService(PersonDao dao) {
        this.dao = dao;
    }

    public Person add(String name) {
        try {
            return dao.insert(name);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid name: " + name, e);
        }
    }

    public Person search(String name) {
        Person person = dao.select(name);
        Person personLike = dao.select("%" + name + "%");
        return person == null ? personLike : person;
    }

    public void remove(int id) {
        try {
            dao.remove(id + 100);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid id: " + id, e);
        }

    }
}
