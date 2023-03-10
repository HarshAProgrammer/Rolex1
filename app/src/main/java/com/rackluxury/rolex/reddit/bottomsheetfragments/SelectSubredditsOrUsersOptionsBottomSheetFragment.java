package com.rackluxury.rolex.reddit.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.rackluxury.rolex.R;
import com.rackluxury.rolex.reddit.activities.SelectedSubredditsAndUsersActivity;

public class SelectSubredditsOrUsersOptionsBottomSheetFragment extends RoundedBottomSheetDialogFragment {

    @BindView(R.id.select_subreddits_text_view_search_user_and_subreddit_sort_type_bottom_sheet_fragment)
    TextView selectSubredditsTextView;
    @BindView(R.id.select_users_text_view_search_user_and_subreddit_sort_type_bottom_sheet_fragment)
    TextView selectUsersTextView;
    private SelectedSubredditsAndUsersActivity activity;

    public SelectSubredditsOrUsersOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_subreddits_or_users_options_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        selectSubredditsTextView.setOnClickListener(view -> {
            activity.selectSubreddits();
            dismiss();
        });

        selectUsersTextView.setOnClickListener(view -> {
            activity.selectUsers();
            dismiss();
        });

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (SelectedSubredditsAndUsersActivity) context;
    }
}