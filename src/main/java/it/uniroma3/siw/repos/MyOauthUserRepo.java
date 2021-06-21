package it.uniroma3.siw.repos;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.MyOauthUser;

public interface MyOauthUserRepo extends CrudRepository<MyOauthUser, Long> {

}
