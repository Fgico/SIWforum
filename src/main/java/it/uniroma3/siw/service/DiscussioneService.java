package it.uniroma3.siw.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Discussione;
import it.uniroma3.siw.repos.DiscussioneRepo;

@Service
public class DiscussioneService {
	
	@Autowired
	protected DiscussioneRepo mRepo;
	
	@Transactional
	public Discussione prendi(Long id) {
		return mRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public Discussione metti(Discussione dsc) {
		return mRepo.save(dsc);
	}
	
	@Transactional
    public List<Discussione> tutti() {
        List<Discussione> result = new ArrayList<>();
        Iterable<Discussione> iterable = this.mRepo.findAll();
        for(Discussione dsc : iterable)
            result.add(dsc);
        return result;
    }
	
	@Transactional
	public void elimina(Long id) {
		mRepo.deleteById(id);
	}
	
	@Transactional
	public List<Discussione> cercaInNome(String chiave){
		return mRepo.findByNomeContainingIgnoreCase(chiave);
	}

}
