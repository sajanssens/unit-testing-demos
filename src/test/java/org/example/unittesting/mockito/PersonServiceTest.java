package org.example.unittesting.mockito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonDao daoMock;

    @InjectMocks
    private PersonService sut;

    // Create, use, verify example
    @Test
    void whenAddByNameThenInsertIsCalledOnce() {
        // Create, see above

        // Use
        sut.add("Bram");

        // Verify
        verify(daoMock).insert(anyString());
    }

    // Verifying number of invocations
    @Test
    void whenSearchByNameThenSelectIsCalledTwiceAndInsertNever() {
        sut.search("Bram");

        // Verify
        verify(daoMock, times(2)).select(anyString());
        verify(daoMock, never()).insert(anyString());
    }

    // Verifying order for single mock
    @Test
    void whenSearchByNameThenSelectIsCalledTwiceInCorrectOrder() {
        sut.search("Bram");

        InOrder daoMockInOrder = inOrder(daoMock);

        // Verify
        daoMockInOrder.verify(daoMock).select("Bram");
        daoMockInOrder.verify(daoMock).select("%Bram%");
    }

    @Test
    void whenAddByNameThenPersonIsReturned() {
        when(daoMock.insert(anyString())).thenReturn(new Person(1, "Bram"));

        Person actual = sut.add("Bram");

        assertEquals(1, actual.id());
        assertEquals("Bram", actual.name());
    }
}
