package il.co.yedidia_shmuel.ddb_2.controller.fragments.packagesICanCollect;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.MyAddress;
import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;
import il.co.yedidia_shmuel.ddb_2.model.entities.Status;


public class PackagesICanCollectFragment extends Fragment {

    private PackagesICanCollectViewModel packagesICanCollectViewModel;

    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private HashMap<String,Package> _allPackages;

    private RecyclerView _relevantPackagesRecyclerView;
    private Person _currentUser;
    private String _currentUserMail;
    private Calendar _now;
    private String _timeOfCollect;

    private ArrayList<Package> _relevantPackages;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        packagesICanCollectViewModel = ViewModelProviders.of(this).get(PackagesICanCollectViewModel.class);
        View root = inflater.inflate(R.layout.package_i_can_collect_fragment, container, false);
        _now = Calendar.getInstance();
        int year = _now.get(Calendar.YEAR);
        int month = _now.get(Calendar.MONTH) + 1;
        int day = _now.get(Calendar.DAY_OF_MONTH);
        int hour = _now.get(Calendar.HOUR_OF_DAY);
        int minute = _now.get(Calendar.MINUTE);
        _timeOfCollect = String.valueOf(year) + "/" + String.valueOf(month) + "/" + String.valueOf(day)
                + " " + String.valueOf(hour) + ":" + String.valueOf(minute);

        _relevantPackagesRecyclerView = root.findViewById(R.id.packagesICanCollect);
        _relevantPackagesRecyclerView.setHasFixedSize(true);
        _relevantPackagesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        _relevantPackages = new ArrayList<>();

        _currentUserMail = Objects.requireNonNull(requireActivity().getIntent().getExtras()).getString("mail");

        _dataBase.getPerson(_currentUserMail, new DataSource.Action<Person>() {
            @Override
            public void onSuccess(Person obj) throws InterruptedException {
                _currentUser = obj;
                _dataBase.stopNotifyToPackageList();
                _dataBase.notifyToPackageList(new DataSource.NotifyDataChange<HashMap<String, Package>>() {
                    @Override
                    public void onDataChange(HashMap<String,Package> obj) {
                        _allPackages = obj;
                        getRelevantPackages();
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });
            }

            @Override
            public void onFailure(Exception exception) {

            }

            @Override
            public void onProgress(String status, double percent) {

            }
        });
        return root;
    }

    private void getRelevantPackages() {
        MyAddress _currentUserAddress = _currentUser.get_address();
        int _currentUserMaxDistance = Integer.parseInt(_currentUser.get_maxDistanceToTakePackage());

        for (final Package pkg : _allPackages.values()) {
            if ((distance(pkg.get_recipientAddress(), _currentUserAddress) <= _currentUserMaxDistance) &&
                    (pkg.get_status().equals(Status.REGISTERED) || pkg.get_status().equals(Status.COLLECTION_OFFERED) ||
                            pkg.get_status().equals(Status.COLLECTION_APPROVAL))) {
                if (Objects.equals(_currentUser.get_friendsList().get(pkg.get_recipientMail().replace(".", "|")), "T") ||
                        pkg.get_recipientMail().equals(_currentUserMail)) {
                    if(_relevantPackages.stream().noneMatch(o -> o.get_packageID().equals(pkg.get_packageID()))){
                        _relevantPackages.add(pkg);
                        if (_relevantPackagesRecyclerView.getAdapter() == null) {
                            _relevantPackagesRecyclerView.setAdapter(new RelevantPackageRecycleViewAdapter());
                        } else {
                            _relevantPackagesRecyclerView.getAdapter().notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    private double distance(MyAddress a, MyAddress b) {
        double lat1 = a.get_latitude();
        double lon1 = a.get_longitude();
        double lat2 = b.get_latitude();
        double lon2 = b.get_longitude();
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return dist;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public class RelevantPackageRecycleViewAdapter extends RecyclerView.Adapter<RelevantPackageRecycleViewAdapter.RelevantPackageViewHolder> {
        @NonNull
        @Override
        public RelevantPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.packages_i_can_collect_layout, parent, false);
            return new RelevantPackageViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final RelevantPackageViewHolder holder, int position) {
            final Package p = _relevantPackages.get(position);
            holder.packageNumber.setText("חבילה מספר: " + (position + 1));
            String fullName = p.get_recipientFName() + " " + p.get_recipientLName();
            holder.addresseeName.setText(fullName);
            holder.addresseeMail.setText(p.get_recipientMail());
            holder.addresseeAddress.setText(p.get_recipientAddress().get_address());


            if (p.get_recipientMail().trim().equals(_currentUserMail.trim())) {
                holder.mySwitch.setVisibility(View.INVISIBLE);
                holder.iCollectThePackage.setVisibility(View.VISIBLE);
            } else {
                if (p.get_offerToPickUpThePackage().containsKey(_currentUserMail.replace(".", "|"))) {
                    if (Objects.equals(p.get_offerToPickUpThePackage().get(_currentUserMail.replace(".", "|")), "N")) {
                        holder.mySwitch.setVisibility(View.VISIBLE);
                        holder.iCollectThePackage.setVisibility(View.INVISIBLE);
                        holder.unCollectThePackage.setVisibility(View.INVISIBLE);
                        holder.mySwitch.setChecked(true);
                    } else if (Objects.equals(p.get_offerToPickUpThePackage().get((_currentUserMail.replace(".", "|"))), "T")) {
                        holder.collectApproval.setVisibility(View.VISIBLE);
                        holder.collectApproval.setTextColor(getResources().getColor(R.color.green));
                        holder.collectApproval.setText("איסוף אושר");
                        if(p.get_mailOfDeliver() == null || p.get_mailOfDeliver().equals("")){
                            holder.iCollectThePackage.setVisibility(View.VISIBLE);
                            holder.unCollectThePackage.setVisibility(View.INVISIBLE);
                        }else {
                            holder.iCollectThePackage.setVisibility(View.INVISIBLE);
                            holder.unCollectThePackage.setVisibility(View.VISIBLE);
                        }
                        holder.iCollectThePackage.setVisibility(View.VISIBLE);
                        holder.unCollectThePackage.setVisibility(View.INVISIBLE);
                    } else if (Objects.equals(p.get_offerToPickUpThePackage().get((_currentUserMail.replace(".", "|"))), "F")) {
                        holder.collectApproval.setVisibility(View.VISIBLE);
                        holder.collectApproval.setTextColor(getResources().getColor(R.color.redColor));
                        holder.collectApproval.setText("איסוף לא אושר");
                        holder.iCollectThePackage.setVisibility(View.INVISIBLE);
                        holder.unCollectThePackage.setVisibility(View.INVISIBLE);
                    }
                } else {
                    holder.mySwitch.setVisibility(View.VISIBLE);
                    holder.iCollectThePackage.setVisibility(View.INVISIBLE);
                    holder.mySwitch.setChecked(false);
                }
            }

            holder.mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        p.insertNewOffer(_currentUserMail);
                        _dataBase.addPackage(p, new DataSource.Action<Package>() {
                            @Override
                            public void onSuccess(Package obj) throws InterruptedException {

                            }

                            @Override
                            public void onFailure(Exception exception) {

                            }

                            @Override
                            public void onProgress(String status, double percent) {

                            }
                        });
                    } else {
                        p.removeOffer(_currentUserMail);
                        _dataBase.addPackage(p, new DataSource.Action<Package>() {
                            @Override
                            public void onSuccess(Package obj) throws InterruptedException {

                            }

                            @Override
                            public void onFailure(Exception exception) {

                            }

                            @Override
                            public void onProgress(String status, double percent) {

                            }
                        });
                    }
                }
            });

            holder.iCollectThePackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.iCollectThePackage.setVisibility(View.INVISIBLE);
                    holder.unCollectThePackage.setVisibility(View.VISIBLE);
                    p.set_dayOfCollect(_timeOfCollect);
                    p.set_mailOfDeliver(_currentUserMail);
                    if(_currentUserMail.trim().equals(p.get_recipientMail().trim())){
                        p.set_isDelivered(true);
                    }
                    _dataBase.addPackage(p, new DataSource.Action<Package>() {
                        @Override
                        public void onSuccess(Package obj) throws InterruptedException {

                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }

                        @Override
                        public void onProgress(String status, double percent) {

                        }
                    });
                }
            });

            holder.unCollectThePackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.iCollectThePackage.setVisibility(View.VISIBLE);
                    holder.unCollectThePackage.setVisibility(View.INVISIBLE);
                    p.set_isDelivered(false);
                    p.set_dayOfCollect("");
                    p.set_mailOfDeliver("");
                    _dataBase.addPackage(p, new DataSource.Action<Package>() {
                        @Override
                        public void onSuccess(Package obj) throws InterruptedException {

                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }

                        @Override
                        public void onProgress(String status, double percent) {

                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return _relevantPackages.size();
        }

        public class RelevantPackageViewHolder extends RecyclerView.ViewHolder {
            TextView packageNumber;
            TextView addresseeName;
            TextView addresseeMail;
            TextView addresseeAddress;
            TextView collectApproval;
            Switch mySwitch;
            CardView packageCard;
            Button iCollectThePackage;
            Button unCollectThePackage;

            RelevantPackageViewHolder(View itemView) {
                super(itemView);
                addresseeName = itemView.findViewById(R.id.addresseeName);
                addresseeMail = itemView.findViewById(R.id.addresseeMail);
                addresseeAddress = itemView.findViewById(R.id.addresseeAddress);
                mySwitch = itemView.findViewById(R.id.confirmToPickUpPackage);
                packageCard = itemView.findViewById(R.id.iCanCollectCard);
                packageNumber = itemView.findViewById(R.id.packageNumber);
                collectApproval = itemView.findViewById(R.id.collectionApproval);
                iCollectThePackage = itemView.findViewById(R.id.iCollectButton);
                unCollectThePackage = itemView.findViewById(R.id.unCollectButton);
            }
        }
    }
}
