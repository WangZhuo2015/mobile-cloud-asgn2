package org.magnum.mobilecloud.video.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.Collection;

//@RepositoryDefinition(domainClass = Video.class, idClass = Long.class)
public interface VideoRepository extends CrudRepository<Video,Long> {
//    Collection<Video> ();
//    Video getVideoById(long id);
    Collection<Video> findAllByName(String name);
    Collection<Video> findAllByDurationLessThan(long duration);
}
