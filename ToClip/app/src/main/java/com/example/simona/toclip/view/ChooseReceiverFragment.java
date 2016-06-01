package com.example.simona.toclip.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.simona.toclip.R;

public class ChooseReceiverFragment extends Fragment {
    private Bitmap mImage;
//    public static SendImages sendImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
         if (intent.getByteArrayExtra("image") != null) {
             mImage = byteArrayToImage(intent.getByteArrayExtra("image"));
         }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_choose_receiver, container, false);
        final EditText editTextIp = (EditText) v.findViewById(R.id.edit_text_ip);

        editTextIp.setText(getDataFromSharedPreferences("ip"));

        Button buttonSend = (Button) v.findViewById(R.id.button_send);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImage != null && !editTextIp.getText().toString().isEmpty()) {

                }
            }
        });

        return v;
    }

    private String getDataFromSharedPreferences(String key) {
        Log.i(">>>", "getDataFromSharedPreferences : " + key);

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, "");
        } else {
            return null;
        }
    }

    private Bitmap byteArrayToImage(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

//    public interface SendImages {
//        public void sendImage(Bitmap bitmap, String fileName);
//    }
}
