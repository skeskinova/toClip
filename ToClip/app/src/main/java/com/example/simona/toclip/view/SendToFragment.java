package com.example.simona.toclip.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;

/**
 * Created by simona on 19.5.2016 г..
 */
public class SendToFragment extends Fragment {
    private Button mOk;
    private TextView mTextViewFileName;
    private Bitmap mImage;
    private String mFileName;
    public static SendImages sendImages;

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
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String path = Helper.getPath(getContext(), imageUri);

            mFileName = path.substring(path.lastIndexOf("/")+1);
            mImage = BitmapFactory.decodeFile(path);
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
        //mTextViewFileSize = (TextView) v.findViewById(R.id.text_view_size);

        mTextViewFileName.setText(mFileName);

        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImage != null) {
                    if (sendImages != null) {
                        sendImages.sendImage(mImage, mFileName);
                        getActivity().finish();
                    }
//                    Intent intent = new Intent(getActivity(), ChooseReceiverActivity.class);
//                    intent.putExtra("image", imageToByteArray(mImage));
//                    startActivity(intent);
                }
            }
        });

        return v;
    }

    public interface SendImages {
        public void sendImage(Bitmap bitmap, String fileName);
    }
}
