package ru.gur.archauth.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.gur.archauth.entity.Session;

@Repository
public interface RedisRepository extends CrudRepository<Session, String> {
}
