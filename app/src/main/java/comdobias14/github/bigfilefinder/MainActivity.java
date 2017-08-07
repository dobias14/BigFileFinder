package comdobias14.github.bigfilefinder;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import comdobias14.github.bigfilefinder.dummy.DummyContent;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements ItemFragment.OnListFragmentInteractionListener {

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private File root;
    public static ArrayList<File> scanFiles;
    public static CounterVariable counter;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanFiles = new ArrayList<>();
        context = getApplicationContext();

        counter = new CounterVariable();
        counter.setListener(new CounterVariable.ChangeListener() {
            @Override
            public void onChange() {
                TextView textView =(TextView) findViewById(R.id.NumberOfFiles);
                textView.setText(scanFiles.size()+" items selected");
            }
        });

        if (ActivityCompat.checkSelfPermission(MainActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_EXTERNAL_STORAGE},REQUEST_READ_EXTERNAL_STORAGE);
            }
            finish();
            return;
        }
        root = Environment.getExternalStorageDirectory();
        Log.d("walker", "onCreate: "+root.getAbsolutePath());
        DummyContent.addItem(DummyContent.createDummyItem("..",root.getParent(),0,false,true));
        ScanFolder(root);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (scanFiles.size()==0){
                    Snackbar.make(view, "Pick atleast 1 file/folder", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                }
                else {
                    Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                    intent.putExtra("DATA",scanFiles);
                    MainActivity.this.startActivity(intent);
                }

            }
        });

        FloatingActionButton home = (FloatingActionButton) findViewById(R.id.Home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DummyContent.ClearItems();
                root = Environment.getExternalStorageDirectory();
                DummyContent.addItem(DummyContent.createDummyItem("..",root.getParent(),0,false,true));
                ScanFolder(root);
                TextView textView =(TextView) findViewById(R.id.PathOfFiles);
                textView.setText("Current directory:\n"+root.getAbsolutePath());
                ItemFragment fragment = (ItemFragment) getFragmentManager().findFragmentById(R.id.fragment);
                fragment.UpdateItems();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


    }

    public static void ScanFolder(File root) {
        File[] list = root.listFiles();

        int i = 0;
        if (list != null) {
            for (File f : list) {
                i++;
                if (f.isDirectory()) {
                    Log.d("walker", "Dir: " + f.getAbsoluteFile());
                    DummyContent.addItem(DummyContent.createDummyItem(f.getName(), f.getAbsolutePath(),i,false,true));
                }
                else {
                    Log.d("walker", "File: " + f.getAbsoluteFile());
                    DummyContent.addItem(DummyContent.createDummyItem(f.getName(), f.getAbsolutePath(),i,false,false));
                }
            }
        }
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d("FragmentInteraction", "onListFragmentInteraction() called with: item = [" + item + "]");
        if (item.path == null) {
            return;
        }
        if (item.isDirectory){
            DummyContent.ClearItems();
            TextView textView =(TextView) findViewById(R.id.PathOfFiles);
            textView.setText("Current directory:\n"+item.path);
            root = new File(item.path);
            DummyContent.addItem(DummyContent.createDummyItem("..",root.getParent(),0,false,true));
            ScanFolder(root);
            ItemFragment fragment = (ItemFragment) getFragmentManager().findFragmentById(R.id.fragment);
            fragment.UpdateItems();
        }
    }

}
