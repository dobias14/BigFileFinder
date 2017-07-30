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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity {

    ArrayList<File> filesToBeSearched;
    static ArrayList<File> filesFound;
    private static int numberOfFilesPicked;

    Handler handlerForProgressBar;
    private ProgressBar progressBar;
    private TextView textViewProgress;

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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Walker
                        int start = 0;
                        int end = filesToBeSearched.size();
                        for (File file : filesToBeSearched) {
                            walker(file);
                            start++;
                            final int finalStart = start*100/end;
                            final File finalFile = file;
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
                        for (File file : filesFound) {
                            text+= counter+". "+file.getAbsolutePath()+" size: "+ file.length() +" Bytes"+"\n";
                            counter++;
                        }
                        final String finalText = text;
                        handlerForProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                textView1.setText(finalText);
                            }
                        });

                    }
                }).start();
            }
        });
    }

    public static void walker(File root) {
        //android.os.SystemClock.sleep(500);
        File[] list = root.listFiles();
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
            } //(int)o2.length() - (int)o1.length();
            //filesInDirectory.toArray(files)
            SortAndPick(filesInDirectory,filesFound);
            SortAndPick(filesFound,filesFound);


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
