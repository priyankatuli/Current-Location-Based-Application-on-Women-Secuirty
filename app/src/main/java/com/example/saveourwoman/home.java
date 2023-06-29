package com.example.saveourwoman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class home extends Fragment implements View.OnClickListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private CardView FirstId,SecondId,ThirdId,FourthId,FifthId, DefenseTricksView, PoliceStationView;
    public home() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    public static home newInstance(String param1, String param2) {
        home fragment = new home();
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
        // Inflate the layout for this fragment
        // Normal findViewById won't work here, that's why we used created view to call view.findViewById
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        FirstId=(CardView) view.findViewById(R.id.FirstCardId);
        SecondId=(CardView) view.findViewById(R.id.SecondCardId);
        ThirdId=(CardView) view.findViewById(R.id.ThirdCardId);
        FourthId=(CardView) view.findViewById(R.id.FouthCardId);
        FifthId=(CardView) view.findViewById(R.id.FifthCardId);
        PoliceStationView = view.findViewById(R.id.SixthCardId);
        DefenseTricksView = (CardView) view.findViewById(R.id.defenseTricksCardId);

        FirstId.setOnClickListener(this);
        SecondId.setOnClickListener(this);
        ThirdId.setOnClickListener(this);
        FourthId.setOnClickListener(this);
        FifthId.setOnClickListener(this);
        PoliceStationView.setOnClickListener(this);
        DefenseTricksView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if(view.getId()==R.id.FirstCardId){
            Intent intent=new Intent(getContext(), SirenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if(view.getId()==R.id.FouthCardId){
            Intent intent=new Intent(getContext(), MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        else if (view.getId() == R.id.ThirdCardId)
        {
            Intent intent=new Intent(getContext(), MessageSendingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if (view.getId()==R.id.FifthCardId) {
            Intent intent=new Intent(getContext(), ShareLocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        else if (view.getId() == R.id.SixthCardId) {
            Intent intent = new Intent(getContext(), NearbyPoliceStationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
        else if (view.getId() == R.id.defenseTricksCardId) {
            Intent intent = new Intent(getContext(), SelfDefenseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else if (view.getId() == R.id.SecondCardId) {
            Intent intent = new Intent(getContext(), EmergencyCallingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}