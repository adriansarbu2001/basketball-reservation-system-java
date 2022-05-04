package basketball.model;

import java.io.Serializable;

public interface IEntity<ID> extends Serializable {
    void setId(ID id);
    ID getId();
}
