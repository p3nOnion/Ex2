import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) {

        // Địa chỉ máy chủ: 112.137.129.129
        final String serverHost = "112.137.129.129";

        Socket socketOfClient = null;
        DataOutputStream os = null;
        DataInputStream is = null;
        try {
            /*
             * create a connector to server, port 27001.
             */
            socketOfClient = new Socket(serverHost, 27002);

            os = new DataOutputStream(new BufferedOutputStream(socketOfClient.getOutputStream()));
            is = new DataInputStream(new BufferedInputStream(socketOfClient.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }

        try {
            /*
             * Hi server!!!.
             */
            String str = "19020235";
            byte[] msv = str.getBytes(StandardCharsets.UTF_8);
            os.write(Helper.intToByteArray(0));
            os.write(Helper.intToByteArray(8));
            os.write(msv);
            os.flush();

            while (true) {
                boolean exit = false;

                int type = Helper.myReadInt(is);
                System.out.println("type : " + type);

                switch (type) {
                    case 0 -> {
                        int value = Helper.myReadInt(is);
                        System.out.println(value);
                    }
                    case 1 -> {
                        int length_1 = Helper.myReadInt(is);
                        System.out.println("length_1: " + length_1);
                        int N = Helper.myReadInt(is);
                        System.out.println("N: " + N);
                        BigInteger M = BigInteger.valueOf(Helper.myReadInt(is));
                        System.out.println("M: " + M);
                        BigInteger x = BigInteger.valueOf(Helper.myReadInt(is));
                        System.out.println("x: " + x);

                        BigInteger[] arr = new BigInteger[N + 1];
                        for (int i = 0; i <= N; i++) {
                            arr[i] = BigInteger.valueOf(Helper.myReadInt(is)).mod(M);
                        }

                        BigInteger sum = arr[N];
                        for (int i = N; i > 0; i--) {
                            sum = sum.multiply(x).add(arr[i - 1]);
                            sum = sum.mod(M);
                        }
                        System.out.println(sum);

                        os.write(Helper.intToByteArray(2));
                        os.write(Helper.intToByteArray(4));
                        os.write(Helper.intToByteArray(Integer.parseInt(sum.toString())));
                        os.flush();
                        System.out.println("sent");
                    }
                    case 3 -> exit = true;
                    case 4 -> {
                        int length = Helper.myReadInt(is);
                        System.out.println("length " + length);
                        System.out.print(new String(is.readAllBytes()));
                        exit = true;
                    }
                }

                if (exit) {
                    break;
                }
            }

            os.close();
            is.close();
            socketOfClient.close();

        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

}
