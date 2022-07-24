package com.eka.cacapp.utils.rootedCheck;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static com.eka.cacapp.utils.rootedCheck.Const.BINARY_BUSYBOX;
import static com.eka.cacapp.utils.rootedCheck.Const.BINARY_SU;



public class RootCheck {

    private final Context mContext;
    private boolean loggingEnabled = true;

    public RootCheck(Context context) {
        mContext = context;
    }


    public boolean isRooted() {

        return detectRootManagementApps() || detectPotentiallyDangerousApps() || checkForBinary(BINARY_SU)
                || checkForDangerousProps() || checkForRWPaths()
                || detectTestKeys() || checkSuExists() ||  checkForMagiskBinary()
                ||hasSuperuserAPK() || hasSU();
    }

    private  boolean hasSU() {
        return findBinary("su") || executeCommand(new String[] { "/system/xbin/which", "su" }) || executeCommand(new String[] { "which", "su" });
    }
    private  boolean findBinary(String binaryName) {
        String[] places = {
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/",
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su",
                "/system/bin/.ext/",
                "/system/usr/we-need-root",
                "/cache",
                "/data",
                "/dev",
                "/system/etc/init.d/99SuperSUDaemon"

        };
        for (String where : places) {
            if (new File(where + binaryName).exists()) {
                return true;
            }
        }
        return false;
    }

    private  boolean executeCommand(String[] command) {
        Process localProcess = null;
        BufferedReader in = null;
        try {
            localProcess = Runtime.getRuntime().exec(command);
            in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
            return (in.readLine() != null);
        } catch (Exception e) {
            return false;
        } finally {
            if (localProcess != null) localProcess.destroy();
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {

                }
            }
        }
    }

    private  boolean hasSuperuserAPK() {
        try {
            File file = new File("/system/app/Superuser.apk");
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }


    @Deprecated
    public boolean isRootedWithoutBusyBoxCheck() {
        return isRooted();
    }


    public boolean isRootedWithBusyBoxCheck() {

        return detectRootManagementApps() || detectPotentiallyDangerousApps() || checkForBinary(BINARY_SU)
                || checkForBinary(BINARY_BUSYBOX) || checkForDangerousProps() || checkForRWPaths()
                || detectTestKeys() || checkSuExists()  || checkForMagiskBinary();
    }


    public boolean detectTestKeys() {
        String buildTags = android.os.Build.TAGS;

        return buildTags != null && buildTags.contains("test-keys");
    }


    public boolean detectRootManagementApps() {
        return detectRootManagementApps(null);
    }


    public boolean detectRootManagementApps(String[] additionalRootManagementApps) {

        // Create a list of package names to iterate over from constants any others provided
        ArrayList<String> packages = new ArrayList<>(Arrays.asList(Const.knownRootAppsPackages));
        if (additionalRootManagementApps!=null && additionalRootManagementApps.length>0){
            packages.addAll(Arrays.asList(additionalRootManagementApps));
        }

        return isAnyPackageFromListInstalled(packages);
    }


    public boolean detectPotentiallyDangerousApps() {
        return detectPotentiallyDangerousApps(null);
    }


    public boolean detectPotentiallyDangerousApps(String[] additionalDangerousApps) {

        // Create a list of package names to iterate over from constants any others provided
        ArrayList<String> packages = new ArrayList<>();
        packages.addAll(Arrays.asList(Const.knownDangerousAppsPackages));
        if (additionalDangerousApps!=null && additionalDangerousApps.length>0){
            packages.addAll(Arrays.asList(additionalDangerousApps));
        }

        return isAnyPackageFromListInstalled(packages);
    }


    public boolean detectRootCloakingApps() {
        return detectRootCloakingApps(null)
                ;
    }


    public boolean detectRootCloakingApps(String[] additionalRootCloakingApps) {

        // Create a list of package names to iterate over from constants any others provided
        ArrayList<String> packages = new ArrayList<>(Arrays.asList(Const.knownRootCloakingPackages));
        if (additionalRootCloakingApps!=null && additionalRootCloakingApps.length>0){
            packages.addAll(Arrays.asList(additionalRootCloakingApps));
        }
        return isAnyPackageFromListInstalled(packages);
    }


    public boolean checkForSuBinary(){
        return checkForBinary(BINARY_SU);
    }


    public boolean checkForMagiskBinary(){ return checkForBinary("magisk"); }


    public boolean checkForBusyBoxBinary(){
        return checkForBinary(BINARY_BUSYBOX);
    }


    public boolean checkForBinary(String filename) {

        String[] pathsArray = Const.getPaths();

        boolean result = false;

        for (String path : pathsArray) {
            String completePath = path + filename;
            File f = new File(path, filename);
            boolean fileExists = f.exists();
            if (fileExists) {

                result = true;
            }
        }

        return result;
    }



    private String[] propsReader() {
        try {
            InputStream inputstream = Runtime.getRuntime().exec("getprop").getInputStream();
            if (inputstream == null) return null;
            String propVal = new Scanner(inputstream).useDelimiter("\\A").next();
            return propVal.split("\n");
        } catch (IOException | NoSuchElementException e) {

            return null;
        }
    }

    private String[] mountReader() {
        try {
            InputStream inputstream = Runtime.getRuntime().exec("mount").getInputStream();
            if (inputstream == null) return null;
            String propVal = new Scanner(inputstream).useDelimiter("\\A").next();
            return propVal.split("\n");
        } catch (IOException | NoSuchElementException e) {

            return null;
        }
    }

    
    private boolean isAnyPackageFromListInstalled(List<String> packages){
        boolean result = false;

        PackageManager pm = mContext.getPackageManager();

        for (String packageName : packages) {
            try {
                // Root app detected
                pm.getPackageInfo(packageName, 0);
                result = true;
            } catch (PackageManager.NameNotFoundException e) {
                // Exception thrown, package is not installed into the system
            }
        }

        return result;
    }

    public boolean checkForDangerousProps() {

        final Map<String, String> dangerousProps = new HashMap<>();
        dangerousProps.put("ro.debuggable", "1");
        dangerousProps.put("ro.secure", "0");

        boolean result = false;

        String[] lines = propsReader();

        if (lines == null){
            // Could not read, assume false;
            return false;
        }

        for (String line : lines) {
            for (String key : dangerousProps.keySet()) {
                if (line.contains(key)) {
                    String badValue = dangerousProps.get(key);
                    badValue = "[" + badValue + "]";
                    if (line.contains(badValue)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }


    public boolean checkForRWPaths() {

        boolean result = false;

        String[] lines = mountReader();

        if (lines == null){
            // Could not read, assume false;
            return false;
        }

        int sdkVersion = android.os.Build.VERSION.SDK_INT;


        for (String line : lines) {

            // Split lines into parts
            String[] args = line.split(" ");

            if ((sdkVersion <= android.os.Build.VERSION_CODES.M && args.length < 4)
                    || (sdkVersion > android.os.Build.VERSION_CODES.M && args.length < 6)) {
                // If we don't have enough options per line, skip this and log an error
                continue;
            }

            String mountPoint;
            String mountOptions;


            if (sdkVersion > android.os.Build.VERSION_CODES.M) {
                mountPoint = args[2];
                mountOptions = args[5];
            } else {
                mountPoint = args[1];
                mountOptions = args[3];
            }

            for(String pathToCheck: Const.pathsThatShouldNotBeWritable) {
                if (mountPoint.equalsIgnoreCase(pathToCheck)) {


                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
                        mountOptions = mountOptions.replace("(", "");
                        mountOptions = mountOptions.replace(")", "");

                    }


                    for (String option : mountOptions.split(",")){

                        if (option.equalsIgnoreCase("rw")){
                            result = true;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }



    public boolean checkSuExists() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "which", BINARY_SU });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }





}