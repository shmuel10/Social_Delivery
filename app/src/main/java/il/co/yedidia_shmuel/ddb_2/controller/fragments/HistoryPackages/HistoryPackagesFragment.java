package il.co.yedidia_shmuel.ddb_2.controller.fragments.HistoryPackages;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.model.entities.Status;

public class HistoryPackagesFragment extends Fragment {

    private HistoryPackagesViewModel _historyPackagesViewModel;
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private RecyclerView _packageRecyclerView;
    private List<Package> _roomPackage;
    private String _currentUserMail;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _historyPackagesViewModel = ViewModelProviders.of(this).get(HistoryPackagesViewModel.class);
        View view = inflater.inflate(R.layout.history_packages_fragment, container, false);
        _packageRecyclerView = view.findViewById(R.id.myHistoryPackages);
        _packageRecyclerView.setHasFixedSize(true);
        _packageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


         _currentUserMail = Objects.requireNonNull(requireActivity().getIntent().getExtras()).getString("mail");

        _historyPackagesViewModel.get_allPackages().observe(this, new Observer<List<Package>>() {
            @Override
            public void onChanged(List<Package> p) {
                // TODO here is the data loading
                _roomPackage = p;
                if (_packageRecyclerView.getAdapter() == null){
                    _packageRecyclerView.setAdapter(new PackagesRecycleViewAdapter());
                } else {
                    _packageRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });

        _dataBase.stopNotifyToPackageList();
        _dataBase.notifyToPackageList(new DataSource.NotifyDataChange<HashMap<String, Package>>() {
            @Override
            public void onDataChange(HashMap<String, Package> obj) {
                _roomPackage = new ArrayList<Package>();
                if(obj != null ) {
                    for (Package p : obj.values()) {
                        if (p.get_recipientMail().equals(_currentUserMail) && ((p.get_status().equals(Status.ON_THE_WAY)) || (p.get_status().equals(Status.DELIVERED)))) {
                            _roomPackage.add(p);
                        }
                    }
                }
                _historyPackagesViewModel.deleteAllNotes();
                for(Package p: _roomPackage)
                    _historyPackagesViewModel.insert(p);
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }


    public class PackagesRecycleViewAdapter extends RecyclerView.Adapter<PackageViewHolder> {
        @NonNull
        @Override
        public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.history_package_layout, parent,false);
            return new PackageViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final PackageViewHolder holder, int position) {
            final Package pkg = _roomPackage.get(position);

            if (pkg.get_mailOfDeliver() != null) {
                holder.deliverMail.setText(pkg.get_mailOfDeliver());
                holder.packageKey.setText(pkg.get_packageID());
                holder.packageStatus.setTextColor(getResources().getColor(R.color.green));
                holder.packageStatus.setText(pkg.get_status().toString());
                holder.packageNumber.setText("חבילה מספר: " + String.valueOf(position + 1));
                holder.collectDay.setText(pkg.get_dayOfCollect());
                if(pkg.get_status().equals(Status.ON_THE_WAY)){
                    holder.iGotPackage.setEnabled(true);
                    holder.iGotPackage.setVisibility(View.VISIBLE);
                }
            }

            holder.iGotPackage.setOnClickListener(v -> {
                pkg.set_status(Status.DELIVERED);
                pkg.set_isDelivered(true);
                _dataBase.addPackage(pkg, new DataSource.Action<Package>() {
                    @Override
                    public void onSuccess(Package obj) throws InterruptedException {
                        holder.iGotPackage.setEnabled(false);
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }

                    @Override
                    public void onProgress(String status, double percent) {

                    }
                });
            });
        }

        @Override
        public int getItemCount()
        {
            return _roomPackage.size();
        }
    }

    public class PackageViewHolder extends RecyclerView.ViewHolder {
        TextView packageNumber;
        TextView packageStatus;
        TextView deliverMail;
        TextView packageKey;
        TextView collectDay;
        CardView packageCard;
        Button iGotPackage;

        PackageViewHolder(View itemView)
        {
            super(itemView);
            packageNumber = itemView.findViewById(R.id.packageNumber);
            deliverMail = itemView.findViewById(R.id.deliverMail);
            packageKey = itemView.findViewById(R.id.packageKey);
            packageStatus = itemView.findViewById(R.id.packageStatus);
            packageCard = itemView.findViewById(R.id.packageCard);
            collectDay = itemView.findViewById(R.id.collectDay);
            iGotPackage = itemView.findViewById(R.id.iGotPackage);
        }
    }
}
