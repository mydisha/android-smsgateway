package com.sms.mydisha.sms_server;

/**
 * Created by mydisha on 6/5/17.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import android.telephony.SmsManager;
import android.util.Log;

public class SmsServer {
    MainActivity activity;
    ServerSocket serverSocket;
    String message = "";
    // Port Socket
    static final int socketServerPORT = 8080;

    public SmsServer(MainActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread {

        int count = 0;

        @Override
        public void run() {
            try {

                serverSocket = new ServerSocket(socketServerPORT);

                while (true) {

                    Socket socket = serverSocket.accept();

                    count++;
                    message += "#" + count + " from "
                            + socket.getInetAddress() + ":"
                            + socket.getPort() + "\n";

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.mTextMessage.setText(message);
                        }
                    });

                    SocketServerReplyThread socketServerReplyThread =
                            new SocketServerReplyThread(socket, count);
                    socketServerReplyThread.run();
                    Log.d("Reply Server", "Server telah me-reply");

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;
        String inputan;

        SocketServerReplyThread(Socket socket, int c) throws IOException {
            hostThreadSocket = socket;
            cnt = c;

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputan = in.readLine();
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Koneksi Berhasil.";

            String[] information = inputan.split(";");
            Log.d("Nomor", information[0]);
            Log.d("Pesan", information[1]);

            SmsManager.getDefault().sendTextMessage(information[0], null, information[1], null,
                    null);

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "Balasan: " + msgReply + "\n";

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.mTextMessage.setText(message);
                    }
                });

                hostThreadSocket.close();

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    activity.mTextMessage.setText(message);
                }
            });
        }

    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

}
