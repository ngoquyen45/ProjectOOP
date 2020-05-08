package com.viettel.dms.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.WrapContentLinearLayoutManager;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.models.ExhibitionDto;
import com.viettel.dmsplus.sdk.models.ExhibitionRatingDto;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VisitExhibitionRatingEditFragment extends BaseFragment {
    private static final String PARAM_EXHIBITION = "PARAM_EXHIBITION";
    private static final String PARAM_PREVIOUS_RATE = "PARAM_PREVIOUS_RATE";
    private static final String PARAM_VISITED = "PARAM_VISITED";

    LayoutInflater layoutInflater;
    RecyclerView.LayoutManager layoutManager;
    @Bind(R.id.rv_Content) RecyclerView rvContent;
    @Bind(R.id.tv_Exhibition_Name) TextView tvExhibitionName;
    RatingAdapter adapter;

    ExhibitionDto rateContent;
    ExhibitionRatingDto previousChange;
    boolean visited;
    Map<String, Float> changeRateHashMap = new HashMap<String, Float>();

    public static VisitExhibitionRatingEditFragment newInstance(ExhibitionDto ex, ExhibitionRatingDto previousChange, boolean visited) {
        VisitExhibitionRatingEditFragment fragment = new VisitExhibitionRatingEditFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_EXHIBITION, ex);
        args.putParcelable(PARAM_PREVIOUS_RATE, previousChange);
        args.putBoolean(PARAM_VISITED, visited);
        fragment.setArguments(args);
        return fragment;
    }

    public VisitExhibitionRatingEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rateContent = getArguments().getParcelable(PARAM_EXHIBITION);
            previousChange = getArguments().getParcelable(PARAM_PREVIOUS_RATE);
            visited = getArguments().getBoolean(PARAM_VISITED);
            if (previousChange != null) {
                for (ExhibitionRatingDto.ExhibitionRatingItemDto itemDto : previousChange.getItems()) {
                    changeRateHashMap.put(itemDto.getExhibitionItemId(), itemDto.getRate());
                }
            }
        }
        adapter = new RatingAdapter();
        layoutInflater = LayoutInflater.from(context);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_exhibition_rating_edit, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.visit_exhibition_rating_edit_title);
        ((BaseActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((BaseActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        tvExhibitionName.setText(rateContent.getName());
        layoutManager = new LinearLayoutManager(context);
        rvContent.setLayoutManager(layoutManager);
        rvContent.setAdapter(adapter);
        rvContent.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false,
                        R.drawable.divider_list_0));
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!visited)
            inflater.inflate(R.menu.mb_menu_action_done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_done) {
            // Collect Change
            int size = changeRateHashMap.entrySet().size();
            if (size > 0) {
                ExhibitionRatingDto submit = new ExhibitionRatingDto();
                submit.setExhibitionId(rateContent.getId());
                ExhibitionRatingDto.ExhibitionRatingItemDto[] details = new ExhibitionRatingDto.ExhibitionRatingItemDto[size];
                int i = 0;
                for (Object o : changeRateHashMap.keySet().toArray()) {
                    if (o instanceof String) {
                        String itemId = (String) o;
                        float rate = changeRateHashMap.get(itemId);
                        details[i] = new ExhibitionRatingDto.ExhibitionRatingItemDto();
                        details[i].setExhibitionItemId(itemId);
                        details[i].setRate(rate);
                        i++;
                    }
                }
                submit.setItems(details);
                Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_RATING_ADDED);
                Bundle extras = new Bundle();
                extras.putParcelable("rate", submit);
                intent.putExtras(extras);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
            getActivity().onBackPressed();
        }
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.RatingHeaderViewHolder> {

        @Override
        public RatingHeaderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_visit_exhibition_rating_edit, parent, false);
            return new RatingHeaderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RatingHeaderViewHolder holder, int position) {
            ExhibitionDto.ExhibitionCategoryDto categoryDto = rateContent.getCategories()[position];
            holder.tvHeader.setText(categoryDto.getName());
            holder.rvItems.setLayoutManager(new WrapContentLinearLayoutManager(context));
            holder.rvItems.setAdapter(new InnerRatingAdapter(categoryDto.getItems()));
        }

        @Override
        public int getItemCount() {
            return rateContent.getCategories().length;
        }

        class RatingHeaderViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.tv_Header) TextView tvHeader;
            @Bind(R.id.rv_Items) RecyclerView rvItems;

            public RatingHeaderViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

    }

    RatingBar.OnRatingBarChangeListener mOnRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean byUser) {
            if (byUser) {
                String itemId = (String) ratingBar.getTag();
                changeRateHashMap.put(itemId, rating);
            }
        }
    };

    class InnerRatingAdapter extends RecyclerView.Adapter<RatingItemViewHolder> {
        ExhibitionDto.ExhibitionItemDto[] viewData;

        public InnerRatingAdapter(ExhibitionDto.ExhibitionItemDto[] data) {
            viewData = data;
        }

        @Override
        public RatingItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_visit_exhibition_rating_edit_item, parent, false);
            return new RatingItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RatingItemViewHolder holder, int position) {
            ExhibitionDto.ExhibitionItemDto item = viewData[position];
            holder.tvItem.setText(item.getName());
            String idItem = item.getId();
            if (changeRateHashMap.containsKey(idItem)) {
                holder.rtItem.setRating(changeRateHashMap.get(idItem));
            } else
                holder.rtItem.setRating(0);

            holder.rtItem.setTag(idItem);
            holder.rtItem.setIsIndicator(visited);
        }

        @Override
        public int getItemCount() {
            return viewData.length;
        }
    }

    class RatingItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.rt_Item) RatingBar rtItem;
        @Bind(R.id.tv_Item) TextView tvItem;

        public RatingItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            rtItem.setOnRatingBarChangeListener(mOnRatingBarChangeListener);
        }
    }
}
