package comdobias14.github.bigfilefinder;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity {

    private ArrayList<File> filesToBeSearched;
    private static ArrayList<File> filesFound;
    private static int numberOfFilesPicked;

    private Handler handlerForProgressBar;
    private ProgressBar progressBar;
    private TextView textViewProgress;
    private Thread vlakno;
    private long startTime;
    private long stopTime;
    private long elapsedTime;

    public SearchActivity() {
        filesToBeSearched = null;
        filesFound = new ArrayList<>();
        numberOfFilesPicked = 0;
        handlerForProgressBar = new Handler();
        textViewProgress = null;
    }

    //TODO: Refactor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //noinspection unchecked
        filesToBeSearched = (ArrayList<File>) getIntent().getExtras().getSerializable("DATA");

        //Setup progressBar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textViewProgress = (TextView) findViewById(R.id.textView4);

        //Setup numberPicker
        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.NumberPicker);
        numberPicker.setMaxValue(9999);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(true);

        //setup textView
        TextView textView = (TextView) findViewById(R.id.ListOfFiles);
        String text = "";
        for (File file : filesToBeSearched) {
            text+= file.getAbsolutePath()+"\n";
        }
        textView.setText(text);

        //Setup SearchButton
        Button SearchButton = (Button) findViewById(R.id.SearchButton);
        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Init
                final TextView textView1 = (TextView) findViewById(R.id.textView3);
                numberOfFilesPicked =  numberPicker.getValue();

                //Clear
                textView1.setText("");
                filesFound = new ArrayList<>();
                progressBar.setProgress(0);

                //Search
                vlakno = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Walker
                        int start = 0;
                        int end = filesToBeSearched.size();
                        for (File file : filesToBeSearched) {
                            if (Thread.interrupted()) {
                                return;
                            }
                            walker(file);
                            start++;
                            final int finalStart = start*100/end;
                            final File finalFile = file;
                            if (Thread.interrupted()) {
                                return;
                            }
                            handlerForProgressBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(finalStart);
                                    textViewProgress.setText(finalFile.getAbsolutePath());
                                    //Toast.makeText(SearchActivity.this, finalStart+"", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //Fill up textView1
                        int counter = 1;
                        String text = "";
                        String sizeOfFile;
                        for (File file : filesFound) {
                            sizeOfFile = new StringBuilder(file.length() + "").reverse().toString();
                            sizeOfFile = sizeOfFile.replaceAll("(\\d{3})","$1 ").trim();
                            sizeOfFile = new StringBuilder(sizeOfFile).reverse().toString();
                            Log.d("sizeOfFile", "run: "+sizeOfFile);
                            text+= counter+". "+file.getAbsolutePath()+"\nsize:  "+ sizeOfFile +" Bytes"+"\n\n";
                            counter++;
                        }
                        final String finalText = text;
                        handlerForProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                textView1.setText(finalText);
                            }
                        });
                        stopTime = System.currentTimeMillis();
                        elapsedTime = stopTime - startTime;

                        handlerForProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SearchActivity.this, "Time of search: "+ elapsedTime/1000.0+" sec", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                startTime = System.currentTimeMillis();
                vlakno.start();
            }
        });
        //Setup CancelButton
        Button CancelButton = (Button) findViewById(R.id.CancelButton);
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vlakno != null){
                    vlakno.interrupt();
                    if (vlakno.isInterrupted()){
                        Toast.makeText(SearchActivity.this, "Search canceled", Toast.LENGTH_SHORT).show();
                    }
                };
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vlakno != null) {
            vlakno.interrupt();
        }
    }

    public static void walker(File root) {
        File[] list = new File[1];
        if (root.isDirectory()) {
            list = root.listFiles();
        }else{
            list[0] = root;
        }
        ArrayList<File> filesInDirectory = new ArrayList<>();

        if (list != null) {

            //File[] files = new File[0];
            for (File f : list) {
                if (f.isDirectory()) {
                    Log.d("walker", "Dir: " + f.getAbsoluteFile());
                    walker(f);
                }
                else {
                    Log.d("walker", "File: " + f.getAbsoluteFile() + " size: "+f.length());
                    filesInDirectory.add(f);
                }
            }
            SortAndPick(filesInDirectory,filesFound);

            if (filesFound.size() > numberOfFilesPicked){
                Collections.sort(filesFound, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return (int)(o2.length() - o1.length());
                    }
                });
                ArrayList<File> tempfiles = new ArrayList<>();
                for (int i = 0; i < numberOfFilesPicked ; i++) {
                    tempfiles.add(filesFound.get(i));
                }
                filesFound.clear();
                filesFound.addAll(tempfiles);
            }
            Collections.sort(filesFound, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return (int)(o2.length() - o1.length());
                }
            });


        }

    }

    private static void SortAndPick(ArrayList<File> filesInDirectory, ArrayList<File> filesFound) {
        //Sort by file size
        Collections.sort(filesInDirectory, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int)(o2.length() - o1.length());
            }
        });

        //Pick only *numberOfFilesPicked* files with largest size
        int counter = 0;
        for (File item : filesInDirectory) {
            if (counter == numberOfFilesPicked){
                break;
            }
            Log.d("DIR", "walk: " + item.getName()+" size: "+item.length());
            if (!filesFound.contains(item)){
                filesFound.add(item);
            }
            counter++;
        }
    }
}
