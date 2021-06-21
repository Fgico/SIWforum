package it.uniroma3.siw.repos;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
	
	public Optional<User> findByNickName(String nickName);

}