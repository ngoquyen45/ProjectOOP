package com.viettel.dmsplus.sdk.auth;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.viettel.dmsplus.sdk.utils.JsonUtils;
import com.viettel.dmsplus.sdk.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class will show a scrollable list of accounts a user can choose to log in to, and at the end
 * an option to login as a new account. Activities using this fragment should implement OnAuthenticationChosen.
 */
public class ChooseAuthenticationFragment extends Fragment {

    private ListView mListView;
    private static final String EXTRA_AUTHENTICATION_INFOS = "AuthenticationInfos";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayList<AuthenticationInfo> infos = getAuthenticationInfoList();
        View view = inflater.inflate(R.layout.sdk_choose_auth_activity, null);
        mListView = (ListView)view.findViewById(R.id.sdk_accounts_list);
        if (infos == null){
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        } else {
            mListView.setAdapter(new AuthenticatedAccountsAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, infos));
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (parent.getAdapter() instanceof AuthenticatedAccountsAdapter){
                        AuthenticatedAccountsAdapter accountsAdapter = (AuthenticatedAccountsAdapter)parent.getAdapter();
                        AuthenticationInfo info = accountsAdapter.getItem(position);
                        if (info instanceof AuthenticatedAccountsAdapter.DifferentAuthenticationInfo){
                            if (getActivity() instanceof OnAuthenticationChosen){
                                ((OnAuthenticationChosen) getActivity()).onDifferentAuthenticationChosen();
                            }

                        }
                        else {
                            if (getActivity() instanceof OnAuthenticationChosen){
                                ((OnAuthenticationChosen) getActivity()).onAuthenticationChosen(info);
                            }
                        }

                    }
                }
            });
        }
        return view;
    }

    /**
     *
     * @return a list of authentication info objects to display in the fragment.
     */
    public ArrayList<AuthenticationInfo> getAuthenticationInfoList(){
        if (getArguments() != null && getArguments().getCharSequenceArrayList(EXTRA_AUTHENTICATION_INFOS) != null){
            ArrayList<CharSequence> jsonSerialized = getArguments().getCharSequenceArrayList(EXTRA_AUTHENTICATION_INFOS);
            ArrayList<AuthenticationInfo> list;
            if (jsonSerialized != null) {
                list = new ArrayList<AuthenticationInfo>(jsonSerialized.size());
                for (CharSequence sequence : jsonSerialized){
                    AuthenticationInfo info = JsonUtils.toObject(sequence.toString(), AuthenticationInfo.class);
                    if (info != null) {
                        list.add(info);
                    }
                }
            } else {
                list = new ArrayList<AuthenticationInfo>();
            }
            return list;
        }
        Map<String, AuthenticationInfo> map = AuthenticationManager.getInstance().getStoredAuthInfo(getActivity());
        if (map != null){
            ArrayList<AuthenticationInfo> list = new ArrayList<AuthenticationInfo>(map.size());
            for (String key : map.keySet()){
                list.add(map.get(key));
            }
            return list;
        }
        return null;
    }

    /**
     * Create an instance of this fragment to display any AuthenticationInfo retrieved via AuthenticationManager.getInstance().getStoredAuthInfo().
     * @param context current context
     * @return an instance of this fragment displaying auth infos via AuthenticationManager.getInstance().getStoredAuthInfo().
     */
    public static ChooseAuthenticationFragment createAuthenticationActivity(final Context context){
        ChooseAuthenticationFragment fragment = new ChooseAuthenticationFragment();
        return fragment;

    }

    /**
     * Create an instance of this fragment to display the given list of AuthenticationInfo.
     * @param context current context
     * @param listOfAuthInfo a list of auth infos in the order to display to the user.
     * @return a fragment displaying list of authinfos provided above.
     */
    public static ChooseAuthenticationFragment createChooseAuthenticationFragment(final Context context, final ArrayList<AuthenticationInfo> listOfAuthInfo){
        ChooseAuthenticationFragment fragment = createAuthenticationActivity(context);
        Bundle b = fragment.getArguments();
        if (b == null){
            b = new Bundle();
        }

        ArrayList<CharSequence> jsonSerialized = new ArrayList<CharSequence>(listOfAuthInfo.size());
        for (AuthenticationInfo info : listOfAuthInfo) {
            jsonSerialized.add(JsonUtils.toJsonString(info));
        }
        b.putCharSequenceArrayList(EXTRA_AUTHENTICATION_INFOS, jsonSerialized);

        fragment.setArguments(b);
        return fragment;

    }


    /**
     * Interface this fragment uses to communicate to its parent activity selection of an auth info or if a "different account" was chosen.
     */
    public interface OnAuthenticationChosen {

        /**
         *
         * @param authInfo the auth info chosen by the user, returns a new empty instance of DifferentAuthenticationInfo if the user chose to use a different account.
         */
        void onAuthenticationChosen(AuthenticationInfo authInfo);


        /**
         * Called if a user chooses to login as a "different account". If this is called the instantiater of this fragment should show appropriate ui to allow a new user to login.
         */
        void onDifferentAuthenticationChosen();
    }

}
