package il.co.yedidia_shmuel.ddb_2.model.dataSource;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import il.co.yedidia_shmuel.ddb_2.model.entities.Package;

@Dao
public interface DAO {
    @Insert
    void insert(Package parcel);

    @Update
    void update(Package parcel);

    @Delete
    void delete(Package parcel);

    @Query("DELETE FROM package_table")
    void deleteAllPackages();

    @Query("SELECT * FROM package_table")
    LiveData<List<Package>> getAllPackages();
}
