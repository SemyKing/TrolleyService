package com.grigorij.trolleyservice.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;
import java.util.Date;

@Entity
public class Gig {

	@PrimaryKey(autoGenerate = true)
	private int id;

	private String name;

	private Date date;

	public Gig() {
		this.setDate(Calendar.getInstance().getTime());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
