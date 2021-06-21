package it.uniroma3.siw;

import java.util.List;

public class Page<T> {
	
	List<T> elementi;
	boolean hasAfter;
	boolean hasBefore;
	int pageNumber;
	
	int step = 10;
	
	public Page(List<T> lista, int pageNum ){
		int startPos = (pageNum - 1) * step;
		
		if(lista.size() <= 10) {
			elementi = lista;
			pageNumber= 1;
			hasAfter = hasBefore = false;
			return;
		}
		if(startPos<0) {
			elementi = lista.subList(0, step);
			pageNumber = 1;
			hasAfter = true;
			hasBefore = false;
			return;
		}
		if (startPos + step >= lista.size()) {
			elementi = lista.subList(lista.size()-(lista.size()%step), lista.size());
			pageNumber = (lista.size()/10)+1;
			hasAfter = false;
			hasBefore = true;
			return;
		}
		pageNumber = (startPos/step)+1;
		elementi = lista.subList(startPos, startPos+step);	
		
		hasAfter = true;
		if (startPos < 10) hasBefore = false;
		else hasBefore = true;
		
		return;
	}

	public List<T> getElementi() {
		return elementi;
	}

	public void setElementi(List<T> elementi) {
		this.elementi = elementi;
	}

	public boolean isHasAfter() {
		return hasAfter;
	}

	public void setHasAfter(boolean hasAfter) {
		this.hasAfter = hasAfter;
	}

	public boolean isHasBefore() {
		return hasBefore;
	}

	public void setHasBefore(boolean hasBefore) {
		this.hasBefore = hasBefore;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

}
