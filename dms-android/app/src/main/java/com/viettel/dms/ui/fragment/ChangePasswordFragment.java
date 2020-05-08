package com.viettel.dms.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.ChangePasswordPresenter;
import com.viettel.dms.ui.iview.IChangePasswordView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.UserInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChangePasswordFragment extends BaseFragment implements IChangePasswordView {

    private static String ERROR_OLD_PASSWORD = "invalid.old.password";
    private static int NUMBER_CHARACTERS = 6;

    @Bind(R.id.tv_Customer_Name) TextView tvCustomerName;
    @Bind(R.id.tv_Customer_Login_Name) TextView tvCustomerLoginName;
    @Bind(R.id.edt_Current_Password) MaterialEditText edtCurrentPassword;
    @Bind(R.id.edt_New_Password) MaterialEditText edtNewPassword;
    @Bind(R.id.edt_Retype_Password) MaterialEditText edtRetypePassword;

    Dialog mProgressDialog;
    ChangePasswordPresenter presenter;

    public static ChangePasswordFragment newInstance() {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChangePasswordPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        ButterKnife.bind(this, view);

        UserInfo userInfo = OAuthSession.getDefaultSession().getUserInfo();
        tvCustomerName.setText(userInfo.getFullname());
        tvCustomerLoginName.setText(userInfo.getUsername());

        setTitleResource(R.string.change_password_title);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mb_menu_action_done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            if (validateChangePassword()) {
                showProgressDialog();
                presenter.requestChangePassword(edtCurrentPassword.getText().toString(), edtNewPassword.getText().toString());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean validateChangePassword() {
        String oldPass, newPass, retypePass;
        oldPass = edtCurrentPassword.getText().toString();
        newPass = edtNewPassword.getText().toString();
        retypePass = edtRetypePassword.getText().toString();
        if (StringUtils.isNullOrEmpty(oldPass)) {
            edtCurrentPassword.setError(getResources().getString(R.string.change_password_null_oldpassword));
            edtCurrentPassword.requestFocus();
            return false;
        }
        if (!newPass.equalsIgnoreCase(retypePass)) {
            edtRetypePassword.setError(getResources().getString(R.string.change_password_not_match));
            edtRetypePassword.requestFocus();
            return false;
        }
        if (newPass.length() < NUMBER_CHARACTERS) {
            edtNewPassword.setError(String.format(getResources().getString(R.string.change_password_number_characters), NUMBER_CHARACTERS));
            edtNewPassword.requestFocus();
            return false;
        }
//        if (newPass.equalsIgnoreCase(oldPass)) {
//            edtNewPassword.setError(getResources().getString(R.string.change_password_not_change));
//            edtNewPassword.requestFocus();
//            return false;
//        }
        return true;
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void changePasswordSuccess() {
        dismissDialog();
        Toast.makeText(context, R.string.change_password_successful, Toast.LENGTH_SHORT).show();
        clearScreen();
    }

    private void clearScreen() {
        edtCurrentPassword.setText(null);
        edtRetypePassword.setText(null);
        edtNewPassword.setText(null);
    }

    @Override
    public void changePasswordError(SdkException info) {
        dismissDialog();
        if (info.getError().getMessage().equalsIgnoreCase(ERROR_OLD_PASSWORD)) {
            edtCurrentPassword.setError(getResources().getString(R.string.change_password_wrong_password));
            edtCurrentPassword.requestFocus();
        } else {
            NetworkErrorDialog.processError(context, info);
        }
    }

    void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showProgressDialog() {
        mProgressDialog = DialogUtils.showProgressDialog(context, getResources().getString(R.string.notify), getResources().getString(R.string.message_please_wait), true);
    }

}
