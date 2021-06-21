package it.uniroma3.siw.repos;

import java.util.List;

import it.uniroma3.siw.model.Messaggio;
import it.uniroma3.siw.model.User;

public interface MessaggioRepo
extends org.springframework.data.repository.CrudRepository<Messaggio, Long>
{
	public List<Messaggio> findByCreatore(User creatore);
}
