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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import il.co.yedidia_shmuel.ddb_2.R;
import il.co.yedidia_shmuel.ddb_2.controller.activities.UserProfileActivity;
import il.co.yedidia_shmuel.ddb_2.model.dataSource.DataSource;
import il.co.yedidia_shmuel.ddb_2.model.dataSourceFactory.DataSourceFactory;
import il.co.yedidia_shmuel.ddb_2.model.entities.Person;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsersFragment extends Fragment {
    private DataSource _database = DataSourceFactory.getDataBase();
    private RecyclerView _usersRecyclerView;
    private String _currentUserMail;
    private Map<String,Person> _allUsers;
    private Map<String,String> _myFriends;
    private List<Person> _usersList;

    public AllUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_users, container, false);

        _usersList = new ArrayList<>();
        _usersRecyclerView = v.findViewById(R.id.allUsers);
        _usersRecyclerView.setHasFixedSize(true);
        _usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        _currentUserMail = Objects.requireNonNull(requireActivity().getIntent().getExtras()).getString("mail");

        _database.getPerson(_currentUserMail, new DataSource.Action<Person>() {
            @Override
            public void onSuccess(Person obj) throws InterruptedException {
                _myFriends = obj.get_friendsList();
                _database.stopNotifyToUsersList();
                _database.notifyToUserList(new DataSource.NotifyDataChange<HashMap<String, Person>>() {
                    @Override
                    public void onDataChange(HashMap<String, Person> obj) {
                        _allUsers = obj;
                        createAdapter();
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
        return v;
    }

    private void createAdapter() {
        for (String s : _allUsers.keySet()) {
            if (!(_myFriends.containsKey(s.replace(".","|"))) && !s.trim().equals(_currentUserMail.trim())) {
                if (_usersList.stream().noneMatch(o -> o.get_mail().trim().equals(s.trim()))) {
                    _usersList.add(_allUsers.get(s));
                    if (_usersRecyclerView.getAdapter() == null) {
                        _usersRecyclerView.setAdapter(new AllUsersFragment.UsersRecycleViewAdapter());
                    } else {
                        _usersRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public class UsersRecycleViewAdapter extends RecyclerView.Adapter<AllUsersFragment.UsersRecycleViewAdapter.UsersViewHolder> {
        @NonNull
        @Override
        public AllUsersFragment.UsersRecycleViewAdapter.UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.all_users_layout, parent, false);
            return new AllUsersFragment.UsersRecycleViewAdapter.UsersViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final AllUsersFragment.UsersRecycleViewAdapter.UsersViewHolder holder, int position) {
            final Person friend = _usersList.get(position);
            String fullName = friend.get_fName() + " " + friend.get_lName();
            holder.friendName.setText(fullName);
            holder.friendMail.setText(friend.get_mail());


            if(friend.get_friendsList().containsKey(_currentUserMail.replace(".","|"))){
                holder.unRequest.setVisibility(View.VISIBLE);
                holder.requestFriend.setVisibility(View.INVISIBLE);
            }else{
                holder.unRequest.setVisibility(View.INVISIBLE);
                holder.requestFriend.setVisibility(View.VISIBLE);
            }

            holder.requestFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friend.requestFriend(_currentUserMail);
                    _database.addPerson(friend, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {
                            holder.requestFriend.setVisibility(View.INVISIBLE);
                            holder.unRequest.setVisibility(View.VISIBLE);
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


            holder.unRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friend.unRequestFriend(_currentUserMail);
                    _database.addPerson(friend, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {
                            holder.requestFriend.setVisibility(View.VISIBLE);
                            holder.unRequest.setVisibility(View.INVISIBLE);
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
            return _usersList.size();
        }

        public class UsersViewHolder extends RecyclerView.ViewHolder {
            TextView friendMail;
            TextView friendName;
            Button requestFriend,unRequest;


            UsersViewHolder(View itemView) {
                super(itemView);
                friendMail = itemView.findViewById(R.id.friendMail);
                friendName = itemView.findViewById(R.id.friendName);
                requestFriend = itemView.findViewById(R.id.requestFriend);
                unRequest = itemView.findViewById(R.id.unRequest);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserProfileActivity) requireActivity()).setActionBarTitle("כל המשתמשים");
    }
}
