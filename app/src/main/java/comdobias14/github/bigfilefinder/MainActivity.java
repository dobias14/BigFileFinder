package comdobias14.github.bigfilefinder;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import comdobias14.github.bigfilefinder.dummy.DummyContent;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,ItemFragment.OnListFragmentInteractionListener {

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



        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        //    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_READ_EXTERNAL_STORAGE);
        //}

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
            //onCreate(savedInstanceState);
        }
        root = Environment.getExternalStorageDirectory();
        Log.d("walker", "onCreate: "+root.getAbsolutePath());
        DummyContent.addItem(DummyContent.createDummyItem("..",root.getParent(),0,false,true));
        walk(root);


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
                for (DummyContent.DummyItem item : DummyContent.ITEMS) {
                    if (item.checked)
                    Log.d("Snackbar 1", "onClickSnackbar: "+item);
                }
                for (File scanFile : scanFiles) {
                    Log.d("Snackbar 2", "onClickSnackbar: "+scanFile.getAbsolutePath()+"is directory? "+scanFile.isDirectory());
                }
                //TODO Create Intent for handle files and number of files
            }
        });

        FloatingActionButton home = (FloatingActionButton) findViewById(R.id.Home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DummyContent.ClearItems();
                root = Environment.getExternalStorageDirectory();
                DummyContent.addItem(DummyContent.createDummyItem("..",root.getParent(),0,false,true));
                walk(root);
                TextView textView =(TextView) findViewById(R.id.PathOfFiles);
                textView.setText("Current directory:\n"+root.getAbsolutePath());
                ItemFragment fragment = (ItemFragment) getFragmentManager().findFragmentById(R.id.fragment);
                fragment.UpdateItems();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public static void walk(File root) {
        File[] list = root.listFiles();

        int i = 0;
        if (list != null) {
            for (File f : list) {
                i++;
                if (f.isDirectory()) {
                    Log.d("walker", "Dir: " + f.getAbsoluteFile());
                    DummyContent.addItem(DummyContent.createDummyItem(f.getName(), f.getAbsolutePath(),i,false,true));
                    //walk(f);
                }
                else {
                    Log.d("walker", "File: " + f.getAbsoluteFile());
                    DummyContent.addItem(DummyContent.createDummyItem(f.getName(), f.getAbsolutePath(),i,false,false));
                }
            }
        }
        for (DummyContent.DummyItem item : DummyContent.ITEMS) {
            Log.d("DIR", "walk: " + item.name);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d("Moje", "onListFragmentInteraction() called with: item = [" + item + "]");
        if (item.isDirectory){
            DummyContent.ClearItems();
            TextView textView =(TextView) findViewById(R.id.PathOfFiles);
            textView.setText("Current directory:\n"+item.path);
            //DummyContent.addItem(DummyContent.createDummyItem("..", root.getAbsolutePath(),0,false,true));
            root = new File(item.path);
            //if (!item.path.equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                DummyContent.addItem(DummyContent.createDummyItem("..",root.getParent(),0,false,true));
            //}
            walk(root);
            //myItemRecyclerViewAdapter.notifyDataSetChanged();
            //Fragment frg = null;
            ItemFragment fragment = (ItemFragment) getFragmentManager().findFragmentById(R.id.fragment);
            fragment.UpdateItems();
            //View listView = fragment.getActivity().findViewById(R.id.list);
            //fragment.myItemRecyclerViewAdapter
//            getFragmentManager()
//                    .beginTransaction()
//                    .detach(fragment)
//                    .attach(fragment)
//                    .commit();
            //tr.replace(R.id.fragment, R.id.fragment);
            //tr.commit();
        }
        //item.checked = !item.checked;
        //View fragment = findViewById(R.id.fragment);
        //Log.i("poss", "onListFragmentInteraction: "+ fragment.getVerticalScrollbarPosition());
    }

}
