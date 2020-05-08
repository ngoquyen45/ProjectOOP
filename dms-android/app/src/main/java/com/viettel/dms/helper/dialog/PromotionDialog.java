package com.viettel.dms.helper.dialog;

/**
 * @author PHAMHUNG
 * @since 4/8/2015
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dmsplus.sdk.models.PromotionListItem;

public class PromotionDialog implements View.OnClickListener {

    protected Context context;

    private Dialog dialog;
    private ImageButton btnClose;
    private TextView txtName, txtTime, txtFocusObject, txtContent;
    private PromotionListItem item;


    @SuppressLint("InflateParams")
    public PromotionDialog(Context context, PromotionListItem i) {
        this.context = context;
        this.item = i;
        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.dialog_promotion);
        this.dialog.setCancelable(false);
        initViews();

    }

    private void initViews() {
        btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(this);
        txtName = (TextView) dialog.findViewById(R.id.id_promotion_name);
        txtTime = (TextView) dialog.findViewById(R.id.id_time);
        txtFocusObject = (TextView) dialog.findViewById(R.id.id_focus_to);
        txtContent = (TextView) dialog.findViewById(R.id.id_content);
        if (item != null) {
            String tempStartTime = DateTimeUtils.formatDate(item.getStartDate());
            String tempEndTime = DateTimeUtils.formatDate(item.getEndDate());
            txtName.setText(item.getName());
            String content = context.getResources().getString(R.string.promotion_available_dialog_time_content);
            content = content.replace("{0}", tempStartTime);
            content = content.replace("{1}", tempEndTime);
            txtTime.setText(content);
            txtFocusObject.setText(item.getApplyFor());
            txtContent.setText(item.getDescription());
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btnClose.getId()) {
            this.dismiss();
        }
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

/*  class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.view_dialog_promotion_list_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MyViewHolder vh = (MyViewHolder) holder;
            PromotionListItem item = data;
            vh.tvPromotionName.setText(item.getName());
            vh.tvPromotionDate.setText(
                    "Từ ngày "
                            + DateTimeUtils.formatDate(item.getStartedDate())
                            + " đến ngày "
                            + DateTimeUtils.formatDate(item.getEndedDate())
            );
            vh.tvApplyTo.setText(item.getApplyFor());
            vh.tvContent.setText(item.getComponent());
        }

        @Override
        public int getItemCount() {
            if (data != null) return 1;
            else return 0;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPromotionName;
        TextView tvPromotionDate;
        TextView tvApplyTo;
        TextView tvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvPromotionName = (TextView) itemView.findViewById(R.id.tvPromotionName);
            tvPromotionDate = (TextView) itemView.findViewById(R.id.tvPromotionDate);
            tvApplyTo = (TextView) itemView.findViewById(R.id.tvApplyTo);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
        }
    }*/
}
