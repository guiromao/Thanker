package co.thanker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import co.thanker.R;

public class ThankFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_thank, container, false);



        return view;
    }

    @Override
    public void onPause(){
        super.onPause();

        Fragment f = (Fragment) getFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (f != null)
            getFragmentManager().beginTransaction().remove(f).commit();
    }

}
