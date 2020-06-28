package il.co.yedidia_shmuel.ddb_2.controller.fragments.AllPackages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.controller.fragments.OfferPickUpThePackageFragment;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.model.entities.Status;

public class AllPackagesFragment extends Fragment {
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private AllPackagesViewModel _allPackagesViewModel;
    private RecyclerView _packageRecyclerView;
    private List<Package> _roomPackage;
    private String _currentUserMail;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _allPackagesViewModel = ViewModelProviders.of(this).get(AllPackagesViewModel.class);
        View view = inflater.inflate(R.layout.all_packages_fragment, container, false);

        _packageRecyclerView = view.findViewById(R.id.myPackage);
        _packageRecyclerView.setHasFixedSize(true);
        _packageRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        _currentUserMail = Objects.requireNonNull(requireActivity().getIntent().getExtras()).getString("mail");

        _dataBase.stopNotifyToPackageList();
        _dataBase.notifyToPackageList(new DataSource.NotifyDataChange<HashMap<String, Package>>() {
            @Override
            public void onDataChange(HashMap<String, Package> obj) {
                _roomPackage = new ArrayList<Package>();
                if (obj != null) {
                    for (Package p : obj.values()) {
                        if (p.get_recipientMail().equals(_currentUserMail) && (p.get_status() != Status.DELIVERED) && (p.get_status() != Status.ON_THE_WAY)) {
                            _roomPackage.add(p);
                        }
                    }
                }
                _allPackagesViewModel.deleteAllNotes();
                for (Package p : _roomPackage)
                    _allPackagesViewModel.insert(p);
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getContext(), "error to get parcel list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        _allPackagesViewModel.get_allPackages().observe(this, new Observer<List<Package>>() {
            @Override
            public void onChanged(List<Package> p) {
                // TODO here is the data loading
                _roomPackage = p;
                if (_packageRecyclerView.getAdapter() == null) {
                    _packageRecyclerView.setAdapter(new PackagesRecycleViewAdapter());
                } else {
                    _packageRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        });
        return view;
    }


    public class PackagesRecycleViewAdapter extends RecyclerView.Adapter<PackagesRecycleViewAdapter.PackageViewHolder> {
        @NonNull
        @Override
        public PackageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.pkg_layout, parent, false);
            return new PackageViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull PackageViewHolder holder, int position) {
            final Package pkg = _roomPackage.get(position);
            holder.packageKey.setText(pkg.get_packageID());
            holder.distributionCenterAddress.setText(pkg.get_distributionCenterAddress().get_address());
            if (pkg.get_status().equals(Status.REGISTERED)) {
                holder.packageStatus.setTextColor(getResources().getColor(R.color.redColor));
            } else if (pkg.get_status().equals(Status.COLLECTION_OFFERED)) {
                holder.packageStatus.setTextColor(getResources().getColor(R.color.buttonColor));
            } else if (pkg.get_status().equals(Status.ON_THE_WAY)) {
                holder.packageStatus.setTextColor(getResources().getColor(R.color.green));
            } else if (pkg.get_status().equals(Status.COLLECTION_APPROVAL)) {
                holder.packageStatus.setTextColor(getResources().getColor(R.color.orange));
            }

            holder.packageStatus.setText(pkg.get_status().toString());
            holder.packageNumber.setText("חבילה מספר:" + " " + String.valueOf(position + 1));
            holder.packageCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OfferPickUpThePackageFragment offerPickUpThePackageFragment = new OfferPickUpThePackageFragment();
                    FragmentManager fragmentManage = getFragmentManager();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID", pkg.get_packageID());
                    offerPickUpThePackageFragment.setArguments(bundle);
                    fragmentManage.beginTransaction().replace(R.id.nav_host_fragment, offerPickUpThePackageFragment, "Tag")
                            .addToBackStack(null).commit();
                }
            });

            holder.allDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tmp = pkg.get_isFragile() ? "Yes" : "No";
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Package Details");
                    builder.setMessage(
                                    "Package ID: " + pkg.get_packageID() + "\n" +
                                    "Recipient mail: " + pkg.get_recipientMail() + "\n" +
                                    "Recipient name: " + pkg.get_recipientFName() + " " + pkg.get_recipientLName() + "\n" +
                                    "Package status: " + pkg.get_status() + "\n" +
                                    "Package type: " + pkg.get_type() + "\n" +
                                    "Is fragile: " + tmp + "\n" +
                                    "Package weight: " + pkg.get_weight() + "\n");
                    builder.setCancelable(true);
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return _roomPackage.size();
        }

        public class PackageViewHolder extends RecyclerView.ViewHolder {
            TextView packageNumber;
            TextView packageKey;
            TextView distributionCenterAddress;
            TextView packageStatus;
            Button allDetails;
            CardView packageCard;

            PackageViewHolder(View itemView) {
                super(itemView);
                packageKey = itemView.findViewById(R.id.packageKey);
                distributionCenterAddress = itemView.findViewById(R.id.distributionCenterAddress);
                packageStatus = itemView.findViewById(R.id.packageStatus);
                packageCard = itemView.findViewById(R.id.packageCard);
                packageNumber = itemView.findViewById(R.id.packageNumber);
                allDetails = itemView.findViewById(R.id.moreDetails);
            }
        }
    }
}
