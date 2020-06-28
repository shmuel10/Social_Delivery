package il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory;

import il.co.yedidia_shmuel.ddb_2.model.authentication.AuthService;
import il.co.yedidia_shmuel.ddb_2.model.authentication.FirebaseAuth;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.FireBase;

public class DataSourceFactory {
    public static DataSource getDataBase() {
        return new FireBase();
    }

    public static AuthService getAuthService() {
        return new FirebaseAuth();
    }

}
