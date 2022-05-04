package basketball.persistence;

import basketball.model.IEntity;
import basketball.model.validator.ValidatorException;

public interface IRepository<ID, T extends IEntity<ID>> {

    void save(T t) throws ValidatorException;

    void remove(ID id);

    Long size();

    T findOne(ID id);

    Iterable<T> findAll();

    void update(T t) throws ValidatorException;
}
