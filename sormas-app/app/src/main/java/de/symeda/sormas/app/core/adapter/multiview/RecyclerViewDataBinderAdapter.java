package de.symeda.sormas.app.core.adapter.multiview;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Orson on 27/11/2017.
 */

public abstract class RecyclerViewDataBinderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getDataBinder(viewType).createViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        DataBinder dataBinder = getDataBinder(viewHolder.getItemViewType());
        //int binderPosition = getBinderPosition(position);
        //dataBinder.moveNextDataPosition();
        if (dataBinder.moveNextDataPosition()) {
            //int kkkk = dataBinder.getCurrentDataPosition();
            dataBinder.bindToViewHolder(viewHolder, dataBinder.getCurrentDataPosition());
        }
    }

    @Override
    public abstract int getItemCount();

    @Override
    public abstract int getItemViewType(int position);

    public abstract <T extends DataBinder> T getDataBinder(int viewType);

    public abstract int getPosition(DataBinder binder, int binderPosition);

    public abstract int getBinderPosition(int position);

    public abstract void notifyBinderItemRangeChanged(DataBinder binder, int positionStart, int itemCount);

    public abstract void notifyBinderItemRangeInserted(DataBinder binder, int positionStart, int itemCount);

    public abstract void notifyBinderItemRangeRemoved(DataBinder binder, int positionStart, int itemCount);

    public void notifyBinderItemChanged(DataBinder binder, int binderPosition) {
        notifyItemChanged(getPosition(binder, binderPosition));
    }

    public void notifyBinderItemInserted(DataBinder binder, int binderPosition) {
        notifyItemInserted(getPosition(binder, binderPosition));
    }

    public void notifyBinderItemRemoved(DataBinder binder, int binderPosition) {
        notifyItemRemoved(getPosition(binder, binderPosition));
    }

    public void notifyBinderItemMoved(DataBinder binder, int fromPosition, int toPosition) {
        notifyItemMoved(getPosition(binder, fromPosition), getPosition(binder, toPosition));
    }

}
