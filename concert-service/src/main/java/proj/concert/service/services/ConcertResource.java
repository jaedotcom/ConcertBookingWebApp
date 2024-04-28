package proj.concert.service.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import proj.concert.service.mapper.Mapper;
import proj.concert.common.dto.ConcertSummaryDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.dto.BookingRequestDTO;
import proj.concert.common.dto.ConcertDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;
import proj.concert.common.dto.SeatDTO;
import proj.concert.common.dto.UserDTO;
import proj.concert.service.domain.Seat;
import proj.concert.service.domain.User;

import java.util.UUID;


@Path("/concert-service")
public class ConcertResource {

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



    @GET
    @Path("/seats/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveSeatsByDate( @PathParam("date") String date, @QueryParam("status") String status) {    

        EntityManager em = PersistenceManager.instance().createEntityManager();
        ArrayList<SeatDTO> seatsDto = new ArrayList<SeatDTO>();
        LocalDateTime checkDate = LocalDateTime.parse(date);
        
        try{
            TypedQuery<Seat> query = em.createQuery("select s from Seat s", Seat.class);

            for (Seat seat : query.getResultList()) {

                if (seat.getDate().isEqual(checkDate)) {

                    if (status.equals("Any")) {
                        seatsDto.add(Mapper.toDto(seat));

                    } else if (status.equals("Booked") && seat.getIsBooked()) {
                        seatsDto.add(Mapper.toDto(seat));

                    } else if (status.equals("Unbooked") && !seat.getIsBooked()) {
                        seatsDto.add(Mapper.toDto(seat));
                    }
                }
            }
            // System.out.println("***********************************");
            // if (status.equals("Any")){
            //     System.out.println("into the first IF");
            //     for (Seat seat : query.getResultList()){
            //         if (seat.getDate().isEqual(checkDate)){ seatsDto.add(Mapper.toDto(seat));}
            //     }
            // } else if (status.equals("Booked")){
                
            // } else if (status.equals("Unbooked")){

            // }

        } catch (Exception e){
            return Response.status(404).build();
        } finally{
            em.close();
        }
        
        return Response.ok(seatsDto).build();
    }

    @Path("/bookings")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response makeBooking(BookingRequestDTO bookingRequest) {

        EntityManager em = PersistenceManager.instance().createEntityManager();

        try {
            em.getTransaction().begin();
            TypedQuery<Seat> query = em.createQuery("select s from Seat s where s.date = :date and s.label = :label", Seat.class);
            query.setParameter("date", bookingRequest.getDate());
            query.setParameter("label", bookingRequest.getSeatLabels());
            Seat seat = query.getSingleResult();
            seat.setIsBooked(true);
            em.getTransaction().commit();
            return Response.ok().build();

        } catch (Exception e) {
            return Response.status(404).build();
        } finally {
            em.close();
        }
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticateUser(UserDTO credentials) {
        try {
            boolean loggedIn = authenticate(credentials.getUsername(), credentials.getPassword());
    
            if (!loggedIn) {
                return Response.status(Response.Status.UNAUTHORIZED).build();

            } else {
                NewCookie token = makeCookie(credentials.getUsername());
                return Response.ok().cookie(token).build();
            }
    
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
    
        private boolean authenticate(String username, String password) {

            EntityManager em = PersistenceManager.instance().createEntityManager();

            try {
                TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
                query.setParameter("username", username);
                User user = query.getSingleResult();

                if(user == null) {
                    return false;
                }
                if(!user.getPassword().equals(password)) {
                    return false;
                }
                return true;

            } catch (NoResultException nre) {
                return false;

            } finally {
                em.close();

            }
        }

        private NewCookie makeCookie(String clientId) {
            String id = clientId != null ? clientId : UUID.randomUUID().toString();
            NewCookie newCookie = new NewCookie("auth", id);
            return newCookie;
        }
 
        

}



