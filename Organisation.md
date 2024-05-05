# Organisation.md for Project 15 
## Team member summary 
* Arnav (@arnard76 | GitHub identifier: 78939786) - Domain Model w/ JPA, Restful API
* Jae (@jaedotcom | GitHub identifier: 83146793) - Entity Relationship Diagram, Restful API, Embeddable issue
* Matthew (@mhol193 | GitHub identifier: 109997934) - Restful API, Version

## Team organisation
We collaborated on all tasks by discussing roadblocks, potential solutions & feedback on implementation 😎😎
.... 

## Domain model organisation Summary
The domain model is organised around the central concept of a concert booking system. 🥁

It includes _User 🙋🏻‍♂️, Concert 🎵, Booking 🤳🏼, Seat 💺, SeatKey 🔑 and Performer 👯‍♂️_ classes.

User represents the system's users, Concert represents the concerts that can be booked, Booking represents the bookings made by users, Seat represents the seats that can be booked for a concert, and Performer represents the performers in a concert. 🎹

## Detailed Domain model organisation 
**Concert:** The Concert class uses @ElementCollection for handling dates, which is a good choice for collections of simple types. It also maintains a @ManyToMany relationship with the Performer class. 👯‍♂️

**Performer:** The Performer class uses @Enumerated(EnumType.STRING) for handling Genre, which ensures that the genre is stored in a readable format in the database. 💅🏻

**Seat:** The Seat class correctly uses @ManyToMany for the relationship with Booking. It also uses an @Embeddable class, **SeatKey**, to represent its composite key. This key includes the seat label and the date, which together uniquely identify a seat. The **SeatKey** class implements Serializable, which is necessary for composite keys in JPA. It also overrides the equals() method to ensure correct comparison of SeatKey instances. 
The **_SeatKey_** class is a good example of how to handle composite keys in JPA. It also helps prevent **double-booking of seats** by ensuring that each seat for a specific concert on a specific date is unique. 🪑

**Booking**: The Booking class uses @ManyToOne for the relationship with Concert and @ManyToMany for the relationship with Seat. The use of FetchType.LAZY in @ManyToOne is a good choice for performance reasons, as it avoids loading the entire Concert every time a Booking is loaded. 🎟

In terms of **eager vs lazy fetching**, we've made good use of lazy fetching in the Booking class to avoid unnecessary database hits. 🧨

For **cascading**, we've used CascadeType.ALL in the Seat class for the bookings field. This means that any operation done on a Seat will be cascaded to the associated Booking entities, ensuring that Booking entities are always in sync with their associated Seat.💺

By default, @OneToMany and @ManyToMany associations use the FetchType.LAZY strategy while the @OneToOne and @ManyToOne use the FetchType.EAGER strategy instead. This implicit fetching strategy is used throughout our domain model to ensure efficient data loading. 💅🏻

In terms of **concurrency**, we've used the @Version annotation for **optimistic** **locking**. This helps to prevent "lost updates", where two transactions read a row, one writes to it and commits, and then the second writes to it and commits, overwriting the first transaction's changes without knowing it. This is a key strategy for handling concurrency in JPA. 👍

## Concurrency strategy 🤌
🐥 Our program minimises concurrency errors through the use of Java's EntityTransaction and EntityManager APIs. Transactions are started before critical sections of code where database operations are performed. If an error occurs, the transaction is rolled back, ensuring data consistency and preventing race conditions or dirty reads/writes. 💁🏻‍♂️

```
EntityTransaction transaction = em.getTransaction();
EntityManager em = PersistenceManager.instance().createEntityManager();
EntityTransaction transaction = em.getTransaction();
try {
    transaction.begin();
    // ... perform database operations ...
    transaction.commit();
} catch (PersistenceException e) {
    if (transaction.isActive()) {
        transaction.rollback(); ⬅️
    }
} finally {
    em.close();
}
```
Additionally, the EntityManager is used to create and manage queries. These operations are enclosed within a try-catch block to handle any PersistenceException that might occur due to concurrent data access. 😎

```
private boolean authenticate(String username, String password) {
    EntityManager em = PersistenceManager.instance().createEntityManager();
    try {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        User user = query.getSingleResult();
    } catch (PersistenceException e) {
        e.printStackTrace();
        return false;
    } finally {
        em.close();
    }
}
```
In both cases, the EntityManager is closed to free up resources, ensuring the database remains in a consistent state even when errors occur. 🚀

We also used @Version annotation in our Seat class, it would definitely help in handling concurrency issues. 💅🏻

```
@Entity
public class Seat implements Serializable {
    @EmbeddedId
    private SeatKey id;
    private BigDecimal price;
    private boolean isBooked = false;
    @Version
    private int version;

    @ManyToMany(mappedBy = "seats", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Booking> bookings;
...

```


#### should `Seat.java` include `isBooked` and `date`?

1. **_No it shouldn't_** 🙅🏻‍♂️
the initial `Seat.java` included a constructor that had these properties but it felt odd to be created 120 new seats for every concert date because for each new concert the same seats would be used. So we tried removing those properties and our ER diagram looked like this:
   
      <img width="479" alt="image" src="https://github.com/CS331-2024/project-project-15/assets/78939786/e5050d21-2260-4ee6-9daf-283b0b39f9ea">

2. **Yes it should** 👍
 We have the requirement  that each seat can only be booked by a single person for each concert date. Therefore, to check if a seat has been booked (e.g. during concurrent booking requests), we would have to check the `seat_booking` table joined with `booking` table to see if a certain seat has been booked. This would take longer than simply checking the seats table. So including the `isBooked` and `date` on the `Seat` entity is a good idea! 👌👌
 
   <img width="317" alt="image" src="https://github.com/CS331-2024/project-project-15/assets/78939786/a41b5065-cde0-4a04-a47c-71bfb98a3d2c">


## Scalability
In terms of scalability, our code provided in the ConcertResource class addresses some aspects of scalability.

1. **Thread Pool**: The code initialises an ExecutorService thread pool using Executors.newCachedThreadPool(). This allows for concurrent execution of tasks, which can improve scalability by efficiently utilising available system resources and handling multiple requests concurrently.👍

2. **Asynchronous Response**: The subscribeToConcert method uses the @Suspended annotation and the AsyncResponse parameter to handle the subscription request asynchronously. By offloading the processing of the subscription to a separate thread, the main thread is freed up to handle other requests, improving the overall scalability of the system.👍

3. **Efficient Querying**: The code uses TypedQuery to execute database queries and retrieve data. By using typed queries, the code can benefit from query optimisation and caching mechanisms provided by the underlying persistence framework. This can improve the performance and scalability of the application when dealing with large datasets.👍



## New Features 
Here are some potential new features we could add to our `concert-service` going forward: 🥁

1. **Personalised Recommendations:** We could add a feature that recommends concerts to users based on their past bookings via subscription. This would involve adding a new method that uses machine learning algorithms to make recommendations.🫶🏻

2. **Social Features:** We could add features that allow users to share their bookings with friends, or see what concerts their friends are going to - adding new relevant Service methods to handle these social interactions.👯‍♂️

3. **Advanced Search:** We could add more advanced search features, allowing users to search for concerts by genre, artist, location, etc - adding new methods to Service to handle these search queries.👀