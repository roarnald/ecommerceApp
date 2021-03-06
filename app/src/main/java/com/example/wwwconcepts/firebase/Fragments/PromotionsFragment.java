package com.example.wwwconcepts.firebase.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wwwconcepts.firebase.POJOs.Promo;
import com.example.wwwconcepts.firebase.POJOs.PromoList;
import com.example.wwwconcepts.firebase.POJOs.User;
import com.example.wwwconcepts.firebase.PromoCfmActivity;
import com.example.wwwconcepts.firebase.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PromotionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PromotionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PromotionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView introTextView, allPointsTextView;
    private ListView promoListView;

    private DatabaseReference userReference;
    private FirebaseAuth auth;
    private List<Promo> promos;

    private DatabaseReference promoReference;

    public PromotionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PromotionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PromotionsFragment newInstance(String param1, String param2) {
        PromotionsFragment fragment = new PromotionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =inflater.inflate(R.layout.fragment_promotions, container, false);
        // Inflate the layout for this fragment

        introTextView = (TextView) view.findViewById(R.id.introTextView);
        allPointsTextView = (TextView) view.findViewById(R.id.allPointsTextView);
        promoListView = (ListView) view.findViewById(R.id.promoListView);

        //admin add promo func
        final EditText addNameEditText = (EditText) view.findViewById(R.id.addNameEditText);
        final EditText addPointsEditText = (EditText) view.findViewById(R.id.addPointsEditText);
        final Button addPromoBtn = (Button) view.findViewById(R.id.addPromoBtn);

        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        introTextView.setText("Hey "+ auth.getCurrentUser().getDisplayName()+",");

        userReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                allPointsTextView.setText("Points: "+user.getPoints());
                if(user.getAdmin()==true){
                    addNameEditText.setVisibility(View.VISIBLE);
                    addPointsEditText.setVisibility(View.VISIBLE);
                    addPromoBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        promos = new ArrayList<>();
        promoReference = FirebaseDatabase.getInstance().getReference("promos");
        promoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                promos.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    final Promo promo = postSnapshot.getValue(Promo.class);
                    promos.add(promo);
                }
                final PromoList promoAdapter = new PromoList(getActivity(), promos);
                promoListView.setAdapter(promoAdapter);
                //onclick listener
                promoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Promo promo = promoAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), PromoCfmActivity.class);
                        Bundle args = new Bundle();
                        args.putString("promoId", promo.getPromoId());
                        intent.putExtras(args);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        //TODO: for admin only
        addPromoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String promoId = promoReference.push().getKey();
                Promo promo = new Promo(addNameEditText.getText().toString(), Integer.valueOf(addPointsEditText.getText().toString()), promoId);
                promoReference.child(promoId).setValue(promo);
                addNameEditText.setText("");
                addPointsEditText.setText("");
                Toast.makeText(getActivity().getApplicationContext(), "Promo Added!", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
