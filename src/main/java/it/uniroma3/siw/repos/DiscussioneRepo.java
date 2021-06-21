package it.uniroma3.siw.repos;

import java.util.List;

import it.uniroma3.siw.model.Discussione;

public interface DiscussioneRepo 
extends org.springframework.data.repository.CrudRepository<Discussione, Long>
{
	List<Discussione> findByNomeContainingIgnoreCase(String parola);
}
