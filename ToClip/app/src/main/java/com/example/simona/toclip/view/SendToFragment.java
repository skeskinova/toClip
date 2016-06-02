package com.example.simona.toclip.view;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.simona.toclip.R;
import com.example.simona.toclip.helpers.Helper;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by simona on 19.5.2016 г..
 */
public class SendToFragment extends Fragment {
    private Button mOk;
    private Button mCancel;
    private TextView mTextViewFileName;
    private File mFile;
    private String mFileName;
    private long mFileSize;
    public static SendImages sendImages;
    private TextView mTextViewFileSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String path = Helper.getPath(getContext(), imageUri);

            mFile = new File(path);

            mFileName = mFile.getName();
            mFileSize = mFile.length() / (1024);
            //mImage = BitmapFactory.decodeFile(path);
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Update UI to reflect multiple images being shared
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_to, container, false);
        mOk = (Button) v.findViewById(R.id.button_ok);
        mTextViewFileName = (TextView) v.findViewById(R.id.text_view_file);
        mTextViewFileSize = (TextView) v.findViewById(R.id.text_view_file_size);
        mCancel = (Button) v.findViewById(R.id.button_cancel);
        mTextViewFileName.setText(mFileName);
        mTextViewFileSize.setText(String.valueOf(mFileSize) + " KB");

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFile != null) {
                    if (sendImages != null) {
                        sendImages.sendImage(mFile, mFileName);
                        getActivity().finish();
                    }
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return v;
    }

    public interface SendImages {
        void sendImage(File file, String fileName);
    }
}
