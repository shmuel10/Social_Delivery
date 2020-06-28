package il.co.yedidia_shmuel.ddb_2.model.entities;

public class DistributionCenter {
    private MyAddress _address;

    public DistributionCenter(String storageID, MyAddress address){
        _address = address;
    }

    public DistributionCenter(MyAddress address){
        _address = address;
    }

    public DistributionCenter(){}

    public MyAddress get_address() {
        return _address;
    }

    public void set_address(MyAddress _address) {
        this._address = _address;
    }
}
