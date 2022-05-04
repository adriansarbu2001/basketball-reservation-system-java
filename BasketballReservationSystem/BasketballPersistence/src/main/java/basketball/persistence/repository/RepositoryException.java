package basketball.persistence.repository;

public class RepositoryException extends Exception {
    public RepositoryException(String errors) {
        super(errors);
    }
}
