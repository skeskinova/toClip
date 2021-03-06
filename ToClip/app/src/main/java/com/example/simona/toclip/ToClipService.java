package com.example.simona.toclip;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.simona.toclip.view.SendToFragment;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class ToClipService extends Service {

    private ClipboardManager clipMan;
    private String message;
    private Socket client;
    private DataOutputStream out;
    private DataInputStream in;
    private static Boolean isStarted = false;
    private String ipAddress;
    private Thread thread;
    private Boolean isPasted = false;

    public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        SendToFragment.sendImages = new SendToFragment.SendImages() {
            @Override
            public void sendImage(File file, String fileName) {
                if (!isStarted) {
                    return;
                }

                if (out != null) {
                    try {
                        out.writeInt(2); // send type 2 - file

                        byte[] bName = fileName.getBytes(UTF8_CHARSET);
                        out.writeInt(bName.length); // send fileName size
                        out.write(bName); //send file name
                        out.flush();

                        byte[] bytes = new byte[1024];
                        FileInputStream inputStream = new FileInputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(inputStream);

                        int b = -1;
                        int count = 1;

                        b = bis.read(bytes);
                        while (b > -1) {
                            out.writeInt(b);
                            out.write(bytes, 0, b);
                            b = bis.read(bytes);
                            count += 1;
                        }

                        out.writeInt(b);
                        out.flush();
                    } catch (IOException e) {
                        Log.e(">>>", "Error", e);
                    }
                }
            }
        };

        if (!isStarted) {
            isStarted = true;
            Log.i(">>>", "Service started");
            ipAddress = intent.getStringExtra("ipAddress");
            clipMan = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isStarted) {
                        Handler h = new Handler(getApplicationContext().getMainLooper());

                        try {
                            client = new Socket(); // connect to the server
                            client.connect(new InetSocketAddress(ipAddress, 4444), 1000);

                            in = new DataInputStream(client.getInputStream());
                            out = new DataOutputStream(client.getOutputStream());
                            Log.i(">>>", "Connection open");

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Успешно свързване!",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });

                            while (isStarted) {
                                int size = in.readInt();

                                if (size > 0) {
                                    byte[] inMessage = new byte[size];
                                    in.read(inMessage);

                                    String mess = new String(inMessage, "UTF-8");
                                    Log.i(">>>", "Message received : " + mess);

                                    isPasted = true;
                                    ClipData clip = ClipData.newPlainText("", mess);
                                    clipMan.setPrimaryClip(clip);
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            Log.e(">>>", e.getMessage());
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Грешка при опит за свързване!",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        } catch (IOException e) {
                            Log.e(">>>", e.getMessage());
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(),
                                            "Грешка при опит за свързване!",
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                        } finally {
                            if (client != null && client.isConnected() && in != null && out != null) {
                                try {
                                    in.close();
                                    in = null;

                                    out.close();
                                    out = null;

                                    client.close(); // closing the connection

                                    Log.i(">>>", "Connection closed");
                                    h.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(),
                                                    "Връзката е затворена успешно!",
                                                    Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                                } catch (IOException e) {
                                    Log.e(">>>", "Грешка", e);
                                }
                            }
                        }
                    }
                }
            });

            thread.start();

            clipMan.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    if (!isStarted) {
                        return;
                    }

                    if (isPasted) {
                        isPasted = false;
                        return;
                    }

                    message = clipMan.getPrimaryClip().getItemAt(0).getText().toString();
                    Log.i(">>>", "Clipboard text : " + message);

                    if (out != null) {
                        try {
                            out.writeInt(1);

                            byte[] b = message.getBytes(UTF8_CHARSET);
                            out.writeInt(b.length);
                            out.write(b); // write the message to output stream
                            out.flush();
                        } catch (IOException e) {
                            Log.e(">>>", "Ërror", e);
                        }
                    }
                }
            });
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(">>>", "Service stopped");
        isStarted = false;

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
