package org.example.unittesting.mockito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
        verify(daoMock).insert("Bram");
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

    // Configure the mock: thenReturn
    @Test
    void whenAddByNameThenPersonIsReturned() {
        when(daoMock.insert(eq("Bram"))).thenReturn(new Person(1, "Bram"));

        Person actual = sut.add("Bram");

        assertEquals(1, actual.id());
        assertEquals("Bram", actual.name());
    }

    // Configure the mock: thenThrow
    @Test
    void whenAddByInvalidNameThenBadRequestExceptionIsThrown() {
        when(daoMock.insert("Vladimir")).thenThrow(IllegalArgumentException.class);

        BadRequestException e = assertThrows(BadRequestException.class, () -> sut.add("Vladimir"));
        assertEquals("Invalid name: Vladimir", e.getMessage());
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    // Argument matchers
    @Test
    void whenAddBySomeNameThenPersonIsReturned() {
        when(daoMock.insert(anyString())).thenReturn(new Person(1, "I don't care"));

        Person actual1 = sut.add("Bram");
        Person actual2 = sut.add("Mieke");

        assertEquals("I don't care", actual1.name());
        assertEquals("I don't care", actual2.name());

        verify(daoMock, atLeastOnce()).insert(anyString());
    }

    // Stubbing void methods: doNothing
    @Test
    void whenRemoveByIdTheRemoveIsCalledWithIdPlus100() {
        doNothing().when(daoMock).remove(anyInt());

        sut.remove(1);

        verify(daoMock).remove(eq(101));
    }

    // Stubbing void methods: doAnswer
    @Test
    void whenRemoveByIdIsCalled100TimesThenRemoveIsCalled100Times() {
        AtomicInteger counter = new AtomicInteger(0);
        doAnswer(i -> counter.getAndIncrement()).when(daoMock).remove(anyInt());

        for (int i = 0; i < 100; i++) sut.remove(i);

        assertEquals(100, counter.get());
    }

    // Stubbing void methods: doThrow
    @Test
    void whenRemoveWithIllegalIdIsCalledBadRequestExceptionIsThrown() {
        doThrow(IllegalArgumentException.class).when(daoMock).remove(lt(100));

        BadRequestException e = assertThrows(BadRequestException.class, () -> sut.remove(-42));
        assertEquals("Invalid id: -42", e.getMessage());
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }

    // Stubbing consecutive calls
    @Test
    void whenInsertIsCalledMoreThanOnceItReturnsDifferentPersons() {
        when(daoMock.insert(anyString()))
                .thenReturn(new Person(1, "Bram"))
                .thenReturn(new Person(2, "Mieke"))
                .thenReturn(new Person(3, "Niek"))
                .thenReturn(new Person(4, "Gijs"));

        Person actual1 = sut.add("Whatever");
        Person actual2 = sut.add("Whatever");
        Person actual3 = sut.add("Whatever");

        assertEquals(3, actual3.id());
        assertEquals("Niek", actual3.name());
    }

    // Capture args with ArgumentCaptor
    @Test
    void whenSelectByNameIsCalledThenSearchIsCalledWithNameAndWildcardedName() {
        ArgumentCaptor<String> arg = ArgumentCaptor.forClass(String.class);
        when(daoMock.select(arg.capture())).thenReturn(new Person(0, ""));

        sut.search("Gijs");

        assertEquals("Gijs", arg.getAllValues().get(0));
        assertEquals("%Gijs%", arg.getAllValues().get(1));
    }
}
