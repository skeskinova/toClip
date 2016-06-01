package com.example.simona.toclip.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.simona.toclip.R;
import com.example.simona.toclip.ToClipService;

public class MainFragment extends Fragment {
    private Button startButton;
    private Button stopButton;
    private Intent mServiceIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        startButton = (Button) v.findViewById(R.id.btnStart);
        stopButton = (Button) v.findViewById(R.id.btnStop);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(v);
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(v);
            }
        });

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    public void startService(View view) {
        EditText edt = (EditText)getView().findViewById(R.id.edtIP);
        String ip = edt.getText().toString();

        saveDataToSharedPreferences("ip",ip);

        mServiceIntent = new Intent(getActivity().getBaseContext(), ToClipService.class);
        mServiceIntent.putExtra("ipAddress", ip);
        getActivity().startService(mServiceIntent);
    }

    // Method to stop the service
    public void stopService(View view) {
        if (getActivity().stopService(mServiceIntent)) {
            Toast.makeText(getActivity(), "Disconnect", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToSharedPreferences(String key, String value) {
        Log.i(">>>", "saveDataToSharedPreferences : " + key);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.commit();
    }
}
