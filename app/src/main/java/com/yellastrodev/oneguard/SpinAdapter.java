package com.yellastrodev.oneguard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.yellastrodev.oneguard.R;
import com.yellastrodev.oneguard.SpinAdapter;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;
import android.graphics.Color;

public class SpinAdapter extends RecyclerView.Adapter<SpinAdapter.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
	private int mItemWidth = 0;

	private int mSelectedPos;
	private View mSelectView;

	private Context mCtx;

    // data is passed into the constructor
	public SpinAdapter(Context context, List<String> data) {
		mCtx = context;
        this.mInflater = LayoutInflater.from(mCtx);
        this.mData = data;
	}


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.it_text, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

	public int getSelected()
	{
		return mSelectedPos;
	}


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View fItemView) {
            super(fItemView);
            myTextView = (TextView) fItemView;
			// itemView.findViewById(android.R.id.text1);
			myTextView.setTextColor(Color.BLACK);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

	public int getItemWidth()
	{
		return mItemWidth;
	}

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

	public void resetSelect(int fCenterPos,View fCenterView)
	{
		if(fCenterPos>=getItemCount())
		{
			fCenterPos = getItemCount()-1;
		}
		/*if(mSelectView!=null)
		 {
		 mSelectView.setSelected(false);
		 if(mData.size()>32)
		 mFrame.setCrippyYear(fCenterPos);
		 else if(mData.size()==12)
		 mFrame.setMonth(fCenterPos);
		 }*/
		fCenterView.setSelected(true);
		mSelectedPos=fCenterPos;
		mSelectView = fCenterView;

	}
	
	OnItemSelectedListener mOnSelect= null;
	
	public void setOnSelect(OnItemSelectedListener f)
	{
		mOnSelect = f;
	}

	public void setSelected(int fCenterPos,View fCenterView)
	{
		if(mSelectedPos!=fCenterPos)
		{
			if(mSelectView!=null)
			{
				mSelectView.setSelected(false);
			}
			fCenterView.setSelected(true);
			mSelectedPos=fCenterPos;
			mSelectView = fCenterView;
			if(mOnSelect!=null)
				mOnSelect.onItemSelected(null,null,fCenterPos,0);
		}
	}

	public void changeData(List<String> fNewData)
	{
		while(mData.size()!=fNewData.size())
		{
			if(mData.size()<fNewData.size())
			{
				int i = mData.size()+1;
				mData.add("  "+i+"  ");
				notifyItemInserted(i-1);
			}else
			{
				int i = mData.size()-1;
				mData.remove(i);
				notifyItemRemoved(i);
			}
		}
		/*mData = fNewData;
		 mSelectView.setSelected(false);
		 mSelectView = null;
		 notifyDataSetChanged();*/

	}


    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
	
    
    
}
