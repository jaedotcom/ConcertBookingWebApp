package proj.concert.service.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import proj.concert.service.mapper.Mapper;
import proj.concert.common.dto.ConcertSummaryDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.dto.ConcertDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;

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
}
