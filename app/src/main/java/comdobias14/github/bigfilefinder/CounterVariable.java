package comdobias14.github.bigfilefinder;

import static comdobias14.github.bigfilefinder.MainActivity.scanFiles;

/**
 * Purpose of this class is to refresh layout only when value is changed.
 * To specify it is maintaining change of {@link android.support.v7.widget.AppCompatTextView}
 * with counter value.
 */
class CounterVariable {
    private int counter = scanFiles.size();
    private ChangeListener listener;

    void inc(){
        this.counter++;
        if (listener != null) listener.onChange();
    }

    void dec(){
        this.counter--;
        if (listener != null) listener.onChange();
    }

    void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    interface ChangeListener {
        void onChange();
    }

}
