package cyou.joiplay.rpgm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cyou.joiplay.commons.dialog.Dialog;

public class PermissionActivity extends Activity {
    private static final int fileRequestCode = 4900;
    private static final int allRequestCode = 4901;

    private TextView progView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.rpgm_permission);

        progView = findViewById(R.id.progText);
        progView.setText(R.string.checking_config);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (!Environment.isExternalStorageManager()){
                showManageStoragePermissionDialog();
            } else {
                startGame();
            }
        } else if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, fileRequestCode);
        } else {
            startGame();
        }
    }

    private void showManageStoragePermissionDialog(){
        Dialog permissionDialog = new Dialog();
        permissionDialog.setTitle(R.string.permission);
        permissionDialog.setMessage(R.string.manage_external_storage_permission_message);
        permissionDialog.setPositiveButton(R.string.ok, ()->{
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                startActivityForResult(intent, allRequestCode);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, allRequestCode);
            }
        });
        permissionDialog.setNegativeButton(R.string.close, this::finishAffinity);
        permissionDialog.show(this);
    }

    private void startGame(){
        if (getIntent() != null && getIntent().getExtras() != null) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtras(getIntent().getExtras());
            startActivity(i);
        } else {
            progView.setText(R.string.got_no_data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case fileRequestCode:
                if (Build.VERSION.SDK_INT >= 23){
                    if ( checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
                        startGame();
                    } else {
                        requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, fileRequestCode);
                    }
                }
                break;
            case allRequestCode:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        startGame();
                    } else {
                        showManageStoragePermissionDialog();
                    }
                }
        }
    }
}
