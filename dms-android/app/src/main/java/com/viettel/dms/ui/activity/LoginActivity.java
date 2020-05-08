package com.viettel.dms.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.ProgressView;
import com.viettel.dms.BuildConfig;
import com.viettel.dms.DMSApplication;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.dialog.ServerSettingDiaglog;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dms.presenter.LoginPresenter;
import com.viettel.dms.ui.iview.ILoginView;
import com.viettel.dmsplus.sdk.ErrorType;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.UserInfo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnLongClick;

public class LoginActivity extends BaseActivity implements ILoginView {

    @Bind(R.id.edt_username)
    MaterialEditText medtUser;
    @Bind(R.id.edt_password)
    MaterialEditText medtPassword;
    @Bind(R.id.ll_login)
    LinearLayout llLogin;
    @Bind(R.id.ll_Login_Error)
    LinearLayout llLoginError;
    @Bind(R.id.pb_Login)
    ProgressView prBLogin;
    @Bind(R.id.tv_Message_Error)
    TextView tvMessageError;
    @Bind(R.id.Itv_User_Name)
    IconTextView itvUserName;
    @Bind(R.id.Itv_User_Pass)
    IconTextView itvUserPass;
    LoginPresenter mLoginPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String lastUserThemeKey = DMSSharePreference.get().getLastUserThemeKey();
        ThemeUtils.changeToThemeWithoutRestartActivity(lastUserThemeKey == null ? ThemeUtils.THEME_DEFAULT : DMSSharePreference.get().getDefaultThemeByUserKey(lastUserThemeKey));

        setContentView(R.layout.activity_login);
        mLoginPresenter = new LoginPresenter(this);
        ButterKnife.bind(this);

        String lastAuthenticatedUsername = DMSSharePreference.get().getLastAuthenticatedUsername();
        medtUser.setText(lastAuthenticatedUsername);
        showLayoutLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mLoginPresenter.checkLoginStatus();
            }
        }, HardCodeUtil.SPLASH_DISPLAY_LENGTH);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mLoginPresenter.onStop();
    }

    @Override
    public void onBackPressed() {
        LoginActivity.this.finish();
    }

    @OnClick(R.id.btn_login)
    void doBtnLoginClick() {
        closeSoftKey();
        if (validateLogin()) {
            showLayoutLoading();
            String username = medtUser.getText().toString();
            String password = medtPassword.getText().toString();
            mLoginPresenter.doLoginTask(username, password);
        }
    }

    @OnLongClick(R.id.btn_login)
    boolean doBtnLoginLongClick() {
        if (BuildConfig.DEBUG) {
            showConfigDialog();
        }
        return true;
    }

    @OnClick(R.id.btn_Config)
    void doBtnConfigClick() {
        if (BuildConfig.DEBUG) {
            showConfigDialog();
        }
    }

    @OnClick(R.id.btn_ReLogin)
    void doBtnReLoginClick() {
        processReLogin();
    }

    private void processReLogin() {
        medtPassword.setText("");
        showLayoutLogin();
    }

    @SuppressLint("SetTextI18n")
    @OnFocusChange(R.id.edt_username)
    void focusChangeEditName(boolean focused) {
        if (focused) {
            itvUserName.setText("{md-person @dimen/login_icon_size @color/white}");
        } else {
            itvUserName.setText("{md-person @dimen/login_icon_size @color/White30}");
        }
    }

    @SuppressLint("SetTextI18n")
    @OnFocusChange(R.id.edt_password)
    void focusChangeEditPass(boolean focused) {
        if (focused) {
            itvUserPass.setText("{md-lock @dimen/login_icon_size @color/white}");
        } else {
            itvUserPass.setText("{md-lock @dimen/login_icon_size @color/White30}");
        }
    }

    boolean validateLogin() {
        String username = medtUser.getText().toString();
        String password = medtPassword.getText().toString();
        if (StringUtils.isNullOrEmpty(username)) {
            medtUser.setError("");
            return false;
        }
        if (StringUtils.isNullOrEmpty(password)) {
            medtPassword.setError("");
            return false;
        }
        return true;
    }


    @Override
    public void startMainActivity() {
        ThemeUtils.changeToThemeWithoutRestartActivity(DMSSharePreference.get().getDefaultThemeByUserKey(DMSSharePreference.get().getLastUserThemeKey()));
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void forceLogout() {
        DMSApplication.get().logout(LoginActivity.this, false);
        DialogUtils.showMessageDialog(this, R.string.notify, R.string.coming_soon_feature);
        prBLogin.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        llLoginError.setVisibility(View.GONE);
        medtUser.requestFocus();
    }

    private void showLayoutLoading() {
        llLogin.setVisibility(View.GONE);
        llLoginError.setVisibility(View.GONE);
        prBLogin.setVisibility(View.VISIBLE);
    }

    private void showConfigDialog() {
        ServerSettingDiaglog dialog = new ServerSettingDiaglog(this);
        dialog.show();
    }

    public void showLayoutLogin() {
        prBLogin.setVisibility(View.GONE);
        llLogin.setVisibility(View.VISIBLE);
        llLoginError.setVisibility(View.GONE);

        medtUser.requestFocus();
    }

    protected void showLayoutError(CharSequence message) {
        llLoginError.setVisibility(View.VISIBLE);
        llLogin.setVisibility(View.GONE);
        prBLogin.setVisibility(View.GONE);

        tvMessageError.setText(message);
    }

    public void processError(SdkException info) {
        if (info.getErrorType() == ErrorType.INVALID_USER_CREDENTIALS) {
            DialogUtils.showMessageDialog(this, R.string.warning, R.string.login_error_invalid_credential, R.string.confirm_close);
            showLayoutLogin();
            medtUser.requestFocus();
        } else if (info.getErrorType() == ErrorType.NETWORK_ERROR) {
            if (BuildConfig.DEBUG) {
                showLayoutError(getString(R.string.error_connect_server));
            } else {
                DialogUtils.showMessageDialog(this, R.string.warning, R.string.error_connect_server, R.string.confirm_close);
                showLayoutLogin();
                medtUser.requestFocus();
            }
        } else if (info.getErrorType() == ErrorType.INVALID_ACCESS_TOKEN || info.getErrorType() == ErrorType.INVALID_REFRESH_TOKEN) {
            DialogUtils.showMessageDialog(this, R.string.login_error_expired, R.string.login_error_will_be_logout);
            DMSApplication.get().logout(LoginActivity.this, false);
            showLayoutLogin();
            medtUser.requestFocus();
        } else {
            if (BuildConfig.DEBUG) {
                showLayoutError(getString(R.string.error_connect));
            } else {
                DialogUtils.showMessageDialog(this, R.string.warning, R.string.error_connect, R.string.confirm_close);
                showLayoutLogin();
                medtUser.requestFocus();
            }
        }
    }
}
