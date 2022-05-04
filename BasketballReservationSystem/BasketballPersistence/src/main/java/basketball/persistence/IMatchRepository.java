package basketball.persistence;

import basketball.model.Match;

import java.util.List;

public interface IMatchRepository extends IRepository<Long, Match> {
    List<Match> availableMatchesDescending();
}
