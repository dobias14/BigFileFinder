package comdobias14.github.bigfilefinder;

import static comdobias14.github.bigfilefinder.MainActivity.scanFiles;

public class CounterVariable {
    private int counter = scanFiles.size();
    private ChangeListener listener;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
        if (listener != null) listener.onChange();
    }

    public void inc(){
        this.counter++;
        if (listener != null) listener.onChange();
    }
    public void dec(){
        this.counter--;
        if (listener != null) listener.onChange();
    }

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public interface ChangeListener {
        void onChange();
    }

}
