package il.co.yedidia_shmuel.ddb_2.model.dataSource;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import il.co.yedidia_shmuel.ddb_2.model.entities.Package;

public class PackageRepository {
    private DAO _packageDAO;
    private LiveData<List<Package>> _allPackages;

    public PackageRepository(Application application) {
        PackageDataBase database = PackageDataBase.getInstance(application);
        _packageDAO = database.PackagesDao();
        _allPackages = _packageDAO.getAllPackages();
    }

    public void insert(Package pkg) {
        new InsertPackagesAsyncTask(_packageDAO).execute(pkg);
    }

    public void update(Package pkg){
        new UpdatePackagesAsyncTask(_packageDAO).execute(pkg);
    }

    public void delete(Package pkg){
        new DeletePackagesAsyncTask(_packageDAO).execute(pkg);
    }

    public void deleteAllPackages(){
        new DeleteAllPackagesAsyncTask(_packageDAO).execute();
    }

    public LiveData<List<Package>> get_allPackages(){
        return _allPackages;
    }

    public static class InsertPackagesAsyncTask extends AsyncTask<Package,Void,Void> {
        private DAO packageDAO;
        public InsertPackagesAsyncTask(DAO packagesDao)
        {
            this.packageDAO = packagesDao;
        }
        @Override
        protected Void doInBackground(Package... pkg) {
            packageDAO.insert(pkg[0]);
            return null;
        }
    }

    public static class UpdatePackagesAsyncTask extends AsyncTask<Package,Void,Void> {
        private DAO pkgDao;
        public UpdatePackagesAsyncTask(DAO packagesDao)
        {
            this.pkgDao = packagesDao;
        }
        @Override
        protected Void doInBackground(Package... packages){
            pkgDao.update(packages[0]);
            return null;
        }
    }


    public static class DeletePackagesAsyncTask extends AsyncTask<Package,Void,Void> {
        private DAO pkgDao;
        public DeletePackagesAsyncTask(DAO packagesDao)
        {
            this.pkgDao = packagesDao;
        }
        @Override
        protected Void doInBackground(Package... packages) {
            pkgDao.delete(packages[0]);
            return null;
        }
    }
    public static class DeleteAllPackagesAsyncTask extends AsyncTask<Package,Void,Void> {
        private DAO pkgDao;
        public DeleteAllPackagesAsyncTask(DAO packagesDao)
        {
            this.pkgDao = packagesDao;
        }
        @Override
        protected Void doInBackground(Package... packages) {
            pkgDao.deleteAllPackages();
            return null;
        }
    }
}
