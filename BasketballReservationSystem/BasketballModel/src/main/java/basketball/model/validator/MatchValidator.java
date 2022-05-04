package basketball.model.validator;

import basketball.model.Match;

public class MatchValidator implements IValidator<Match> {

    @Override
    public void validate(Match match) throws ValidatorException {
        String err = "";
        if (match.getName().length() <= 0) {
            err = err + "name can not be empty!\n";
        }
        if (match.getTicketPrice() < 0) {
            err = err + "ticket_price must be >= 0!\n";
        }
        if (match.getNoAvailableSeats() < 0) {
            err = err + "no_available_seats must be >= 0!\n";
        }
        if (err.length() > 0) {
            throw new ValidatorException(err);
        }
    }
}
