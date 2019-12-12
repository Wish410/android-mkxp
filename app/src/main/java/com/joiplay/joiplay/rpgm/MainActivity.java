package com.joiplay.joiplay.rpgm;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import org.libsdl.app.SDLActivity;

import java.io.*;

public class MainActivity extends SDLActivity {

    public static String CONF_FILE = null;
    public static final String CONF_FILE_NAME = "mkxp.conf";
    public static String GAME_FOLDER = null;
    public static String RGA_PATH = "";
    public static String RTP_PATH = "";
    private static String rpgStr = "";
    private static String rgssExt = "";
    public static Integer cKeyCode = KeyEvent.KEYCODE_C;
    public static Integer xKeyCode = KeyEvent.KEYCODE_X;
    public static Integer yKeyCode = KeyEvent.KEYCODE_Y;
    public static Integer zKeyCode = KeyEvent.KEYCODE_Z;
    public static Integer btnOpacity = 100;
    public static Float btnScale = 1.0f;
    private Boolean smoothScaling = true;
    private Boolean vsync = true;
    private Boolean frameSkip = false;
    private Boolean solidFonts = false;
    private Boolean forcedDim = false;
    private Boolean pathCache = true;

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        if (getIntent() != null){
            Intent intent = getIntent();
            if (intent.hasExtra("rgaPath")){
                String path = intent.getStringExtra("rgaPath");
                GAME_FOLDER = path;
                RGA_PATH = path;
                Log.d("RPGM","Game Folder = "+GAME_FOLDER);
            }

            if (intent.hasExtra("rtpPath")){
                RTP_PATH = intent.getStringExtra("rtpPath");
            }
            if (intent.hasExtra("rpgStr")){
                rpgStr = intent.getStringExtra("rpgStr");
                if (rpgStr.endsWith("XP")){
                    rgssExt = "rgssad";
                } else if (rpgStr.endsWith("VX")){
                    rgssExt = "rgss2a";
                } else if (rpgStr.endsWith("VXACE")){
                    rgssExt = "rgss3a";
                }

            }

            if (intent.hasExtra("smoothScaling")){
                smoothScaling = intent.getBooleanExtra("smoothScaling",true);
            }
            if (intent.hasExtra("vsync")){
                vsync = intent.getBooleanExtra("vsync",true);
            }
            if (intent.hasExtra("frameSkip")){
                frameSkip = intent.getBooleanExtra("frameSkip",true);
            }
            if (intent.hasExtra("solidFonts")){
                solidFonts = intent.getBooleanExtra("solidFonts",true);
            }
            if (intent.hasExtra("forcedDim")){
                forcedDim = intent.getBooleanExtra("forcedDim",true);
            }
            if (intent.hasExtra("pathCache")){
                pathCache = intent.getBooleanExtra("pathCache",true);
            }
        }

        CONF_FILE = GAME_FOLDER + File.separator + CONF_FILE_NAME;
        AssetManager assetFile = getAssets();
        try {
            File rtp = new File(getExternalFilesDir(null).getAbsolutePath()+"/RTP/"+rpgStr+"/app");

            File nomedia = new File(GAME_FOLDER+"/.nomedia");
            if (!nomedia.exists()){
                try {
                    nomedia.createNewFile();
                } catch (Exception e){
                    Log.d("MainActivity","Could not create .nomedia");
                }
            }

            File new_conf = new File(CONF_FILE);
            new_conf.delete();
            new_conf.createNewFile();
            FileOutputStream stream = new FileOutputStream(new_conf);
            BufferedReader br = new BufferedReader(new InputStreamReader(assetFile.open(CONF_FILE_NAME)));
            while (true) {
                String read = br.readLine();
                if (read == null) {
                    break;
                }
                stream.write((read.replaceAll("RGA_PATH", GAME_FOLDER)
                        .replaceAll("RTP_PATH",rtp.getAbsolutePath())
                        .replaceAll("GAME_FOLDER", GAME_FOLDER)
                        .replaceAll("RGSS_EXT", "Game."+rgssExt)
                        .replaceAll("SMOOTHSCALING_VALUE",smoothScaling ? "true" : "false")
                        .replaceAll("VSYNC_VALUE",vsync ? "true" : "false")
                        .replaceAll("FRAMESKIP_VALUE",frameSkip ? "true" : "false")
                        .replaceAll("SOLIDFONTS_VALUE",solidFonts ? "true" : "false")
                        .replaceAll("FORCEDWIDTH_VALUE",forcedDim ? "1920" : "0")
                        .replaceAll("FORCEDHEIGHT_VALUE",forcedDim ? "1080" : "0")
                        .replaceAll("PATHCACHE_VALUE",pathCache ? "true" : "false") + "\n").getBytes());
            }
            stream.close();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String getConfPath(){
        return CONF_FILE;
    }
}
