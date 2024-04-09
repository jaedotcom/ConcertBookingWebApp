package proj.concert.service.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import proj.concert.common.dto.ConcertDTO;
import proj.concert.common.dto.PerformerDTO;
import proj.concert.common.dto.SeatDTO;
import proj.concert.service.domain.Concert;
import proj.concert.service.domain.Performer;
import proj.concert.service.domain.Seat;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * Concerts or Performers or ....gonna add all to same file 😈😈....
 */
public class Mapper {

    public static Concert toDomainModel(ConcertDTO dtoConcert) {
        Concert fullConcert = new Concert(
                dtoConcert.getId(),
                dtoConcert.getTitle(),
                dtoConcert.getImageName(),
                dtoConcert.getBlurb());

        List<PerformerDTO> performers = dtoConcert.getPerformers();
        fullConcert.setPerformers(
                performers.stream().map(performer -> new Performer(performer.getId(), performer.getName(),
                        performer.getImageName(), performer.getGenre(), performer.getBlurb()))
                        .collect(Collectors.toList()));

        Set<LocalDateTime> hSet = new HashSet<LocalDateTime>();
        for (LocalDateTime x : dtoConcert.getDates())
            hSet.add(x);
        fullConcert.setDates(hSet);

        return fullConcert;
    }

    public static ConcertDTO toDto(Concert concert) {
        ConcertDTO dtoConcert = new ConcertDTO(
                concert.getId(),
                concert.getTitle(),
                concert.getImageName(), concert.getBlurb());

        List<Performer> performers = concert.getPerformers();

        dtoConcert.setPerformers(
                performers.stream().map(performer -> new PerformerDTO(performer.getId(), performer.getName(),
                        performer.getImageName(), performer.getGenre(), performer.getBlurb()))
                        .collect(Collectors.toList()));

        dtoConcert.setDates(new ArrayList<LocalDateTime>(concert.getDates()));

        return dtoConcert;

    }

    public static PerformerDTO toDto(Performer performer) {
        PerformerDTO dtoPerformer = new PerformerDTO(
                performer.getId(),
                performer.getName(),
                performer.getImageName(), performer.getGenre(), performer.getBlurb());

        return dtoPerformer;
    }

    // Add version stuff once learnt :)
    public static SeatDTO toDto(Seat seat) {
        SeatDTO dtoSeat = new SeatDTO(
            seat.getLabel(), seat.getPrice());

        return dtoSeat;
    }

}
