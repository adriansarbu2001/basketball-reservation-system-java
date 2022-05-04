package basketball.model;

public class Ticket implements IEntity<Long> {
    private Long ticketId;
    private String clientName;
    private int noSeats;
    private Long matchId;

    public Ticket(Long ticketId, String clientName, int noSeats, Long matchId) {
        this.ticketId = ticketId;
        this.clientName = clientName;
        this.noSeats = noSeats;
        this.matchId = matchId;
    }

    public Ticket(String clientName, int noSeats, Long matchId) {
        this.clientName = clientName;
        this.noSeats = noSeats;
        this.matchId = matchId;
    }

    public Ticket() {

    }

    @Override
    public Long getId() {
        return ticketId;
    }

    @Override
    public void setId(Long ticketId) {
        this.ticketId = ticketId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getNoSeats() {
        return noSeats;
    }

    public void setNoSeats(int noSeats) {
        this.noSeats = noSeats;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "idt=" + ticketId +
                ", clientName='" + clientName + '\'' +
                ", noSeats=" + noSeats +
                ", idm=" + matchId +
                '}';
    }
}
