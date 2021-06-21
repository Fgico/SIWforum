package it.uniroma3.siw.model;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

@Entity
public class Argomento {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, unique = true)
	private String nome;
	
	@OneToMany (mappedBy = "argomento", cascade = CascadeType.ALL)
	private List<Discussione> threads;

	public Discussione creaThread(Discussione dsc) {
		this.getThreads().add(dsc);
		dsc.setArgomento(this);
		return dsc;
	}
	
	public Argomento(){
		setThreads(new LinkedList<>());
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Discussione> getThreads() {
		return threads;
	}

	public void setThreads(List<Discussione> threads) {
		this.threads = threads;
	}
	
	
}
