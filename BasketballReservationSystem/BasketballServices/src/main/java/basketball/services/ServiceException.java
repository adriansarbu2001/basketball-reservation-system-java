package basketball.services;

public class ServiceException extends Exception {
    public ServiceException(String errors) {
        super(errors);
    }
}
