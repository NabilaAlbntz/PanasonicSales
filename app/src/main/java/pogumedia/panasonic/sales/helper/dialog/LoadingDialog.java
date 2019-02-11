package pogumedia.panasonic.sales.helper.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import pogumedia.panasonic.sales.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by crocodic-mbp8 on 9/1/17.
 */

public class LoadingDialog extends DialogFragment {


    AVLoadingIndicatorView avLoadingIndicatorView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.loading_dialog, container, false);

        avLoadingIndicatorView = mView.findViewById(R.id.pb_loading_dialog);
        avLoadingIndicatorView.smoothToShow();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        setCancelable(false);


        return mView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        avLoadingIndicatorView.smoothToHide();
        super.onDismiss(dialog);
    }

    public void show(android.support.v4.app.FragmentManager manager) {
        show(manager, "");
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commit();
        } catch (IllegalStateException e) {

        }
    }

    @Override
    public void dismiss() {

        super.dismiss();
    }
}
