package com.grigorij.trolleyservice.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.grigorij.trolleyservice.data.model.User;

import java.util.List;

@Dao
public interface UserDao {
	@Query("SELECT * FROM user WHERE username LIKE :username AND password LIKE :password LIMIT 1")
	User findByCredentials(String username, String password);

	@Query("SELECT * FROM user")
	List<User> getUserList();

	@Insert
	void insertUser(User user);

	@Update
	void updateUser(User user);
}
