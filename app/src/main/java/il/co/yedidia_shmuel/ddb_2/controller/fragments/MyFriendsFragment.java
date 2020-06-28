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
public class MyFriendsFragment extends Fragment {
    private DataSource _database = DataSourceFactory.getDataBase();
    private RecyclerView _friendsRecyclerView;
    private Map<String,String> _myFriends;
    private List<Person> _friendsList;
    private String _currentUserMail;
    private Person _currentUser;

    public MyFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_friends, container, false);


        _friendsRecyclerView = v.findViewById(R.id.myFriends);
        _friendsRecyclerView.setHasFixedSize(true);
        _friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        _friendsList = new ArrayList<>();

        _currentUserMail = Objects.requireNonNull(requireActivity().getIntent().getExtras()).getString("mail");

        _database.getPerson(_currentUserMail, new DataSource.Action<Person>() {
            @Override
            public void onSuccess(Person obj) throws InterruptedException {
                _currentUser = obj;
                _myFriends = obj.get_friendsList();
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
        for(String s : _myFriends.keySet()){
            if(Objects.equals(_myFriends.get(s), "T")){
                if(_friendsList.stream().noneMatch(o -> o.get_key().trim().equals(s.trim()))){
                    _database.getPerson(s, new DataSource.Action<Person>() {
                        @Override
                        public void onSuccess(Person obj) throws InterruptedException {
                            if(obj != null) {
                                _friendsList.add(obj);
                                if (_friendsRecyclerView.getAdapter() == null) {
                                    _friendsRecyclerView.setAdapter(new FriendsRecycleViewAdapter());
                                } else {
                                    _friendsRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            }else {
                                _myFriends.remove(s);
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
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            _friendsList.removeIf(o -> o.get_key().trim().equals(s.trim()));
                        }

                        @Override
                        public void onProgress(String status, double percent) {

                        }
                    });
                }
            }
        }

    }


    public class FriendsRecycleViewAdapter extends RecyclerView.Adapter<FriendsRecycleViewAdapter.FriendsViewHolder> {
        @NonNull
        @Override
        public FriendsRecycleViewAdapter.FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.my_friend_layout, parent, false);
            return new FriendsRecycleViewAdapter.FriendsViewHolder(v);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull final FriendsRecycleViewAdapter.FriendsViewHolder holder, int position) {
            final Person friend = _friendsList.get(position);
            String fullName = friend.get_fName() + " " + friend.get_lName();
            holder.friendName.setText(fullName);
            holder.friendMail.setText(friend.get_mail());
            holder.unFriend.setEnabled(true);


            holder.unFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.unFriend.setEnabled(false);
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

                    _currentUser.unFriend(friend.get_mail());
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
        }

        @Override
        public int getItemCount() {
            return _friendsList.size();
        }

        public class FriendsViewHolder extends RecyclerView.ViewHolder {
            TextView friendMail;
            TextView friendName;
            Button unFriend;

            FriendsViewHolder(View itemView) {
                super(itemView);
                friendMail = itemView.findViewById(R.id.friendMail);
                friendName = itemView.findViewById(R.id.friendName);
                unFriend = itemView.findViewById(R.id.unFriend);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserProfileActivity) requireActivity()).setActionBarTitle("החברים שלי");
    }
}
