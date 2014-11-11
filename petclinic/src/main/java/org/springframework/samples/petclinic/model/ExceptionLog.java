package org.springframework.samples.petclinic.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "seriojaExceptionLog")
public class ExceptionLog extends BaseEntity {

	@Column(name = "mesaj")
	@NotEmpty
	private String mesaj;

	@Column(name = "parametru")
//	@NotEmpty
	private String parametru;

	@Column(name = "exceptia")
//	@NotEmpty
	private Exception exceptia;

	public ExceptionLog(String mesaj, String parametru, Exception exceptia) {
		super();
		this.mesaj = mesaj;
		this.parametru = parametru;
		this.exceptia = exceptia;
	}

	public ExceptionLog() {
		super();
	}

	public void setMesaj(String mesaj) {
		this.mesaj = mesaj;
	}

	public void setParametru(String parametru) {
		this.parametru = parametru;
	}

	public void setExceptia(Exception exceptia) {
		this.exceptia = exceptia;
	}

	public String getMesaj() {
		return mesaj;
	}

	public String getParametru() {
		return parametru;
	}

	public Exception getExceptia() {
		return exceptia;
	}
}
