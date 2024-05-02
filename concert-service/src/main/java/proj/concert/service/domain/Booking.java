package proj.concert.service.domain;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;

/**
 * Represents a completed booking.
 * concertId the id of the concert which was booked
 * date the date on which that concert was booked
 * seats the seats which were booked for that concert on that date
 */

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long bookingId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_username")
    private User user;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    private LocalDateTime date; // must be a valid concert date

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "seat_label")
    private Set<Seat> seats = new TreeSet<Seat>();

    public Booking() {
    }

    public Booking(User user, Concert concert, LocalDateTime date, Set<Seat> seats) {
        this.user = user;
        this.concert = concert;
        this.date = date;
        this.seats = seats;
    }

    public long getBookingId() {
        return bookingId;
    }

    public Concert getConcert() {
        return concert;
    }

    public void setConcert(Concert concert) {
        this.concert = concert;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Set<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Set<Seat> seats) {
        this.seats = seats;
    }

    public User getUser() {
        return user;
    }
}
