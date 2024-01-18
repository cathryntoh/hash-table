import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tester class for the functionalities of the HashtableMap class. It ensures the correctness of the implementation
 * the class's methods on various scenarios.
 * @author cathr
 */
public class HashtableMapTests {

    /**
     * This tester method tests the functionality and correctness of the HashtableMap class's constructor methods.
     */
    @Test
    public void testConstructors() {
        // (1) default constructor
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>();
            int actualCapacity = hashtable.getCapacity();
            int expectedCapacity = 8;
            assertEquals(actualCapacity, expectedCapacity);
        }
        // (2) constructor using valid capacity
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(15);
            int actualCapacity = hashtable.getCapacity();
            int expectedCapacity = 15;
            assertEquals(actualCapacity, expectedCapacity);
        }
        // (3) constructor using invalid capacity
        {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> new HashtableMap<>(-1));
            String expectedMessage = "Capacity must be more than 0!";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
    }

    /**
     * This tester method tests the functionality and correctness of the HashtableMap class's put() method.
     */
    @Test
    public void testPut() {
        // (1) key is null
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(10);
            Exception exception = assertThrows(IllegalArgumentException.class, () -> hashtable.put(null, "invalid key"));
            String expectedMessage = "Invalid key or key already exists!";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
        // (2) duplicate key
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(10);
            hashtable.put(1, "one");
            Exception exception = assertThrows(IllegalArgumentException.class, () -> hashtable.put(1, "another one"));
            String expectedMessage = "Invalid key or key already exists!";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
        // (3) added without hash collision
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(10);
            hashtable.put(1, "one");
            hashtable.put(2, "two");
            String actualValIndex1 = hashtable.hashTable[1].value;
            String actualValIndex2 = hashtable.hashTable[2].value;
            int actualSize = hashtable.getSize();
            assertEquals("one", actualValIndex1);
            assertEquals("two", actualValIndex2);
            assertEquals(2, actualSize);
        }
        // (4) added with hash collision
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(10);
            hashtable.put(1, "one");
            hashtable.put(2, "two");
            hashtable.put(3, "three");
            hashtable.put(11, "eleven");
            String actualValIndex1 = hashtable.hashTable[1].value;
            String actualValIndex4 = hashtable.hashTable[4].value;
            int actualSize = hashtable.getSize();
            assertEquals("one", actualValIndex1);
            assertEquals("eleven", actualValIndex4);
            assertEquals(4, actualSize);
        }
        // (5) added with array growth (check capacity-doubling and rehashing)
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            hashtable.put(1, "one"); // loadFactor = 0.2
            hashtable.put(2, "two"); // loadFactor = 0.4
            hashtable.put(18, "eighteen"); // loadFactor = 0.6
            hashtable.put(29, "twenty nine"); // loadFactor = 0.8 (> overload factor 0.7)
            int actualCapacity = hashtable.getCapacity();
            int actualSize = hashtable.getSize();
            String actualValIndex8 = hashtable.hashTable[8].value;
            String actualValIndex9 = hashtable.hashTable[9].value;
            assertEquals(10, actualCapacity);
            assertEquals("eighteen", actualValIndex8);
            assertEquals("twenty nine", actualValIndex9);
            assertEquals(4, actualSize);
        }
    }

    /**
     * This tester method tests the functionality and correctness of the HashtableMap class's containsKey() method.
     */
    @Test
    public void testContainsKey() {
        // (1) key exists
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            hashtable.put(1, "one");
            boolean actualContainsKey = hashtable.containsKey(1);
            assertEquals(true, actualContainsKey);
        }
        // (2) key does not exist
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            hashtable.put(1, "one");
            boolean actualContainsKey = hashtable.containsKey(7);
            assertEquals(false, actualContainsKey);
        }
        // (3) removal previously occurred
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            hashtable.put(3, "three");
            hashtable.put(4, "four");
            hashtable.put(8, "eight");
            hashtable.remove(4);
            boolean actualContainsKey = hashtable.containsKey(8);
            assertEquals(true, actualContainsKey);
        }
    }

    /**
     * This tester method tests the functionality and correctness of the HashtableMap class's get() method.
     */
    @Test
    public void testGet() {
        // (1) key does not exist
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            Exception exception = assertThrows(NoSuchElementException.class, () -> hashtable.get(3));
            String expectedMessage = "Key does not exist in the hashtable!";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
        // (2) key exists
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            hashtable.put(1, "one");
            String actualValue = hashtable.get(1);
            assertEquals("one", actualValue);
        }
    }

    /**
     * This tester method tests the functionality and correctness of the HashtableMap class's remove() method.
     */
    @Test
    public void testRemove() {
        // (1) key does not exist
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            Exception exception = assertThrows(NoSuchElementException.class, () -> hashtable.remove(3));
            String expectedMessage = "Key does not exist in the hashtable!";
            String actualMessage = exception.getMessage();
            assertEquals(expectedMessage, actualMessage);
        }
        // (2) key exists
        {
            HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
            hashtable.put(1, "one");
            hashtable.put(4, "four");
            String actualRemovedValue = hashtable.remove(1);
            String actualValIndex1 = hashtable.hashTable[1].value;
            int actualSize = hashtable.getSize();
            assertEquals("one", actualRemovedValue);
            assertEquals(null, actualValIndex1);
            assertEquals(1, actualSize);
        }
    }

    /**
     * This tester method tests the functionality and correctness of the HashtableMap class's clear() method.
     */
    @Test
    public void testClear() {
        HashtableMap<Integer,String> hashtable = new HashtableMap<>(5);
        hashtable.put(1, "one");
        hashtable.put(4, "four");
        hashtable.clear();
        int actualSize = hashtable.getSize();
        int actualLoadFactor = actualSize / hashtable.getCapacity();
        HashtableMap.Node<Integer,String> actualContentsIndex1 = hashtable.hashTable[1];
        HashtableMap.Node<Integer,String> actualContentsIndex4 = hashtable.hashTable[4];
        assertEquals(0, actualSize);
        assertEquals(0, actualLoadFactor);
        assertEquals(null, actualContentsIndex1);
        assertEquals(null, actualContentsIndex4);
    }

}
