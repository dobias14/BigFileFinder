package comdobias14.github.bigfilefinder;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import comdobias14.github.bigfilefinder.ItemFragment.OnListFragmentInteractionListener;
import comdobias14.github.bigfilefinder.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).path+"");
        if (mValues.get(position).checked){
            Log.d("Checked?", "onBindViewHolder:"+position+". mCheckBox checked? "+ mValues.get(position).checked);
        }
        holder.mCheckBox.setChecked(
                MainActivity.scanFiles.contains(
                        new File(String.valueOf(Uri.parse(holder.mItem.path)))
                )
        );
        if(holder.mItem.name.equals("..")){
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        }

        //holder.mCheckBox.setChecked(mValues.get(position).checked);

        /*
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mCheckBox.isChecked())
                    holder.mCheckBox.setChecked(true);
                else
                    holder.mCheckBox.setChecked(false);
            }
        });
        */

        if (mValues.get(position).isDirectory){
            holder.mIcon.setImageResource(R.drawable.ic_folder_black_24dp);
        }else{
            holder.mIcon.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final CheckBox mCheckBox;
        public DummyItem mItem;
        public ImageView mIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mCheckBox  = (CheckBox) view.findViewById(R.id.checkBox);
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mIdView.getText().equals("..")) {
                        if (mCheckBox.isChecked()) {
                            mItem.checked = true;
                            Toast.makeText(MainActivity.context, mIdView.getText() + " added", Toast.LENGTH_SHORT).show();
                            MainActivity.scanFiles.add(new File(String.valueOf(Uri.parse((String) mContentView.getText()))));
                            MainActivity.counter.inc();
                        } else {
                            mItem.checked = false;
                            Toast.makeText(MainActivity.context, mIdView.getText() + " removed", Toast.LENGTH_SHORT).show();
                            MainActivity.scanFiles.remove(new File(String.valueOf(Uri.parse((String) mContentView.getText()))));
                            MainActivity.counter.dec();
                        }
                    }
                }
            });
            mIcon = (ImageView) view.findViewById(R.id.imageView2);
            mIcon.setImageResource(R.drawable.ic_folder_black_24dp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
