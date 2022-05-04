package basketball.model.validator;

import basketball.model.Ticket;

public class TicketValidator implements IValidator<Ticket> {
    @Override
    public void validate(Ticket ticket) throws ValidatorException {
        String err = "";
        if (ticket.getClientName().length() <= 0) {
            err = err + "client_name can not be empty!\n";
        }
        if (ticket.getNoSeats() <= 0) {
            err = err + "no_seats must be > 0!\n";
        }
        if (err.length() > 0) {
            throw new ValidatorException(err);
        }
    }
}
