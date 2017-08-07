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

import java.io.File;
import java.util.List;

import comdobias14.github.bigfilefinder.FileStructure.FileContent;
import comdobias14.github.bigfilefinder.FileStructure.FileContent.FileItem;
import comdobias14.github.bigfilefinder.ItemFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link FileContent.FileItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<FileItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyItemRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mValues = FileContent.ITEMS;
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
        if (holder.mItem.path == null){
            return;
        }
        holder.mIdView.setText(mValues.get(position).name);
        holder.mContentView.setText(mValues.get(position).path);
        if (mValues.get(position).checked){
            Log.d("Checked?", "onBindViewHolder:"+position+". mCheckBox checked? "+ mValues.get(position).checked);
        }
        holder.mCheckBox.setChecked(
                MainActivity.scanFiles.contains(
                        new File(String.valueOf(Uri.parse(holder.mItem.path)))
                )
        );
        if (position == 0){
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        }

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
        public FileItem mItem;
        public final ImageView mIcon;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mContentView = view.findViewById(R.id.content);
            mCheckBox  = view.findViewById(R.id.checkBox);
            mCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!mIdView.getText().equals("..")) {
                        if (mCheckBox.isChecked()) {
                            mItem.checked = true;
                            //Toast.makeText(MainActivity.context, MainActivity.context.getString(R.string.adding_files,mIdView.getText()), Toast.LENGTH_SHORT).show();
                            MainActivity.scanFiles.add(new File(String.valueOf(Uri.parse((String) mContentView.getText()))));
                            MainActivity.counter.inc();
                        } else {
                            mItem.checked = false;
                            //Toast.makeText(MainActivity.context, MainActivity.context.getString(R.string.removing_files,mIdView.getText()), Toast.LENGTH_SHORT).show();
                            MainActivity.scanFiles.remove(new File(String.valueOf(Uri.parse((String) mContentView.getText()))));
                            MainActivity.counter.dec();
                        }
                    }
                }
            });
            mIcon = view.findViewById(R.id.imageView2);
            mIcon.setImageResource(R.drawable.ic_folder_black_24dp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
