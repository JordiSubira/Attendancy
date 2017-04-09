package com.jordi.provasimplebtserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectedThread extends Thread {
    public static final int MESSAGE_READ = 1;

    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mmHandler;
    private final String MY_MAC;

    public ConnectedThread(Handler handler, BluetoothSocket socket,
                           String MAC) {
        mmHandler = handler;
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        MY_MAC=MAC;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }
        catch (IOException e) { }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = mmInStream.read(buffer);

                byte[] bf = MY_MAC.getBytes();

                write(bf);

                mmHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                        .sendToTarget();
            }
            catch (IOException e) {
                break;
            }
        }
    }

    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
            mmOutStream.flush();
        }
        catch (IOException e) { }
    }

    public void cancel() {
        try {
            mmSocket.close();
        }
        catch (IOException e) { }
    }
}