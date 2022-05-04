package basketball.model.validator;

public class ValidatorException extends Exception {
    public ValidatorException(String errors) {
        super(errors);
    }
}
