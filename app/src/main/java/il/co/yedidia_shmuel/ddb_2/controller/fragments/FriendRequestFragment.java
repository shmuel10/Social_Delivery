package il.co.yedidia_shmuel.ddb_2.controller.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.controller.activities.UserProfileActivity;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendRequestFragment extends Fragment {

    private DataSource _database = DataSourceFactory.getDataBase();
    private Person _currentUser;
    private String _currentUserMail;
    private RecyclerView _friendRecyclerView;
    private Map<String,String> _myFriend;
    private List<Person> _relevantUsersList;

    public FriendRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friend_request, container, false);

        _friendRecyclerView = v.findViewById(R.id.friendRequest);
        _friendRecyclerView.setHasFixedSize(true);
        _friendRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        _relevantUsersList = new ArrayList<>();

        _currentUserMail = Objects.requireNonNull(requireActivity().getIntent().getExtras()).getString("mail");

        _database.getPerson(_currentUserMail, new DataSource.Action<Person>() {
            @Override
            public void onSuccess(Person obj) throws InterruptedException {
                _currentUser = obj;
                _myFriend = obj.get_friendsList();
                createAdapter();
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

    private void createAdapter() {
        for(final String s : _myFriend.keySet()){
            if(Objects.equals(_myFriend.get(s), "N")){
                if(_relevantUsersList.stream().noneMatch(o -> o.get_key().trim().equals(s.trim()))){
                    _database.getPerson(s, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {
                            if(obj != null){
                                _relevantUsersList.add(obj);
                                if (_friendRecyclerView.getAdapter() == null) {
                                    _friendRecyclerView.setAdapter(new FriendRequestRecycleViewAdapter());
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


    public class FriendRequestRecycleViewAdapter extends RecyclerView.Adapter<FriendRequestFragment.FriendRequestRecycleViewAdapter.FriendRequestViewHolder> {
        @NonNull
        @Override
        public FriendRequestFragment.FriendRequestRecycleViewAdapter.FriendRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.friend_request_layout, parent, false);
            return new FriendRequestFragment.FriendRequestRecycleViewAdapter.FriendRequestViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final FriendRequestFragment.FriendRequestRecycleViewAdapter.FriendRequestViewHolder holder, int position) {
            final Person friend = _relevantUsersList.get(position);
            final String fullName = friend.get_fName() + " " + friend.get_lName();
            holder.friendName.setText(fullName);
            holder.friendMail.setText(friend.get_mail());


            holder.confirmRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rejectRequest.setEnabled(false);
                    holder.confirmRequest.setEnabled(false);
                    friend.confirmFriend(_currentUserMail);
                    _database.addPerson(friend, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {

                        }

                        @Override
                        public void onFailure(Exception exception) {

                        }

                        @Override
                        public void onProgress(String status, double percent) {

                        }
                    });

                    _currentUser.confirmFriend(friend.get_mail());
                    _database.addPerson(_currentUser, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {

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

            holder.rejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.rejectRequest.setEnabled(false);
                    holder.confirmRequest.setEnabled(false);
                    friend.unFriend(_currentUserMail);
                    _database.addPerson(friend, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {

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
            return _relevantUsersList.size();
        }

        public class FriendRequestViewHolder extends RecyclerView.ViewHolder {
            TextView friendMail;
            TextView friendName;
            Button rejectRequest,confirmRequest;


            FriendRequestViewHolder(View itemView) {
                super(itemView);
                friendMail = itemView.findViewById(R.id.friendMail);
                friendName = itemView.findViewById(R.id.friendName);
                confirmRequest = itemView.findViewById(R.id.confirmFriend);
                rejectRequest = itemView.findViewById(R.id.rejectRequest);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserProfileActivity) requireActivity()).setActionBarTitle("בקשות חברות");
    }
}
