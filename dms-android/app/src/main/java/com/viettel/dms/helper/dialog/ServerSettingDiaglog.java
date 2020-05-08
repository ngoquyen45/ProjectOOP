package com.viettel.dms.helper.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.viettel.dms.R;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dmsplus.sdk.SdkConfig;

/**
 * <b>Note: This class may be remove in the future</b>
 *
 * @author ThanhNV60
 */
public class ServerSettingDiaglog implements OnCheckedChangeListener, OnClickListener {

    public static void loadSavedServerConfig() {
        DMSSharePreference preference = DMSSharePreference.get();
        String config = preference.getServerConfig();
        if (!TextUtils.isEmpty(config)) {
            updateServerConfig(config);
        } else {
            config = SdkConfig.BASE_URL;
            preference.putServerConfig(config);
        }
    }

    private static void updateServerConfig(String config) {
        SdkConfig.BASE_URL = config;
    }

    protected Context context;
    protected LayoutInflater inflater;

    private AlertDialog dialog;
    private View view;
    private RadioGroup radioGroup;
    private Button btnSave;
    private Button btnCancel;
    private Button btnUseCustomServer;
    private EditText edtServerIp;
    private EditText edtServerPort;

    private ServerConfig[] configs;

    @SuppressLint("InflateParams")
    public ServerSettingDiaglog(Context context) {
        this.context = context;

        this.configs = new ServerConfig[]{
                new ServerConfig("Local Development", "http://dmsplus.net:8080"),
                new ServerConfig("188.166.247.136", "http://188.166.247.136:8080"),
                new ServerConfig("10.60.108.153", "http://10.60.108.153:8080"),
                new ServerConfig("128.199.84.237", "http://128.199.84.237:8080"),
                new ServerConfig("10.61.187.152", "http://10.61.187.152:8080"),
        };

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.view = inflater.inflate(R.layout.fragment_setting_server, null, false);
        this.dialog = new AlertDialog.Builder(this.context).setView(view).create();

        initViews(view);
    }

    private void initViews(View v) {
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);

        String currentConfig = DMSSharePreference.get().getServerConfig();

        for (int i = 0; i < this.configs.length; i++) {
            ServerConfig config = this.configs[i];
            RadioButton radio = new RadioButton(context);
            radio.setText(config.getName());
            radio.setId(i);
            radioGroup.addView(radio);

            if (currentConfig != null && TextUtils.equals(currentConfig, config.getAddress())) {
                radio.setChecked(true);
            }
        }

        radioGroup.post(new Runnable() {

            @Override
            public void run() {
                radioGroup.setOnCheckedChangeListener(ServerSettingDiaglog.this);
            }
        });

        btnSave = (Button) v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        edtServerIp = (EditText) v.findViewById(R.id.edtServerIp);
        edtServerPort = (EditText) v.findViewById(R.id.edtServerPort);
        btnUseCustomServer = (Button) v.findViewById(R.id.btnUseCustomServer);
        btnUseCustomServer.setOnClickListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btnSave.getId()) {
            int index = radioGroup.getCheckedRadioButtonId();
            if (index == -1) return;
            String config = configs[index].getAddress();
            DMSSharePreference.get().putServerConfig(config);
            updateServerConfig(config);
            this.dismiss();
        } else if (id == btnCancel.getId()) {
            this.dismiss();
        } else if (id == btnUseCustomServer.getId()) {
            StringBuffer buff = new StringBuffer();
            buff.append("http://");
            buff.append(edtServerIp.getText().toString());
            buff.append(":");
            buff.append(edtServerPort.getText().toString());
            DMSSharePreference.get().putServerConfig(buff.toString());
            updateServerConfig(buff.toString());
            this.dismiss();
        }
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public static class ServerConfig implements Parcelable {

        public static final Creator<ServerConfig> CREATOR = new Creator<ServerConfig>() {
            public ServerConfig createFromParcel(Parcel in) {
                return new ServerConfig(in);
            }

            public ServerConfig[] newArray(int size) {
                return new ServerConfig[size];
            }
        };

        private String name;
        private String address;

        public ServerConfig() {
        }

        public ServerConfig(String name, String address) {
            this.setName(name);
            this.setAddress(address);
        }

        private ServerConfig(Parcel in) {
            this.setName(in.readString());
            this.setAddress(in.readString());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.getName());
            dest.writeString(this.getAddress());
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
