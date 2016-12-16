package com.coordiwise.bluetoothaha;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by aha on 2016-12-14.
 */

public class RfcServer {
    public static final String TAG = "AAAA";//RfcServer.class.getName();
    static final String SERVER_NAME = "AhaServer";
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final int maxBufLen = 1024;
    private InputStream inStream = null;
    private OutputStream outStream = null;
    private BluetoothServerSocket serverSocket = null;
    private BluetoothSocket socket = null;
    private boolean bRun = false;
    private BluetoothAdapter ba;
    private thread mThread = null;


    public synchronized void Start () {
        if (mThread == null) {
            mThread = new thread();
            mThread.start();
        }
    }

    public synchronized void Stop () {
        if (mThread != null) {
            bRun = false;
            mThread.close ();
        }
    }

    public boolean isRun() {
        return bRun;
    }
    public synchronized void send (final byte[] data) {
        if (outStream != null) {
            try {
                outStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class thread extends Thread {
        public thread () {
            Log.d(TAG, "thread()");
            ba = BluetoothAdapter.getDefaultAdapter();
        }
        public void close () {
            bRun = false;
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outStream = null;
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "run()");
            super.run();

            bRun = true;
            while (bRun) {
                Log.d(TAG, "run");
                /*
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                */
                try {
                    serverSocket = ba.listenUsingRfcommWithServiceRecord(SERVER_NAME, MY_UUID_SECURE);
                } catch (IOException e) {
                    Log.d(TAG, "server socket Exception");
                    serverSocket = null;
                }

                if (serverSocket == null) {
                    bRun = false;
                    return;
                }

                if (accept_incoming() == false)
                    continue;
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;
                Log.d(TAG, "socket connected");

                if (open_inputStream() == false)
                    continue;

                if (open_outputStream() == false)
                    continue;

                processIncomingData ();
            }
        }
        private boolean accept_incoming () {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                Log.d(TAG, "accept Exception");
                socket = null;
            }

            if (socket == null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }

            return true;
        }
        private boolean open_inputStream () {
            try {
                inStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (inStream == null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inStream = null;

                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                outStream = null;

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        }
        private boolean open_outputStream () {
            try {
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outStream == null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outStream = null;

                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                inStream = null;

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                socket = null;

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
            return true;
        }
        private synchronized void processIncomingData () {
            byte[] buf = new byte[maxBufLen];
            int read;
            int bufLen = 0;
            int bufRest;

            while (bRun) {
                if (inStream == null) {
                    return;
                }

                bufRest = maxBufLen - bufLen;

                try {
                    read = inStream.read(buf, bufLen, bufRest);
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

                bufLen += read;
            }
        }
    }
}
