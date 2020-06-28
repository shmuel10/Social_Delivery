package il.co.yedidia_shmuel.ddb_2.controller.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.controller.activities.UserProfileActivity;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Package;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferPickUpThePackageFragment extends Fragment {
    private DataSource _dataBase = DataSourceFactory.getDataBase();
    private RecyclerView _friendRecyclerView;
    private Package _package;
    private Map<String, String> _mailsOfFriendOfferToTakePackages;
    private ArrayList<Person> _friendsList;
    private String _currentUserMail;
    private String _currentPackageID;

    public OfferPickUpThePackageFragment() { /*Required empty public constructor*/ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_offer_pick_up_the_package, container, false);

        _friendRecyclerView = v.findViewById(R.id.packagesFriendsList);
        _friendRecyclerView.setHasFixedSize(true);
        _friendRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        _currentUserMail = getActivity().getIntent().getExtras().getString("mail");
        _friendsList = new ArrayList<>();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _currentPackageID = bundle.getString("ID");
        }

        _dataBase.getPackage(_currentPackageID, new DataSource.Action<Package>() {
            @Override
            public void onSuccess(Package obj) throws InterruptedException {
                _package = obj;
                _mailsOfFriendOfferToTakePackages = obj.get_offerToPickUpThePackage();
                setAdapter();
            }

            @Override
            public void onFailure(Exception exception) {

            }

            @Override
            public void onProgress(String status, double percent) {

            }
        });
        return v;
    }

    private void setAdapter() {
        for (String s : _mailsOfFriendOfferToTakePackages.keySet()) {
            if (!s.trim().equals(_currentUserMail.replace(".", "|"))) {
                if (_friendsList.stream().noneMatch(o -> o.get_key().trim().equals(s))) {
                    _dataBase.getPerson(s.replace("|", "."), new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {
                            if (obj != null) {
                                _friendsList.add(obj);
                                if (_friendRecyclerView.getAdapter() == null) {
                                    _friendRecyclerView.setAdapter(new OfferPickUpThePackageFragment.FriendRecycleViewAdapter());
                                } else {
                                    _friendRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }
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
        }
    }

    public class FriendRecycleViewAdapter extends RecyclerView.Adapter<FriendRecycleViewAdapter.FriendViewHolder> {
        @NonNull
        @Override
        public FriendRecycleViewAdapter.FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.offer_to_take_my_package_layout, parent, false);
            return new FriendRecycleViewAdapter.FriendViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final FriendRecycleViewAdapter.FriendViewHolder holder, int position) {
            final Person person = _friendsList.get(position);

            if (_mailsOfFriendOfferToTakePackages.get(person.get_mail().replace(".", "|")).equals("T")) {
                holder.mySwitch.setOnCheckedChangeListener(null);
                holder.mySwitch.setChecked(true);
            }

            holder.takerName.setText(person.get_fName() + " " + person.get_lName());
            holder.takerMail.setText(person.get_mail());
            holder.takerAddress.setText(person.get_address().get_address());

            holder.mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        if (!_package.get_offerToPickUpThePackage().containsValue("T")) {
                            _package.confirmOffer(person.get_mail());
                            _dataBase.addPackage(_package, new DataSource.Action<Package>() {
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("ERROR");
                            builder.setMessage("A deliver has already been selected" + "\n" +
                                    "You can remove the existing deliver and then select a new one");
                            builder.setIcon(R.mipmap.ic_question);
                            builder.setCancelable(true);
                            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    holder.mySwitch.setChecked(false);
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        _package.unConfirmOffer(person.get_mail());
                        _dataBase.addPackage(_package, new DataSource.Action<Package>() {
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
        }

        @Override
        public int getItemCount() {
            return _friendsList.size();
        }

        public class FriendViewHolder extends RecyclerView.ViewHolder {
            TextView takerName;
            TextView takerMail;
            TextView takerAddress;
            Switch mySwitch;
            RelativeLayout layout;


            FriendViewHolder(View itemView) {
                super(itemView);
                takerName = itemView.findViewById(R.id.takerName);
                takerMail = itemView.findViewById(R.id.takerMail);
                takerAddress = itemView.findViewById(R.id.takerAddress);
                mySwitch = itemView.findViewById(R.id.confirmPickUpPackage);
                layout = itemView.findViewById(R.id.packageLayout);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserProfileActivity) getActivity()).setActionBarTitle("הציעו לקחת את החבילה שלך");
    }
}
