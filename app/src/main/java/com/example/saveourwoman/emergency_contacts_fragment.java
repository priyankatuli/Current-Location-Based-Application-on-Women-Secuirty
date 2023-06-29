package com.example.saveourwoman;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link emergency_contacts_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class emergency_contacts_fragment extends Fragment implements View.OnClickListener{
    //region variable declaration
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //endregion

    //region method initialization
    public emergency_contacts_fragment() {
        // Required empty public constructor
        super(R.layout.activity_second);
    }

    public static emergency_contacts_fragment newInstance(String param1, String param2) {
        emergency_contacts_fragment fragment = new emergency_contacts_fragment();
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

    //endregion

    // initiate card view item and set listener

    private void initiateCardsListener(View view)
    {
        CardView addContactView = view.findViewById(R.id.addContactId);
        CardView viewContactView = view.findViewById(R.id.viewContactId);
        addContactView.setOnClickListener(this);
        viewContactView.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        initiateCardsListener(view);
        return view;
    }

    private void setFragment(int id, Fragment fragment)
    {
        getParentFragmentManager()
                .beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.addContactId){ // will display Add contact section
            setFragment(R.id.fragmentContainer, new AddContractFragment());
        }
        else if(view.getId() == R.id.viewContactId){ // will display View contact section
            setFragment(R.id.fragmentContainer, new ViewContactFragment());

        }
    }
}