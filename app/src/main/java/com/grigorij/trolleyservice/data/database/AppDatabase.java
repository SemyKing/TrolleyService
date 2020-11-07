package com.grigorij.trolleyservice.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.grigorij.trolleyservice.data.dao.GigDao;
import com.grigorij.trolleyservice.data.dao.UserDao;
import com.grigorij.trolleyservice.data.model.Gig;
import com.grigorij.trolleyservice.data.model.User;

@Database(entities = {User.class, Gig.class}, exportSchema = false, version = 1)
@TypeConverters({DateTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
	private static final String DATABASE_NAME = "trolleyservice_db";
	private static AppDatabase instance;

	public static synchronized AppDatabase getInstance(Context context) {
		if (instance == null) {
			instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
					DATABASE_NAME)
					.fallbackToDestructiveMigration()
					.build();
		}
		return instance;
	}

	public abstract UserDao userDao();
	public abstract GigDao gigDao();
}
