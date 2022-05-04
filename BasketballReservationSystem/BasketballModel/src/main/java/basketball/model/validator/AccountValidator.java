package basketball.model.validator;

import basketball.model.User;

public class AccountValidator implements IValidator<User> {
    @Override
    public void validate(User user) throws ValidatorException {
        String err = "";
        if (user.getUsername().length() <= 0) {
            err = err + "username can not be empty!\n";
        }
        if (user.getPassword().length() < 5) {
            err = err + "password is too short!\n";
        }
        if (err.length() > 0) {
            throw new ValidatorException(err);
        }
    }
}
