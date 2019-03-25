package ua.pt.eventua.Fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

import ua.pt.eventua.Activities.LoginActivity;
import ua.pt.eventua.Activities.Welcome;
import ua.pt.eventua.R;


public class Settings extends Fragment {

    private TextView tvProgressLabel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View rootView = inflater.inflate(R.layout.activity_settings, container, false);

        SeekBar seekBar = rootView.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // updated continuously as the user slides the thumb
                tvProgressLabel.setText(getResources().getString(R.string.settings_brightness) +" " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // called when the user first touches the SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // called after the user finishes moving the SeekBar
            }
        });

        int progress = seekBar.getProgress();
        tvProgressLabel =  rootView.findViewById(R.id.textView);
        tvProgressLabel.setText(getResources().getString(R.string.settings_brightness) +" " + progress);

        Spinner dropdown = rootView.findViewById(R.id.spinner1);
        String[] items = new String[]{getString(R.string.language_1), getString(R.string.language_2), "Select a Language"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(2);




        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Configuration config;
            Locale locale;


            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Intent restart;
                switch (position)
                {
                    case 0:
                        config = getActivity().getResources().getConfiguration();
                        locale = new Locale("en");
                        Locale.setDefault(locale);
                        config.locale = locale;
                        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

                        restart = getActivity().getIntent();
                        getActivity().finish();
                        startActivity( restart );
                        break;

                    case 1:
                        config = getActivity().getResources().getConfiguration();
                        locale = new Locale("pt");
                        Locale.setDefault(locale);
                        config.locale = locale;
                        getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        restart = getActivity().getIntent();
                        getActivity().finish();
                        startActivity( restart );
                        break;

                    case 2:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        /*
        Configuration config = getActivity().getResources().getConfiguration();
        Locale locale = new Locale("pt");
        Locale.setDefault(locale);
        config.locale = locale;
        getActivity().getResources().updateConfiguration(config,
                getActivity().getResources().getDisplayMetrics());
        */
        return rootView;

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle(getString(R.string.title_settings));
    }
}
