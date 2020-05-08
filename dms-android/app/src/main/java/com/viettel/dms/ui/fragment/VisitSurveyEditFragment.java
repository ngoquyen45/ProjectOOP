package com.viettel.dms.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.WrapContentLinearLayoutManager;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.models.SurveyAnswerDto;
import com.viettel.dmsplus.sdk.models.SurveyListResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VisitSurveyEditFragment extends BaseFragment {
    private String mVisitId;
    private SurveyListResult.SurveyDto mSurvey;
    private Map<String, Set<String>> mapAnsweredQuestions;
    private boolean visitEnded;
    private String customerName;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private WrapContentLinearLayoutManager wrapContentLinearLayoutManager;
    @Bind(R.id.rv_Content) RecyclerView rvContent;
    @Nullable @Bind(R.id.tv_Sub_Title) TextView tvSubTitle;

    private SurveyQuestionAndAnswerAdapter adapter;
    private InnerAdapter innerAdapter;

    private static final int ITEM_TYPE_CHECKBOX = 1;
    private static final int ITEM_TYPE_RADIO = 2;
    private static final int ITEM_TYPE_HEADER = 3;
    private static final int ITEM_TYPE_RECYCLERVIEW = 4;

    private static final String PARAM_CUSTOMER_NAME = "PARAM_CUSTOMER_NAME";
    private static final String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    private static final String PARAM_SURVEY = "PARAM_SURVEY";
    private static final String PARAM_SURVEY_ANSWER = "PARAM_SURVEY_ANSWER";
    private static final String PARAM_VISIT_END = "PARAM_VISIT_END";


    public static BaseFragment newInstance(String visitId,String name, SurveyListResult.SurveyDto survey, SurveyAnswerDto surveyAnswer, boolean visitEnded) {
        VisitSurveyEditFragment fragment = new VisitSurveyEditFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_VISIT_ID, visitId);
        bundle.putString(PARAM_CUSTOMER_NAME,name);
        bundle.putParcelable(PARAM_SURVEY, survey);
        bundle.putParcelable(PARAM_SURVEY_ANSWER, surveyAnswer);
        bundle.putBoolean(PARAM_VISIT_END, visitEnded);
        fragment.setArguments(bundle);
        return fragment;
    }

    public VisitSurveyEditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        this.layoutInflater = getLayoutInflater(savedInstanceState);

        mapAnsweredQuestions = new HashMap<>();
        adapter = new SurveyQuestionAndAnswerAdapter();
        if (getArguments() != null) {
            this.mVisitId = getArguments().getString(PARAM_VISIT_ID);
            this.customerName = getArguments().getString(PARAM_CUSTOMER_NAME);
            this.mSurvey = getArguments().getParcelable(PARAM_SURVEY);
            SurveyAnswerDto surveyAnswer = getArguments().getParcelable(PARAM_SURVEY_ANSWER);
            if (surveyAnswer != null && surveyAnswer.getOptions() != null) {
                List<String> allSelectedAnswers = Arrays.asList(surveyAnswer.getOptions());
                Set<String> selectedAnswers = new HashSet<>();
                for (SurveyListResult.SurveyQuestion question : mSurvey.getQuestions()) {
                    for (SurveyListResult.SurveyAnswer answer : question.getAnswers()) {
                        if (allSelectedAnswers.contains(answer.getId())) {
                            selectedAnswers.add(answer.getId());
                        }
                    }
                    if (selectedAnswers.size() > 0) {
                        mapAnsweredQuestions.put(question.getId(), selectedAnswers);
                        selectedAnswers = new HashSet<>();
                    }
                }
            }
            this.visitEnded = getArguments().getBoolean(PARAM_VISIT_END, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visit_survey_edit, container, false);
        if(getResources().getBoolean(R.bool.is_tablet)) setPaddingLeft(80f);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.visit_survey_edit_title);
        SetTextUtils.setText(tvSubTitle, customerName);
        ((BaseActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((BaseActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        setHasOptionsMenu(true);
        rvContent.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context);
        rvContent.setLayoutManager(layoutManager);
        rvContent.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!visitEnded) {
            inflater.inflate(R.menu.mb_menu_action_done, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (visitEnded) {
                return true;
            }
            Set<String> selectedAnswers = new HashSet<>();
            for (Map.Entry<String, Set<String>> entry : mapAnsweredQuestions.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    selectedAnswers.addAll(entry.getValue());
                }
            }
            String[] answerIds = new String[selectedAnswers.size()];
            answerIds = selectedAnswers.toArray(answerIds);
            SurveyAnswerDto answerDto = new SurveyAnswerDto();
            answerDto.setSurveyId(mSurvey.getId());
            answerDto.setOptions(answerIds);

            Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_SURVEY_ADDED);
            Bundle extras = new Bundle();
            extras.putString("visitId", mVisitId);
            extras.putParcelable("surveyAnswer", answerDto);
            intent.putExtras(extras);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            getActivity().onBackPressed();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private class SurveyQuestionAndAnswerAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_HEADER) {
                View view = layoutInflater.inflate(R.layout.adapter_survey_edit_item_header, parent, false);
                return new HeaderViewHolder(view);
            }
            if (viewType == ITEM_TYPE_RECYCLERVIEW) {
                View view = layoutInflater.inflate(R.layout.adapter_survey_question_answer, parent, false);
                return new SurveyQuestionAndAnswerViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0 && holder.getItemViewType() == ITEM_TYPE_HEADER) {
                HeaderViewHolder vh = (HeaderViewHolder) holder;
                vh.tvSurveyName.setText(mSurvey.getName());
                return;
            }
            if (position > 0 && holder.getItemViewType() == ITEM_TYPE_RECYCLERVIEW) {
                SurveyQuestionAndAnswerViewHolder vh = (SurveyQuestionAndAnswerViewHolder) holder;
                vh.itemView.setTag(position - 1);
                SurveyListResult.SurveyQuestion question = mSurvey.getQuestions()[position - 1];

                vh.textView.setText((position) + ". " + question.getName());
                wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(context);
                vh.recyclerView.setLayoutManager(wrapContentLinearLayoutManager);
                innerAdapter = new InnerAdapter(question);
                vh.recyclerView.setAdapter(innerAdapter);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return ITEM_TYPE_HEADER;
            } else return ITEM_TYPE_RECYCLERVIEW;
        }

        @Override
        public int getItemCount() {
            if (mSurvey != null && mSurvey.getQuestions() != null) {
                return mSurvey.getQuestions().length + 1;
            }
            return +1;
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSurveyName;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvSurveyName = (TextView) itemView.findViewById(R.id.tv_Survey_Name);
        }
    }

    private class SurveyQuestionAndAnswerViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;

        public SurveyQuestionAndAnswerViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_Name);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.rv_item);
        }
    }

    private class InnerAdapter extends RecyclerView.Adapter {
        SurveyListResult.SurveyQuestion question;
        int mCurrentSelectRadioButton = -1;

        private View.OnClickListener radioButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag(R.id.survey_edit_radio_button_position);
                mCurrentSelectRadioButton = position;
                notifyDataSetChanged();
                if (visitEnded) {
                    return;
                }
                String questionId = (String) view.getTag(R.id.survey_edit_question_id);
                if (questionId == null) {
                    return;
                }
                mapAnsweredQuestions.remove(questionId);
                String answerId = (String) view.getTag(R.id.survey_edit_answer_id);
                if (answerId == null) {
                    return;
                }
                Set<String> selectedAnswers = mapAnsweredQuestions.get(questionId);
                if (selectedAnswers == null) {
                    selectedAnswers = new HashSet<>();
                    mapAnsweredQuestions.put(questionId, selectedAnswers);
                } else {
                    selectedAnswers.clear();
                }
                selectedAnswers.add(answerId);
            }
        };
        CompoundButton.OnCheckedChangeListener checkboxCheckedListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (visitEnded) {
                    return;
                }
                String questionId = (String) buttonView.getTag(R.id.survey_edit_question_id);
                String answerId = (String) buttonView.getTag(R.id.survey_edit_answer_id);
                if (questionId == null || answerId == null) {
                    return;
                }
                Set<String> selectedAnswers = mapAnsweredQuestions.get(questionId);
                if (selectedAnswers == null) {
                    if (isChecked) {
                        selectedAnswers = new HashSet<>();
                        selectedAnswers.add(answerId);
                        mapAnsweredQuestions.put(questionId, selectedAnswers);
                    }
                } else {
                    if (isChecked) {
                        selectedAnswers.add(answerId);
                    } else {
                        selectedAnswers.remove(answerId);
                        if (selectedAnswers.size() == 0) {
                            mapAnsweredQuestions.remove(questionId);
                        }
                    }
                }
            }
        };

        public InnerAdapter(SurveyListResult.SurveyQuestion q) {
            this.question = q;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE_CHECKBOX) {
                View view = layoutInflater.inflate(R.layout.adapter_survey_edit_item_checkbox, parent, false);
                return new CheckBoxViewHolder(view);
            }
            if (viewType == ITEM_TYPE_RADIO) {
                View view = layoutInflater.inflate(R.layout.adapter_survey_edit_item_radiobuttom, parent, false);
                return new RadioButtonViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SurveyListResult.SurveyAnswer answer = question.getAnswers()[position];
            Set<String> selectedAnswers = mapAnsweredQuestions.get(question.getId());
            if (selectedAnswers == null) {
                selectedAnswers = Collections.emptySet();
            }

            if (holder.getItemViewType() == ITEM_TYPE_CHECKBOX) {
                CheckBoxViewHolder vh = (CheckBoxViewHolder) holder;
                vh.checkBox.setText(answer.getName());
                vh.checkBox.setTag(R.id.survey_edit_question_id, question.getId());
                vh.checkBox.setTag(R.id.survey_edit_answer_id, answer.getId());
                vh.checkBox.setOnCheckedChangeListener(checkboxCheckedListener);
                if (selectedAnswers.contains(answer.getId())) {
                    vh.checkBox.setChecked(true);
                } else vh.checkBox.setChecked(false);
            }
            if (holder.getItemViewType() == ITEM_TYPE_RADIO) {
                RadioButtonViewHolder vh = (RadioButtonViewHolder) holder;
                vh.radioButton.setText(answer.getName());
                vh.radioButton.setTag(R.id.survey_edit_question_id, question.getId());
                vh.radioButton.setTag(R.id.survey_edit_answer_id, answer.getId());

                vh.radioButton.setTag(R.id.survey_edit_radio_button_position, position);
                vh.radioButton.setOnClickListener(radioButtonClick);
                if (selectedAnswers.contains(answer.getId())) {
                    mCurrentSelectRadioButton = position;
                }
                vh.radioButton.setChecked(position == mCurrentSelectRadioButton);
            }
        }

        @Override
        public int getItemCount() {
            return question.getAnswers().length;
        }

        @Override
        public int getItemViewType(int position) {
            if (question.isMultipleChoice()) {
                return ITEM_TYPE_CHECKBOX;
            } else {
                return ITEM_TYPE_RADIO;
            }
        }
    }

    private class CheckBoxViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public CheckBoxViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkboxItem);
            if (visitEnded) {
                checkBox.setEnabled(false);
            }
        }
    }

    private class RadioButtonViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public RadioButtonViewHolder(View itemView) {
            super(itemView);
            radioButton = (RadioButton) itemView.findViewById(R.id.radioButton);
            if (visitEnded) {
                radioButton.setEnabled(false);
            }
        }
    }
}
