package com.github.xzwj87.mineflea.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import com.avos.avoscloud.AVFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A handler to upload crash info to server
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private static CrashHandler sInstance = new CrashHandler();
    private Context mContext;

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() +
            "MineFlea/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_TYPE = ".txt";
    private static final int MAX_BUFFER_SIZE = 1000*1024;


    private CrashHandler(){}

    public static CrashHandler getInstance(){
        return sInstance;
    }

    public void init(Context context){
        mContext = context;
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        // here we want to upload crash information to server or save it on disk
        try {
            dumpCrashInfoToDisk(e);
            uploadCrashInfoToServer(e);
        }catch (IOException ex){
            ex.printStackTrace();
            Log.v(TAG,"fail to dump crash info to disk/server");
        }

        e.printStackTrace();

        if(mDefaultExceptionHandler != null){
            mDefaultExceptionHandler.uncaughtException(t,e);
        }else{
            Process.killProcess(Process.myPid());
        }
    }

    private void dumpCrashInfoToDisk(Throwable ex) throws IOException{
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.v(TAG,"external storage is not mounted currently");
            return;
        }

        File dir = new File(PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String time = getCrashFileName();
        File file = new File(time);

        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));

        try {
            pw.println(time);
            dumpDeviceInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.v(TAG,"fail to dump crash info to disk");
        }
    }

    private void uploadCrashInfoToServer(Throwable ex) throws IOException{
        File dir = new File(PATH);
        File[] fils = dir.listFiles();
        byte[] bytes = new byte[MAX_BUFFER_SIZE];

        if(fils.length > 0){
            for(File file :fils) {
                FileInputStream fis = new FileInputStream(file);
                fis.read(bytes);
                AVFile avFile = new AVFile(getCrashFileName(), bytes);
                avFile.saveInBackground();
            }
        }
    }

    private void dumpDeviceInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();

        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.println("-" + pi.versionCode);

        //OS version
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.println("-" + Build.VERSION.SDK_INT);

        // Vendor
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        // phone model
        pw.print("Model: ");
        pw.println(Build.MODEL);

        // CPU architecture
        pw.print("CPU ABI: ");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pw.println(Build.SUPPORTED_ABIS);
        }else{
            pw.println(Build.CPU_ABI);
        }
    }

    private String getCrashFileName(){
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));

        return PATH + FILE_NAME + time + FILE_TYPE;
    }
}
