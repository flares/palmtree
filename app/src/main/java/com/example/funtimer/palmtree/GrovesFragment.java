package com.example.funtimer.palmtree;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.funtimer.palmtree.dummy.DummyContent;
import com.example.funtimer.palmtree.dummy.DummyContent.DummyItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Splitter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GrovesFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final  String TAG = "GrovesFragment";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    public MyGrovesRecyclerViewAdapter mAdapter;
    private FirebaseUser user;
    public List<GroveItem> userGrovesList;

    public static class GroveItem
    {
        public final String id;
        public final String content;

        public GroveItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GrovesFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static GrovesFragment newInstance(int columnCount) {
        GrovesFragment fragment = new GrovesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean isAccountClaimed()
    {
        return true;
    }

    private boolean accountClaimApproved()
    {
        return true;
    }
/*
    public void initialSetup()
    {
        SharedPreferences prefs = this.getActivity().getSharedPreferences(MainActivity.PREF_FILENAME, this.getActivity().MODE_PRIVATE);
//
//        if(!isKelutralAccountClaimed())
//        {
//            claimKelutralAccount();
//        }
//
//        if(kelutralAccountClaimApproved())
//        {
//
//        }
        if(isUserAccountCreated())
        {
            createUserAccount();
        }
        else {
            // Write a message to the database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String userid = "uniqueid1";
            DatabaseReference myRef = database.getReference("userDB/" + userid);

            FirebaseAuth.getInstance();
                //myRef.setValue("Hello, World!");

            // Read from the database
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                    //HomeUser value = messageSnapshot.getValue(HomeUser.class);
                    DataSnapshot messageSnapshot = dataSnapshot;

                    TextView nameTextView = (TextView) findViewById(R.id.temp_user_name);
                    TextView dobTextView = (TextView) findViewById(R.id.temp_user_dob);
                    TextView genderTextView = (TextView) findViewById(R.id.temp_user_gender);

                    Toast.makeText(getApplicationContext(), "DB Read value is : " + messageSnapshot.getValue(), Toast.LENGTH_SHORT).show();

                    nameTextView.setText((String) messageSnapshot.child("name").getValue());
                    dobTextView.setText((String) messageSnapshot.child("dob").getValue());
                    genderTextView.setText((String) messageSnapshot.child("gender").getValue());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialSetup();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            Log.d(TAG, "Manoj Details : " + name + email + emailVerified + uid + photoUrl);
        }

        userGrovesList = new ArrayList<GroveItem>();

        FirebaseFirestore store = FirebaseFirestore.getInstance();
        DocumentReference docRef = store.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                        Map<String, Object> details = (Map<String, Object>) document.get("properties");
                        Log.d(TAG, "MAnoj details: " + details);

                        for(Map.Entry<String, Object> entry: details.entrySet()) {
                            Log.d(TAG, "MAnoj details1: " + entry);
                            Log.d(TAG, "MAnoj details1: " + entry.getKey());
                            Log.d(TAG, "MAnoj details1: " + entry.getValue());
                            userGrovesList.add(new GroveItem(entry.getKey(), (String)entry.getValue()));
                        }
                        //mAdapter.updateGrovesList(userGrovesList);
                        //mAdapter.notifyDataSetChanged();
                    }
                    else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        View view = inflater.inflate(R.layout.fragment_groves_list, container, false);

        Map<String, String> tempdetails = Splitter.on(',').withKeyValueSeparator('=').split("{name2=Manoj2 KrishTemp, dob2=221/02/19923, age2=224}");

        for(Map.Entry<String, String> entry: tempdetails.entrySet()) {
            Log.d(TAG, "MAnoj details1: " + entry);
            Log.d(TAG, "MAnoj details1: " + entry.getKey());
            Log.d(TAG, "MAnoj details1: " + entry.getValue());
            userGrovesList.add(new GroveItem(entry.getKey(), (String)entry.getValue()));
        }
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mAdapter = new MyGrovesRecyclerViewAdapter(DummyContent.ITEMS, mListener);
            recyclerView.setAdapter(mAdapter);
        }

        return null;
    }

    @Override
    public void onAttach(Context context) {
        Toast.makeText(getActivity(), "onAttch called", Toast.LENGTH_SHORT).show();
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnListFragmentInteractionListener");
            //Toast.makeText(getApplicationContext(), "onListFrafmentinteractoionlistener", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Manoj onAttach : " + context.toString() + "must implement OnListFragmentInteractionListener");
        }
    }

    public void OnListFragmentInteractionListener()
    {
        //Toast.makeText(getApplicationContext(), "onListFrafmentinteractoionlistener", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
