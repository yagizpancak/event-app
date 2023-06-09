package com.event.eventmanagementservice.repository;


import com.event.eventmanagementservice.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
	@Query(value = "select * from events where events.id in :ids", nativeQuery = true)
	List<Event> findAllByIds(@Param("ids") List<String> ids);

	@Query(value = "select * from events where events.organizator_username in :usernames", nativeQuery = true)
	Page<Event> findAllByUsernames(Pageable pageable, @Param("usernames") List<String> usernames);

	@Query(value = "select * from events where events.organizator_username = :username", nativeQuery = true)
	Page<Event> findByUsername(Pageable pageable,  @Param("username") String username);
}
