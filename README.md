# ExpireMap

A standard Map interface in Java with entries that can expire after a given time period. Useful data structure for caches and metadata.

### Useful points:

* Thread safety is guaranteed by using thread-safe internal data structures like `ConcurrentHashMap` and `ConcurrentSkipListMap`; and using `final` and `volatile` for as many resources as possible, alongwith making small blocks of code `synchronized` to maintain atomocity.
* Space proportional to the entries in the map is used at all times, because the scheduler is accurate enough to remove expired entries almost immediately.
* Custom schedulers, queues, as well as internal concurrent maps can be used as the design is modular and can work with any customized data structures as long as their interfaces are implemented.

### Interface methods:

* public V put(K key, V value, long timeoutMs);
* public V get(K key);
* public V remove(K key);

### Deep dive:

[ExpireMap.java](src/main/java/com/expiremap/interfaces/ExpireMap.java) is the basic interface for a map that supports put, get, and remove methods.

[CustomExpireMap.java] (src/main/java/com/expiremap/impl/CustomExpireMap.java) is a `ConcurrentHashMap` based implementation for the interface.
Internally `CustomExpireMap` is written to work with any variant of `ConcurrentMap` interface, but `ConcurrentHashMap` here is used for prototype purposes.
The map stores `< Key, ExpireMapEntry >` pairs.

[ExpireMapEntry.java] (src/main/java/com/expiremap/impl/ExpireMapEntry.java) is an implementation that has a time to live (TTL) field for each entry in the map.

[ExpireMapScheduler.java] (src/main/java/com/expiremap/interfaces/ExpireMapScheduler.java) is the basic interface that any new custom scheduler needs to implement,
to schedule the removal of expired items from the map.

[BasicScheduler.java] (src/main/java/com/expiremap/impl/BasicScheduler.java) is a basic single threaded implementation that uses a queue based on the expiry times of entries,
and wakes up exactly when an entry has to be discarded from the map. It monitors the addition/removal from the map and updates it's own wake up/sleep times accordingly.
This scheduler can be customized according to the needs like fine tuning wake up/sleep times, deciding on the schedule for removal of expired items, no. of threads concurrently accessing the maps, etc.
The custom schedulers should just implement the interface `ExpireMapScheduler`.

The scheduler uses a queue as a data structure to order the entries in the map according to their TTL. The BasicScheduler by default uses [BasicQueue.java] (src/main/java/com/expiremap/impl/BasicQueue.java),
which is a `ConcurrentSkipListMap` to order the entries by their TTL. But any custom queueing mechansim can be used instead, as long as it implements the [ExpireMapQueue.java](src/main/java/com/expiremap/interfaces/ExpireMapQueue.java) interface.

### Tests:

* Only a few basic unit tests are covered to ensure the basic functionalities of `get`, `put`, and `remove` work correctly. Other tests are yet to be written.

### Build:

* `mvn clean install`
