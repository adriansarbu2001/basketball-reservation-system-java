package basketball.model;

public class Match implements IEntity<Long> {
    private Long matchId;
    private String name;
    private float ticketPrice;
    private int noAvailableSeats;

    public Match(Long matchId, String name, float ticketPrice, int noAvailableSeats) {
        this.matchId = matchId;
        this.name = name;
        this.ticketPrice = ticketPrice;
        this.noAvailableSeats = noAvailableSeats;
    }

    public Match(String name, float ticketPrice, int noAvailableSeats) {
        this.name = name;
        this.ticketPrice = ticketPrice;
        this.noAvailableSeats = noAvailableSeats;
    }

    public Match() {

    }

    @Override
    public Long getId() {
        return matchId;
    }

    @Override
    public void setId(Long matchId) {
        this.matchId = matchId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getNoAvailableSeats() {
        return noAvailableSeats;
    }

    public void setNoAvailableSeats(int noAvailableSeats) {
        this.noAvailableSeats = noAvailableSeats;
    }

    public String getStatusMessage() {
        if (getNoAvailableSeats() <= 0) return "SOLD OUT";
        return "";
    }

    @Override
    public String toString() {
        return "Match{" +
                "idm=" + matchId +
                ", name='" + name + '\'' +
                ", ticketPrice=" + ticketPrice +
                ", noAvailableSeats=" + noAvailableSeats +
                '}';
    }
}
