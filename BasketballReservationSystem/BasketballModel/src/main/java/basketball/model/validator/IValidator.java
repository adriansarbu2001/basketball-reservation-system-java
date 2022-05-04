package basketball.model.validator;

public interface IValidator<E> {
    void validate(E entity) throws ValidatorException;
}
