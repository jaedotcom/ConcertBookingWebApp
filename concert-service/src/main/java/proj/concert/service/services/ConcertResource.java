package proj.concert.service.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import proj.concert.service.mapper.Mapper;
import proj.concert.common.dto.ConcertSummaryDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.dto.BookingDTO;
import proj.concert.common.dto.BookingRequestDTO;
import proj.concert.common.dto.ConcertDTO;
import proj.concert.service.domain.Booking;
import proj.concert.common.dto.ConcertInfoNotificationDTO;
import proj.concert.common.dto.ConcertInfoSubscriptionDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;
import proj.concert.common.dto.SeatDTO;
import proj.concert.common.dto.UserDTO;
import proj.concert.service.domain.Seat;
import proj.concert.service.domain.User;


// Class ConcertResource holds all functions to do with the concert services
@Path("/concert-service")
public class ConcertResource {
    ExecutorService threadPool = Executors.newCachedThreadPool();

    // Function that retrieves summaries for each concert and returns a 200 response code and a 404 error if any Exceptions occur
    @GET
    @Path("/concerts/summaries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveConcertSummaries() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        List<ConcertSummaryDTO> summaries = new ArrayList<ConcertSummaryDTO>();

        try {
            TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
            for (Concert concert : query.getResultList()) {
                summaries.add(new ConcertSummaryDTO(concert.getId(), concert.getTitle(), concert.getImageName()));
            }
        } finally {
            em.close();
        }

        return Response.ok(summaries).build();
    }

    // Function that retrieves a concert from an Id and returns a 200 response if found and a 404 error if any Exceptions occur
    @GET
    @Path("/concerts/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveConcert(@PathParam(value = "id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        ConcertDTO concertDto = null;

        try {
            Concert concert = em.find(Concert.class, id);

            concertDto = Mapper.toDto(concert);
        } catch (Exception e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }

        return Response.ok(concertDto).build();

    }

    // Function that retrieves the list of concerts and returns a 200 response and a 404 error if any Exceptions occur
    @GET
    @Path("/concerts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveConcerts() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        ArrayList<ConcertDTO> concertsDto = new ArrayList<ConcertDTO>();

        try {
            TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
            for (Concert concert : query.getResultList()) {
                concertsDto.add(Mapper.toDto(concert));
            }

        } catch (Exception e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }

        return Response.ok(concertsDto).build();
    }

    // Function that retrieves a performer from an Id and returns a 200 response if found and a 404 error if any Exceptions occur
    @GET
    @Path("/performers/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePerformer(@PathParam(value = "id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        PerformerDTO performerDto = null;

        try {
            Performer performer = em.find(Performer.class, id);

            performerDto = Mapper.toDto(performer);
        } catch (Exception e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }

        return Response.ok(performerDto).build();

    }

    // Function that retrieves the list of performers and returns a 200 response and a 404 error if any Exceptions occur
    @GET
    @Path("/performers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrievePerformers() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        ArrayList<PerformerDTO> performersDto = new ArrayList<PerformerDTO>();

        try {
            TypedQuery<Performer> query = em.createQuery("select p from Performer p", Performer.class);
            for (Performer performer : query.getResultList()) {
                performersDto.add(Mapper.toDto(performer));
            }

        } catch (Exception e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }

        return Response.ok(performersDto).build();

    }

    // Function that retrieves the list of seast based on a given date and given status (Any, Booked, Unbooked)
    // Will return a 200 response and a 404 error if any Exceptions occur
    @GET
    @Path("/seats/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSeatsByDate(@PathParam("date") String date, @QueryParam("status") String status) {

        EntityManager em = PersistenceManager.instance().createEntityManager();
        ArrayList<SeatDTO> seatsDto = new ArrayList<SeatDTO>();
        LocalDateTime checkDate = LocalDateTime.parse(date);

        try {
            TypedQuery<Seat> query = em.createQuery("select s from Seat s", Seat.class);

            for (Seat seat : query.getResultList()) {

                if (seat.getDate().isEqual(checkDate)) {

                    if (status != null && status.equals("Any")) {
                        seatsDto.add(Mapper.toDto(seat));

                    } else if (status != null && status.equals("Booked") && seat.getIsBooked()) {
                        seatsDto.add(Mapper.toDto(seat));

                    } else if (status != null && status.equals("Unbooked") && !seat.getIsBooked()) {
                        seatsDto.add(Mapper.toDto(seat));
                    }
                }
            }

        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(500).build();
        } finally {
            em.close();
        }

        return Response.ok(seatsDto).build();
    }

    // Function that retrieves a booking from an Id returns a 200 response and a 404 error if any Exceptions occur
    // If user if unauthroized throw a 401 error
    @GET
    @Path("/bookings/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBooking(@PathParam(value = "id") long bookingId, @CookieParam("auth") NewCookie clientId) {
        if (clientId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String username = clientId.getName();

        EntityManager em = PersistenceManager.instance().createEntityManager();
        BookingDTO bookingDto = null;

        try {
            TypedQuery<User> getUserQuery = em.createQuery("SELECT u FROM User u WHERE u.username = :username",
                    User.class);
            getUserQuery.setParameter("username", username);
            User user;
            try {
                user = getUserQuery.getSingleResult();
            } catch (NoResultException e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();

            }

            TypedQuery<Booking> getBookingQuery = em.createQuery(
                    "select b from Booking b where b.bookingId=:bookingId", Booking.class);
            // getBookingQuery.setParameter("user", user);
            getBookingQuery.setParameter("bookingId", bookingId);
            Booking booking = getBookingQuery.getSingleResult();

            if (booking.getUser() != user) {
                return Response.status(403).build();
            }

            bookingDto = Mapper.tDto(booking);
        } catch (Exception e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }

        return Response.ok(bookingDto).build();

    }

    // Function that retrieves the list of bookings and returns a 200 response and a 500 error if any Exceptions occur
    // If user if unauthroized throw a 401 error
    @Path("/bookings")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBookings(@Context HttpHeaders headers, @CookieParam("auth") NewCookie clientId) {
        if (clientId == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        String username = clientId.getName();

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {

            TypedQuery<User> query3 = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query3.setParameter("username", username);
            User user;
            try {

                user = query3.getSingleResult();
            } catch (NoResultException e) {
                return Response.status(Response.Status.UNAUTHORIZED).build();

            }

            TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.user=:user", Booking.class);
            query.setParameter("user", user);
            List<Booking> bookings = query.getResultList();

            List<BookingDTO> bookingDtos = new ArrayList<BookingDTO>();
            for (Booking booking : bookings) {
                bookingDtos.add(Mapper.tDto(booking));
            }

            return Response.ok(bookingDtos).build();
        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(500).build();
        } finally {
            em.close();
        }
    }

    // Function that creates a booking, will return 500 response for any errors and a 200 response upon creation
    // If user if unauthroized throw a 401 error
    @Path("/bookings")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBooking(@Context UriInfo uriInfo, BookingRequestDTO bookingRequest,
            @CookieParam("auth") NewCookie clientId) {

        if (clientId == null)
            return Response.status(401).build();

        String username = clientId.getName();

        EntityManager em = PersistenceManager.instance().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            LocalDateTime concertDate = bookingRequest.getDate();

            Concert concert = em.find(Concert.class, bookingRequest.getConcertId());
            if (concert == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            TypedQuery<Seat> query = em.createQuery("select s from Seat s where s.date = :date and s.label IN :labels",
                    Seat.class);
            query.setParameter("date", concertDate);
            query.setParameter("labels", bookingRequest.getSeatLabels());

            List<Seat> seats = query.getResultList();

            if (seats.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            for (Seat seat : seats) {
                if (seat.getIsBooked()) {
                    return Response.status(Response.Status.FORBIDDEN).build();
                }
            }

            for (Seat seat : seats) {
                seat.setIsBooked(true);
            }

            Set<Seat> seatsSet = new HashSet<Seat>();
            for (Seat x : seats)
                seatsSet.add(x);

            TypedQuery<User> getUserQuery = em.createQuery("SELECT u FROM User u WHERE u.username = :username",
                    User.class);
            getUserQuery.setParameter("username", username);
            User user = getUserQuery.getSingleResult();
            Booking booking = new Booking(user, em.find(Concert.class, bookingRequest.getConcertId()), concertDate,
                    seatsSet);
            em.persist(booking);

            transaction.commit();

            return Response.created(URI.create(uriInfo.getRequestUri() + "/" + booking.getBookingId())).build();

        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Response.status(500).build();
        } finally {
            em.close();
        }
    }

    // Function that subscribes and returns a 200 response and a 400 error if any Exceptions occur
    // If user if unauthroized throw a 401 error
    @Path("/subscribe/concertInfo")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void subscribeToConcert(@Suspended AsyncResponse response, ConcertInfoSubscriptionDTO dtoSubInfo,
            @CookieParam("auth") NewCookie clientId) {
        if (clientId == null) {
            response.resume(Response.status(401).build()); // for unauthorized
            return;
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            boolean isValidConcert = em.find(Concert.class, dtoSubInfo.getConcertId()).getDates()
                    .contains(dtoSubInfo.getDate());

            if (!isValidConcert)
                throw new Exception("couldn't subscribe, concert not found!");
        } catch (Exception e) {
            response.resume(Response.status(400).build());
            return;
        } finally {
            em.close();
        }

        threadPool.submit(() -> {
            EntityManager emForThread = PersistenceManager.instance().createEntityManager();
            TypedQuery<Seat> query = emForThread
                    .createQuery("select s from Seat s where s.date='" + dtoSubInfo.getDate().toString()
                            + "' and s.isBooked=false", Seat.class);
            int numSeatsRemaining = query.getResultList().size();

            // recheck booking and see if condition is met or the concert is here
            while ((float) numSeatsRemaining * 100.0 / 120.0 > dtoSubInfo.getPercentageBooked()) {
                try {
                    TypedQuery<Seat> query1 = emForThread
                            .createQuery("select s from Seat s where s.date='" + dtoSubInfo.getDate().toString()
                                    + "' and s.isBooked=false", Seat.class);
                    numSeatsRemaining = query1.getResultList().size();

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        response.resume(Response.status(405).build());
                        emForThread.close();
                        return;
                    }
                } catch (IllegalStateException e) {
                    emForThread = PersistenceManager.instance().createEntityManager();
                }

            }

            ConcertInfoNotificationDTO notification = new ConcertInfoNotificationDTO(numSeatsRemaining);
            response.resume(Response.ok(notification).build());
            emForThread.close();
        });

    }

    // Function that logs a user in and returns a 200 response and a 500 error if any Exceptions occur    
    // Will return 401 for incorrect login (unauthorized)
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UserDTO credentials) {
        try {
            boolean loggedIn = authenticate(credentials.getUsername(), credentials.getPassword());

            if (!loggedIn) {
                return Response.status(Response.Status.UNAUTHORIZED).build();

            } else {
                NewCookie token = makeCookie(credentials.getUsername());
                return Response.ok().cookie(token).build();
            }

        } catch (PersistenceException e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    // Authentication process that returns true or false
    private boolean authenticate(String username, String password) {

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();

            if (user == null) {
                return false;
            }
            if (!user.getPassword().equals(password)) {
                return false;
            }
            return true;

        } catch (NoResultException nre) {
            return false;

        } catch (PersistenceException e) {
            e.printStackTrace();
            return false;

        } finally {
            em.close();

        }
    }

    // Function that creates a new cookie
    private NewCookie makeCookie(String clientId) {
        String id = clientId != null ? clientId : "default-client-id";
        NewCookie newCookie = new NewCookie("auth", id);
        return newCookie;
    }
}
