package com.mk.familyweighttracker.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mk.familyweighttracker.Activities.UserDetailActivity;
import com.mk.familyweighttracker.Models.User;
import com.mk.familyweighttracker.R;
import com.mk.familyweighttracker.Services.UserService;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserDetailsSummaryFragment extends Fragment implements UserDetailsRecordsFragment.OnNewReadingAdded {

    //private User mUser;
    private long mSelectedUserId;

    private View mFragmentView;

    public UserDetailsSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_user_details_summary, container, false);

        mSelectedUserId = getActivity().getIntent().getLongExtra(UserDetailActivity.ARG_USER_ID, 0);

        onNewReadingAdded();

        initDeleteUserControl();

        return mFragmentView;
    }

    @Override
    public void onNewReadingAdded() {
        User user = new UserService().get(mSelectedUserId);

        ((TextView) mFragmentView.findViewById(R.id.userId))
                .setText("Readings: " + user.getReadings().size());

        ImageView userImage = ((ImageView) mFragmentView.findViewById(R.id.userImage));
        if( user.getImageBytes() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getImageBytes(), 0, user.getImageBytes().length);
            userImage.setImageBitmap(bitmap);
        } else {
            userImage.setImageResource(R.drawable.dummy_contact);
        }
    }

    private void initDeleteUserControl() {
        ((Button) mFragmentView.findViewById(R.id.deleteUser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserService().remove(mSelectedUserId);
                ((OnUserDeleted) getActivity()).onUserDeleted();
            }
        });
    }

    public interface OnUserDeleted {
        void onUserDeleted();
    }
}
