package app.mywork.testuizangle;

import android.content.Context;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Thomas on 4/19/2015.
 */
public class StorageIO {

    public static void saveCredentials(String pin, String password, Context context) {

        try {
            byte[] pinBytes = CryptUtil.encrypt(pin.getBytes(), "");
            byte[] passwordBytes = CryptUtil.encrypt(password.getBytes(), "");

            FileOutputStream pinFile = context.openFileOutput("pin.enc", Context.MODE_PRIVATE);
            pinFile.write(pinBytes);
            pinFile.close();

            FileOutputStream passwordFile = context.openFileOutput("password.enc", Context.MODE_PRIVATE);
            passwordFile.write(passwordBytes);
            passwordFile.close();
        } catch (Exception e) {
            Toast.makeText(context, "Error saving credential data", Toast.LENGTH_SHORT).show();
        }

    }

    public static String[] readCredentials(Context context) {

        try {
            //get file
            File pinFile = new File(context.getFilesDir().getPath() + "/" + "pin.enc");
            //get stream from file
            FileInputStream pinStream = new FileInputStream(pinFile);
            //get bytes from stream
            byte[] pinBytes = new byte[(int)pinFile.length()];
            pinStream.read(pinBytes);
            //decrypt bytes
            byte[] decryptedPinBytes = CryptUtil.decrypt(pinBytes, "");
            //convert bytes to string, close stream
            String pin = new String(decryptedPinBytes);
            pinStream.close();

            //same as above
            File passwordFile = new File(context.getFilesDir().getPath() + "/" + "password.enc");
            FileInputStream passwordStream = new FileInputStream(passwordFile);
            byte[] passwordBytes = new byte[(int)passwordFile.length()];
            passwordStream.read(passwordBytes);
            byte[] decryptedPasswordBytes = CryptUtil.decrypt(passwordBytes, "");
            String password = new String(decryptedPasswordBytes);
            passwordStream.close();

            //put the pin and password into an array to send back
            String[] results = {pin, password};

            return results;
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error reading credential data.", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static void saveClassData(List<ClassData> classDataList, Context context) {
        try {
            FileOutputStream fileOut = context.openFileOutput("classData.enc", Context.MODE_PRIVATE);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            //write object to byte array output stream
            out.writeObject(classDataList);
            //get the data back
            byte[] classData = bos.toByteArray();
            //encrypt it
            byte[] encryptedClassData = CryptUtil.encrypt(classData, "");
            //write it to a file
            fileOut.write(encryptedClassData);
            out.close();
            bos.close();
            fileOut.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Error saving assignment data.", Toast.LENGTH_SHORT).show();
        }
    }

    public static List<ClassData> readClassData(Context context) {
        FileInputStream classesIn = null;
        ByteArrayInputStream bis = null;
        ObjectInputStream in = null;
        try {
            //same as password and pin
            File classDataFile = new File(context.getFilesDir().getPath() + "/" + "classData.enc");
            FileInputStream classDataStream = new FileInputStream(classDataFile);
            byte[] classDataBytes = new byte[(int)classDataFile.length()];
            classDataStream.read(classDataBytes);
            //decrypt bytes
            byte[] decryptedClassDataBytes = CryptUtil.decrypt(classDataBytes, "");
            //get the decrypted bytes into an object stream
            bis = new ByteArrayInputStream(decryptedClassDataBytes);
            in = new ObjectInputStream(bis);
            //decode stream into object
            List<ClassData> cdl = (List<ClassData>)in.readObject();
            //close everything and return the answer
            in.close();
            bis.close();
            classDataStream.close();
            return cdl;
        }
        catch (Exception e)
        {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public static void setPeriod(int i, Context context) {
        File f = new File(context.getFilesDir() + "/timings.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(i);
            pw.close();
        } catch (Exception e) {
            Toast.makeText(context, "Did not save", Toast.LENGTH_LONG).show();
        }
    }

    public static long getPeriod(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/timings.txt"));
            long returning = Integer.parseInt(in.next()) * 60 * 1000;
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return 1 * 60 * 60 * 1000;
        }
    }

    public static void setAllowNotifications(boolean toNotify, Context context) {
        File f = new File(context.getFilesDir() + "/toNotify.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(toNotify);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean getAllowNotifications(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/toNotify.txt"));
            boolean returning = Boolean.parseBoolean(in.next());
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return true;
        }
    }

    public static void setPointLimit(int points, Context context) {
        File f = new File(context.getFilesDir() + "/points.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(points);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int getPointLimit(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/points.txt"));
            int returning = Integer.parseInt(in.next());
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return -10;
        }
    }

    public static void setMultiColor(boolean colors, Context context) {
        File f = new File(context.getFilesDir() + "/multiColor.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(colors);
            pw.close();
            System.out.println(colors);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean getMultiColor(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/multiColor.txt"));
            boolean returning = Boolean.parseBoolean(in.next());
            System.out.println(returning);
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return true;
        }
    }

    public static void setPercentageLimit(double percentage, Context context) {
        File f = new File(context.getFilesDir() + "/percentage.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(percentage);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static double getPercentageLimit(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/percentage.txt"));
            double returning = Double.parseDouble(in.next());
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return 0.6;
        }
    }

    public static void setShouldOpenInBrowser(boolean openInBrowser, Context context) {
        File f = new File(context.getFilesDir() + "/browser.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(openInBrowser);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean getShouldOpenInBrowser(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/browser.txt"));
            boolean returning = Boolean.parseBoolean(in.next());
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static void setAutoReloadOnStart(boolean openInBrowser, Context context) {
        File f = new File(context.getFilesDir() + "/reload.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(openInBrowser);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean getAutoReloadOnStart(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/reload.txt"));
            boolean returning = Boolean.parseBoolean(in.next());
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static void setOnlyBackgroundWifi(boolean wifi, Context context) {
        File f = new File(context.getFilesDir() + "/wifi.txt");
        try {
            PrintWriter pw = new PrintWriter(f);
            pw.print(wifi);
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static boolean getOnlyBackgroundWifi(Context context) {
        try {
            Scanner in = new Scanner(new File(context.getFilesDir() + "/wifi.txt"));
            boolean returning = Boolean.parseBoolean(in.next());
            return returning;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

}
