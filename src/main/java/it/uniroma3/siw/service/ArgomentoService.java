package it.uniroma3.siw.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Argomento;
import it.uniroma3.siw.repos.ArgomentoRepo;

@Service
public class ArgomentoService {
	
	@Autowired
	protected ArgomentoRepo mRepo;
	
	@Transactional
	public Argomento prendi(Long id) {
		return mRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public Argomento metti(Argomento arg) {
		return mRepo.save(arg);
	}
	
	@Transactional
    public List<Argomento> tutti() {
        List<Argomento> result = new ArrayList<>();
        Iterable<Argomento> iterable = this.mRepo.findAll();
        for(Argomento arg : iterable)
            result.add(arg);
        return result;
    }

	public void elimina(Long id) {
		mRepo.deleteById(id);
	}

}
