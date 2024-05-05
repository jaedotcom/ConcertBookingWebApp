import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

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

    public Seat() {
    }

    public Seat(String label, LocalDateTime date, BigDecimal price) {
        this.id = new SeatKey(label, date);
        this.price = price;
    }

    public String getLabel() {
        return id.getLabel();
    }

    public LocalDateTime getDate() {
        return id.getDate();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setIsBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }

    public int getVersion(){
        return this.version;
    }

    public void setVersion(int newVersion){
        this.version = newVersion;
    }

    public Set<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(Set<Booking> bookings) {
        this.bookings = bookings;
    }

    @Override
    public String toString() {
        return id.getLabel();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Seat seat = (Seat) o;

        return new EqualsBuilder()
                .append(id, seat.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}