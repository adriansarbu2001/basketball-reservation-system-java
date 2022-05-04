package basketball.persistence;

import basketball.model.User;

public interface IUserRepository extends IRepository<Long, User> {
    User findBy(String username, String password);
}
