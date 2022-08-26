package com.example.RNU;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserDate.class},version=2,exportSchema = true)
public abstract class UserDateDatabase extends RoomDatabase {

    public abstract UserDateDao getUserDateDao();
    private static volatile UserDateDatabase INSTANCE;
    static  final Migration MIGRATION_1_2 =new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE UserDate ADD COLUNM col_bitmap ");
        }
    };
    //싱글톤
    public static UserDateDatabase getDatabase(Context context){
        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),UserDateDatabase.class,"userList.db")
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private UserDateDao userDao;

        private PopulateDbAsyncTask(UserDateDatabase db) {
            userDao = db.getUserDateDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }
    }
}
