package org.example.unittesting.mockito;

public interface PersonDao {
    Person insert(String name);

    Person select(String name);

    void remove(int id);
}
