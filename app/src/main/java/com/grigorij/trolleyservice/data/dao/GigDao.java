package com.grigorij.trolleyservice.data.dao;

import androidx.room.*;
import com.grigorij.trolleyservice.data.model.Gig;

import java.util.List;

@Dao
public interface GigDao {
	@Query("SELECT * FROM gig WHERE name LIKE :name")
	List<Gig> findByName(String name);

	@Query("SELECT * FROM gig WHERE id LIKE :id LIMIT 1")
	Gig getGigById(int id);

	@Query("SELECT * FROM gig")
	List<Gig> getGigList();

	@Insert
	void insertGig(Gig gig);

	@Update
	void updateGig(Gig gig);

	@Delete
	void deleteGig(Gig gig);
}
