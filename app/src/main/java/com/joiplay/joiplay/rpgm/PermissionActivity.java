package com.joiplay.joiplay.rpgm;

import android.content.*;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.tonyodev.fetch2.*;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2core.DownloadBlock;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.progress.ProgressMonitor;

import org.jetbrains.annotations.NotNull;
import uk.co.armedpineapple.innoextract.service.Configuration;
import uk.co.armedpineapple.innoextract.service.ExtractCallback;
import uk.co.armedpineapple.innoextract.service.ExtractService;
import uk.co.armedpineapple.innoextract.service.IExtractService;

import java.io.*;
import java.util.List;

public class PermissionActivity extends AppCompatActivity implements ExtractCallback {

    private final int RPGXP = 100;
    private final int RPGVX = 200;
    private final int RPGVXAce = 300;
    private final int sizeRTPXP = 22;
    private final int sizeRTPVX = 35;
    private final int sizeRTPVXACE = 185;
    private final String sRPGXP = "RPGXP";
    private final String sRPGVX = "RPGVX";
    private final String sRPGVXACE = "RPGVXACE";
    private final String rtpXP = "https://s3-ap-northeast-1.amazonaws.com/degica-prod.product-files/20/xp_rtp104e.exe";
    private final String rtpVX = "https://s3-ap-northeast-1.amazonaws.com/degica-prod.product-files/19/vx_rtp102e.zip";
    private final String rtpVXAce = "http://cached-downloads.degica.com/degica-downloads/RPGVXAce_RTP.zip";

    private TextView progView;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEdit;
    private IExtractService extractService;
    private Fetch fetch;
    private boolean isServiceBound = false;
    private Connection connection = new Connection();
    private int rpgVer = 0;
    private String rpgStr = "";
    private String  rtpSize = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.permissionactivity_layout);
        progView = findViewById(R.id.progText);
        progView.setText(R.string.checking_config);
        if (getIntent() != null){
            String aName = getIntent().getAction();
            aName = aName.replace(".MainActivity","");
            Log.d("ActionName",aName);
            if (aName.endsWith("rpgmxp")){
                rpgVer = RPGXP;
                rpgStr = sRPGXP;
                rtpSize = String.valueOf(sizeRTPXP)+"MB";
            } else if (aName.endsWith("rpgmvx")){
                rpgVer = RPGVX;
                rpgStr = sRPGVX;
                rtpSize = String.valueOf(sizeRTPVX)+"MB";
            } else if (aName.endsWith("rpgmvxace")){
                rpgVer = RPGVXAce;
                rpgStr = sRPGVXACE;
                rtpSize = String.valueOf(sizeRTPVXACE)+"MB";
            }
        }
        Intent ip = new Intent(this,ExtractService.class);
        bindService(ip,connection,Context.BIND_ABOVE_CLIENT | Context.BIND_AUTO_CREATE);
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 12);
        } else {
            sp = this.getPreferences(Context.MODE_PRIVATE);
            spEdit = sp.edit();

            if (sp.contains("download"+rpgStr)){
                boolean dRTP = sp.getBoolean("download"+rpgStr,false);
                File rtpDir = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP");
                if (dRTP){
                    if (!rtpDir.exists()){
                        downloadRTP(progView);
                    }else {
                        if (getIntent() != null && getIntent().getExtras() != null){
                            Intent i = new Intent(this,MainActivity.class);
                            i.putExtras(getIntent().getExtras());
                            i.putExtra("rpgStr",rpgStr);
                            startActivity(i);
                        } else {
                            progView.setText(R.string.got_no_data);
                        }
                    }
                } else {
                    copyDefRes(progView);
                }
            } else {
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
                dBuilder.setTitle(R.string.rtpDialog_title);
                dBuilder.setMessage(R.string.rtpDialog_message);
                dBuilder.setPositiveButton(R.string.default_, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        spEdit.putBoolean("download"+rpgStr,false);
                        spEdit.apply();
                        copyDefRes(progView);
                    }
                });
                dBuilder.setNegativeButton(getString(R.string.enterbrain,rtpSize), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadRTP(progView);
                    }
                });
                AlertDialog dDialog = dBuilder.create();
                dDialog.show();
            }
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    class  Connection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ExtractService.ServiceBinder binder = (ExtractService.ServiceBinder) service;
            extractService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    }

    private void downloadRTP(TextView progText){
        progText.setText(getString(R.string.downloading_rtp,"-","-","-"));
        String rtpUrl = "";
        File rtpFile = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP.zip");;
        switch (rpgVer){
            case RPGXP:
                rtpUrl = rtpXP;
                rtpFile = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP.exe");
                break;
            case RPGVX:
                rtpUrl = rtpVX;
                break;
            case RPGVXAce:
                rtpUrl = rtpVXAce;
                break;
        }
        final File rtp = rtpFile;
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(3)
                .setAutoRetryMaxAttempts(20)
                .enableAutoStart(true)
                .setProgressReportingInterval(1000)
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        final Request request = new Request(rtpUrl,rtpFile.getAbsolutePath());
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        FetchListener fetchListener = new FetchListener() {
            @Override
            public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {

            }

            @Override
            public void onCompleted(@NotNull Download download) {
                if (request.getId() == download.getId()){
                    extractRTP(rtp,progText);
                }
            }

            @Override
            public void onAdded(@NotNull Download download) {

            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progText.setText(R.string.waiting_network);
                    }
                });

            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progText.setText(getString(R.string.download_error)+" Error : "+error.getThrowable().getMessage());
                    }
                });
            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

            }

            @Override
            public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                if (request.getId() == download.getId()) {
                    int progress = download.getProgress();
                    long speed = download.getDownloadedBytesPerSecond()/1024;
                    long timeLeft = download.getEtaInMilliSeconds()/1000;
                    String eta;
                    String sString;
                    if (timeLeft<60){
                        eta = String.valueOf(timeLeft) + "s";
                    } else if (timeLeft <3600){
                        eta = String.valueOf(timeLeft/60)+"m";
                    } else {
                        eta = String.valueOf(timeLeft/3600)+"h";
                    }

                    if (speed < 1024){
                        sString = String.valueOf(speed) + "KBps";
                    } else if(speed < 1024*1024){
                        sString = String.valueOf(speed/1024) + "MBps";
                    } else {
                        sString = String.valueOf(speed/(1024*1024)) + "GBps";
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progText.setText(getString(R.string.downloading_rtp,String.valueOf(progress)+"%",sString,eta));
                        }
                    });

                }

            }

            @Override
            public void onPaused(@NotNull Download download) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progText.setText(R.string.download_paused);
                    }
                });
            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onCancelled(@NotNull Download download) {

            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        };
        fetch.addListener(fetchListener);
        fetch.enqueue(request,updatedRequest -> {
            //Request was successfully enqueued for download.
        }, error -> {
            //An error occurred enqueuing the request.
        });

    }

    private void extractRTP(File file,TextView progText){
        progText.setText(getString(R.string.extracting_rtp, "-"));
        File extDir = new File(getExternalFilesDir(null).getAbsolutePath()+File.separator+"/RTP/"+rpgStr);
        if (!extDir.exists()){
            extDir.mkdirs();
        }
        File exe;
        try {
            ZipFile zF = new ZipFile(file);
            zF.setRunInThread(false);
            switch (rpgVer){
                case RPGXP:
                    exe = file;
                    if (exe.exists() && exe.canRead() && exe.isFile()){
                        progText.setText(R.string.extracting_rtp);
                        Configuration configuration = new Configuration();
                        PermissionActivity.this.extractService.extract(exe,extDir,PermissionActivity.this,configuration);
                    }
                    break;
                case RPGVX:
                    exe = new File(getExternalFilesDir(null).getAbsolutePath()+"/RPGVX_RTP/Setup.exe");
                    zF.extractFile("RPGVX_RTP/Setup.exe",getExternalFilesDir(null).getAbsolutePath());
                    if (exe.exists() && exe.canRead() && exe.isFile()){
                        progText.setText(R.string.extracting_rtp);
                        Configuration configuration = new Configuration();
                        PermissionActivity.this.extractService.extract(exe,extDir,PermissionActivity.this,configuration);
                    }
                    break;
                case RPGVXAce:
                    exe = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP100/Setup.exe");
                    zF.extractFile("RTP100/Setup.exe",getExternalFilesDir(null).getAbsolutePath());
                    zF.extractFile("RTP100/Setup-1.bin",getExternalFilesDir(null).getAbsolutePath());
                    if (exe.exists() && exe.canRead() && exe.isFile()){
                        progText.setText(R.string.extracting_rtp);
                        Configuration configuration = new Configuration(false,false,false);
                        PermissionActivity.this.extractService.extract(exe,extDir,PermissionActivity.this,configuration);
                    }
                    break;
            }


        } catch (Exception e){
            progText.setText(R.string.extract_failed);
            Log.d("Error",Log.getStackTraceString(e));
        }
    }
    private void copyDefRes(TextView progText){
        Log.d("copyDefRes","Extracting files..");
        try {
            File file = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP/"+rpgStr+"/app");
            if (file.exists() && file.listFiles().length>1){
                if (getIntent() != null && getIntent().getExtras() != null){
                    Intent i = new Intent(PermissionActivity.this,MainActivity.class);
                    i.putExtras(getIntent().getExtras());
                    i.putExtra("rpgStr",rpgStr);
                    startActivity(i);
                } else {
                    PermissionActivity.this.progView.setText(R.string.got_no_data);
                }
                return;
            }
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            File rF = new File(file.getParentFile().getAbsolutePath()+"/RTP.zip");
            if (!rF.exists()){
                InputStream rIS = getAssets().open("RTP.zip");
                byte[] buffer = new byte[rIS.available()];
                rIS.read(buffer);
                OutputStream os = new FileOutputStream((rF));
                os.write(buffer);
            }

            ZipFile zipFile = new ZipFile(rF);
            zipFile.setRunInThread(true);
            zipFile.extractAll(file.getAbsolutePath());
            while (zipFile.getProgressMonitor().getPercentDone()<100){
                if (zipFile.getProgressMonitor().getState() != ProgressMonitor.STATE_BUSY) break;
                String pct = String.valueOf(zipFile.getProgressMonitor().getPercentDone()) + "%";
                Log.d("Percent",pct);
                Thread.sleep(1000);
            }

            if (getIntent() != null && getIntent().getExtras() != null){
                Intent i = new Intent(PermissionActivity.this,MainActivity.class);
                i.putExtras(getIntent().getExtras());
                i.putExtra("rpgStr",rpgStr);
                startActivity(i);
            } else {
                progText.setText(R.string.got_no_data);
            }
        } catch (Exception e){
            progText.setText(R.string.extract_failed);
            Log.d("Error",Log.getStackTraceString(e));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 12 && ContextCompat.checkSelfPermission(this,"android.permission.WRITE_EXTERNAL_STORAGE") == 0){
            sp = this.getPreferences(Context.MODE_PRIVATE);
            spEdit = sp.edit();

            if (sp.contains("download"+rpgStr)){
                boolean dRTP = sp.getBoolean("download"+rpgStr,false);
                File rtpDir = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP");
                if (dRTP){
                    if (!rtpDir.exists()){
                        downloadRTP(PermissionActivity.this.progView);
                    }else {
                        if (getIntent() != null && getIntent().getExtras() != null){
                            Intent i = new Intent(this,MainActivity.class);
                            i.putExtras(getIntent().getExtras());
                            i.putExtra("rpgStr",rpgStr);
                            startActivity(i);
                        } else {
                            PermissionActivity.this.progView.setText(R.string.got_no_data);
                        }
                    }
                } else {
                    copyDefRes(PermissionActivity.this.progView);
                }
            } else {
                AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
                dBuilder.setTitle(R.string.rtpDialog_title);
                dBuilder.setCancelable(false);
                dBuilder.setMessage(R.string.rtpDialog_message);
                dBuilder.setPositiveButton(R.string.default_, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        spEdit.putBoolean("download"+rpgStr,false);
                        spEdit.apply();
                        copyDefRes(PermissionActivity.this.progView);
                    }
                });
                dBuilder.setNegativeButton(getString(R.string.enterbrain,rtpSize), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadRTP(PermissionActivity.this.progView);
                    }
                });
                AlertDialog dDialog = dBuilder.create();
                dDialog.show();
            }

        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 12);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(connection);
        } catch (Exception e){

        }

    }

    @Override
    public void onFailure(Exception e) {

    }


    @Override
    public void onSuccess() {
        spEdit.putBoolean("download"+rpgStr,true);
        spEdit.apply();
        File exe = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP.exe");
        File binFile = new File(exe.getParentFile().getAbsolutePath()+"/Setup-1.bin");
        File zipFile = new File(exe.getParentFile().getAbsolutePath()+"/RTP.zip");
        try {
            exe.delete();
            if (binFile.exists()) {
                binFile.delete();
            }
            if (zipFile.exists()) {
                zipFile.delete();
            }
        } catch (Exception e){
            Log.d("Warning","Could not delete rtp setup file.");
        }
        if (getIntent() != null && getIntent().getExtras() != null){
            Intent i = new Intent(this,MainActivity.class);
            i.putExtras(getIntent().getExtras());
            i.putExtra("rpgStr",rpgStr);
            startActivity(i);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progView.setText(R.string.got_no_data);
                }
            });

        }
    }

    @Override
    public void onProgress(long value, long max, long speedBps, long remainingSeconds) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                double pct = (1.0d * value / max) * 100;
                String sPct = String.format("%.2f",pct)+"%";
                PermissionActivity.this.progView.setText(getString(R.string.extracting_rtp,sPct));
            }
        });
    }
}
