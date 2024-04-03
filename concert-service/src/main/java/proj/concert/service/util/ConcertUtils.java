package proj.concert.service.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Seat;
import proj.concert.service.services.PersistenceManager;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConcertUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertUtils.class);

    /**
     * This method will clear all seat and booking data from the database. Then, it will create all Seat objects for
     * all concerts and dates.
     */
    public static void initConcerts() {
        LOGGER.debug("initConcerts(): Creating the Application");

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {

            // Get all concerts
            em.getTransaction().begin();
            TypedQuery<Concert> query = em.createQuery("select c from Concert c", Concert.class);
            List<Concert> concerts = query.getResultList();

            // Get all dates for all concerts
            Set<LocalDateTime> allDates = new HashSet<>();
            for (Concert c : concerts) {
                Set<LocalDateTime> dates = c.getDates();
                allDates.addAll(dates);
            }
            em.getTransaction().commit();

            LOGGER.debug("initConcerts(): There are " + allDates.size() + " concert dates");

            // For each concert date, create the seats for that date and persist them.
            int seatCount = 0;

            em.getTransaction().begin();
            Set<Seat> seatsForDate = TheatreLayout.createSeatsFor();
            for (Seat s : seatsForDate) {
                em.persist(s);
                seatCount++;
            }
            em.getTransaction().commit();

            LOGGER.debug("initConcerts(): Created " + seatCount + " seats!");
        } finally {
            em.close();
        }
    }
}
