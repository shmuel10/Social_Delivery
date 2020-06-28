package il.co.yedidia_shmuel.ddb_2.controller.fragments.HistoryPackages;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import il.co.yedidia_shmuel.ddb_2.model.dataSource.PackageRepository;
import il.co.yedidia_shmuel.ddb_2.model.entities.Package;

public class HistoryPackagesViewModel extends AndroidViewModel {

    private PackageRepository _packageRepository;
    private LiveData<List<Package>> _allPackages;
    public HistoryPackagesViewModel(@NonNull Application application) {
        super(application);
        _packageRepository = new PackageRepository(application);
        _allPackages = _packageRepository.get_allPackages();
    }

    public void insert(Package pkg) {_packageRepository.insert(pkg);}
    public void update(Package pkg) {_packageRepository.update(pkg);}
    public void delete(Package pkg) {_packageRepository.delete(pkg);}
    public void deleteAllNotes() { _packageRepository.deleteAllPackages();}
    public LiveData<List<Package>> get_allPackages() { return _allPackages; }
}