import java.util.NoSuchElementException;

/**
 * This class models a hash table which maps keys of any type to values of any type; duplicate keys are not allowed.
 * It uses open addressing with a simple linear probe to handle hash collisions.
 * @author cathr
 */
public class HashtableMap<KeyType,ValueType> implements MapADT<KeyType,ValueType> {

    /**
     * This class represents a node holding a key of any type and a value of any type.
     */
    protected static class Node<KeyType,ValueType> {
        protected KeyType key;
        protected ValueType value;

        /**
         * Constructor for the Node class.
         * @param key the contents of the key
         * @param value the contents of the value
         */
        public Node(KeyType key, ValueType value) {
            this.key = key;
            this.value = value;
        }
    }

    protected Node<KeyType,ValueType>[] hashTable; // the array that holds key-value pairs
    private int capacity; // the maximum number of elements hashTable can hold
    private int size = 0; // the number of elements currently held in hashTable
    private double loadFactor = 0; // size divided by capacity

    /**
     * Constructor for the HashtableMap class.
     * @param capacity the maximum number of elements hashTable can hold
     * @throws IllegalArgumentException if capacity is 0 or negative
     */
    @SuppressWarnings("unchecked")
    public HashtableMap(int capacity) throws IllegalArgumentException {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be more than 0!");
        }
        this.capacity = capacity;
        this.hashTable = (Node<KeyType,ValueType>[]) new Node[capacity];
    }

    /**
     * Default constructor for the HashtableMap class. The default capacity is 8.
     */
    @SuppressWarnings("unchecked")
    public HashtableMap() {
        this.capacity = 8;
        this.hashTable = (Node<KeyType,ValueType>[]) new Node[8];
    }

    /**
     * Adds a new key-value pair/mapping to the hashtable.
     * @param key the contents of the key
     * @param value the contents of the value
     * @throws IllegalArgumentException if key is null or duplicate of one already stored
     */
    @Override
    public void put(KeyType key, ValueType value) throws IllegalArgumentException {
        // if key is null or has a duplicate, throw an exception
        if (key == null || containsKey(key)) {
            throw new IllegalArgumentException("Invalid key or key already exists!");
        }

        // create a new node for the key-value pair and add it to the hashtable at its intended index
        Node<KeyType,ValueType> newNode = new Node<>(key, value);
        int index = getIndex(key);
        Node<KeyType,ValueType> currentNode = hashTable[index];

        // open-addressing (simple linear probe): if a node already exists at the given index, increment the index until
        // an unoccupied space is found
        while (currentNode != null) {
            // if the node occupying index is a sentinel node, go ahead and place new key-value pair at that index
            if (currentNode.key == null) {
                break;
            }
            // if not, continue linear probe until an empty spot is found
            else {
                index = (index + 1) % capacity;
                currentNode = hashTable[index];
            }
        }
        hashTable[index] = newNode;

        // increment size of the hashTable and update the loadFactor
        size++;
        loadFactor = (1.0 * size) / capacity;

        // if the load factor exceeds the overload factor (70%), double the capacity and rehash
        if (loadFactor >= 0.7) { this.doubleCapacity(); }
    }

    /**
     * Checks whether a key maps to a value within the hashtable.
     * @param key the contents of the key
     * @return true if the provided key already maps to a value within the hashtable and false if otherwise
     */
    @Override
    public boolean containsKey(KeyType key) {
        int index = getIndex(key);
        // traverse table starting from the initial index of the key
        while (hashTable[index] != null) {
            KeyType currKey = hashTable[index].key;
            if (key.equals(currKey)) return true;
            else index = (index + 1) % capacity;
        }
        return false;
    }

    /**
     * Retrieves the specific value that a key maps to.
     * @param key the contents of the key
     * @return the specific value that key maps to
     * @throws NoSuchElementException if key is not stored in the hashtable
     */
    @Override
    public ValueType get(KeyType key) throws NoSuchElementException {
        // if key is not stored in the hashtable, throw an exception
        if (!containsKey(key)) {
            throw new NoSuchElementException("Key does not exist in the hashtable!");
        }

        // search the hashtable for the key and return the corresponding value when found
        ValueType matchedValue = null;
        int index = getIndex(key);
        // traverse table starting from the initial index of the key
        while (hashTable[index] != null) {
            KeyType currKey = hashTable[index].key;
            if (key.equals(currKey)) {
                matchedValue = hashTable[index].value;
                break;
            }
            else index = (index + 1) % capacity;
        }
        return matchedValue;
    }

    /**
     * Removes the mapping for a given key from the hashtable.
     * @param key the contents of the key
     * @return the value of the key-value mapping that was removed
     * @throws NoSuchElementException if key is not stored in the hashtable
     */
    @Override
    public ValueType remove(KeyType key) throws NoSuchElementException {
        // if key is not stored in the hashtable, throw an exception
        if (!containsKey(key)) {
            throw new NoSuchElementException("Key does not exist in the hashtable!");
        }

        // traverse the hashtable for the key and remove the pair
        ValueType removedValue = null;
        int index = getIndex(key);
        // traverse table starting from the initial index of the key
        while (hashTable[index] != null) {
            KeyType currKey = hashTable[index].key;
            if (key.equals(currKey)) {
                removedValue = hashTable[index].value;
                size--;
                // replace removed node with sentinel node to indicate that a node previously existed here
                Node<KeyType,ValueType> sentinelNode = new Node<>(null, null);
                hashTable[index] = sentinelNode;
                break;
            }
            else index = (index + 1) % capacity;
        }
        loadFactor = (1.0 * size) / capacity;
        return removedValue;
    }

    /**
     * Remove all key-value pairs from the hashtable.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        this.size = 0;
        this.loadFactor = 0;
        this.hashTable = (Node<KeyType,ValueType>[]) new Node[this.capacity];
    }

    /**
     * Retrieves the number of keys stored within the hashtable
     * @return the number of keys stored within the hashtable
     */
    @Override
    public int getSize() {
        return this.size;
    }

    /**
     * Retrieves the hashtable's capacity (size of its underlying array)
     * @return the hashtable's capacity
     */
    @Override
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Helper method that calculates the index of the key-value pair (the absolute value of the key's hashCode() modulus
     * the HashtableMap's current capacity).
     * @param key the contents of the key
     * @return the index to insert the key-value pair at
     */
    private int getIndex(KeyType key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    /**
     * Helper method that doubles the capacity and rehashes the hashtable.
     */
    @SuppressWarnings("unchecked")
    private void doubleCapacity() {
        // store the existing data temporarily
        Node<KeyType,ValueType>[] prevArray = hashTable;
        int prevCapacity = capacity;

        // create a new array with double the initial capacity, and reset the size to 0
        capacity = prevCapacity * 2;
        size = 0;
        hashTable = (Node<KeyType,ValueType>[]) new Node[capacity];

        // traverse the old array and add key-value pairs (excluding sentinel nodes) according to the recalculated
        // hash values
        for (int i = 0; i < prevCapacity; i++) {
            Node<KeyType,ValueType> map = prevArray[i];
            if (map != null && map.key != null) {
                hashTable[getIndex(map.key)] = map;
                size++;
            }
        }
        loadFactor = (1.0 * size) / capacity;
    }
}
