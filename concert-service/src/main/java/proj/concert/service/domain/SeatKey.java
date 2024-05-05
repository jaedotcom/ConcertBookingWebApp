package proj.concert.service.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class SeatKey implements Serializable {
    private String label;
    private LocalDateTime date;

    public SeatKey() {
    }

    public SeatKey(String label, LocalDateTime date) {
        this.label = label;
        this.date = date;
    }

    public String getLabel() {
        return label;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatKey)) return false;
        SeatKey that = (SeatKey) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, date);
    }
}
