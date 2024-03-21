package proj.concert.service.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Domain model class to represent seats at the concert venue.
 * <p>
 * A Seat describes a seat in terms of:
 * label the seat label
 * price the price
 */
@Entity
public class Seat {
	@Id
	private String label;
	private BigDecimal price;

	public Seat() {
	}

	// why do we need to connect a seat with a specific date & is booked,
	// this could be deduced from the various reservations
	// this was here from the start 👇
	// public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal
	// cost) {
	public Seat(String label, BigDecimal price) {
		this.label = label;
		this.price = price;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		Seat seat = (Seat) o;

		return new EqualsBuilder()
				.append(label, seat.label)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(label)
				.toHashCode();
	}
}
