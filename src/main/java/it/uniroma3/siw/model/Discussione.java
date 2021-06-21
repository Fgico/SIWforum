package it.uniroma3.siw.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.*;

@Entity
public class Discussione {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(nullable = false, unique = false)
	private String nome;
	
	@Column
	private LocalDateTime lastReplyTime;
	
	@ManyToOne
	private Argomento argomento;
	
	@OneToMany(mappedBy = "discussione", cascade = CascadeType.ALL)
	private List<Messaggio> risposte;

	public Messaggio rispondi(Messaggio msg) {
		this.getRisposte().add(msg);
		msg.setDiscussione(this);
		this.setLastReplyTime(LocalDateTime.now().withNano(0));
		msg.setTimePost(this.getLastReplyTime());
		return msg;
	}
	
	public Discussione(){
		setRisposte(new LinkedList<>());
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

	public LocalDateTime getLastReplyTime() {
		return lastReplyTime;
	}

	public void setLastReplyTime(LocalDateTime lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}

	public Argomento getArgomento() {
		return argomento;
	}

	public void setArgomento(Argomento argomento) {
		this.argomento = argomento;
	}

	public List<Messaggio> getRisposte() {
		return risposte;
	}

	public void setRisposte(List<Messaggio> risposte) {
		this.risposte = risposte;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((argomento == null) ? 0 : argomento.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastReplyTime == null) ? 0 : lastReplyTime.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime * result + ((risposte == null) ? 0 : risposte.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Discussione other = (Discussione) obj;
		if (argomento == null) {
			if (other.argomento != null)
				return false;
		} else if (!argomento.equals(other.argomento))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastReplyTime == null) {
			if (other.lastReplyTime != null)
				return false;
		} else if (!lastReplyTime.equals(other.lastReplyTime))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	
	
}
