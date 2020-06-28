package il.co.yedidia_shmuel.ddb_2.model.dataSource;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.util.Convert;

@Database(entities = {Package.class}, version = 1, exportSchema = false)
@TypeConverters({Convert.class})
public abstract class PackageDataBase extends RoomDatabase
{
    private static PackageDataBase instance;

    public abstract DAO PackagesDao();

    public static synchronized PackageDataBase getInstance(Context context)
    {
        if(instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext() , PackageDataBase.class,
                    "package_database").fallbackToDestructiveMigration().build();
        return instance;
    }
}