package pogumedia.panasonic.sales.helper.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import pogumedia.panasonic.sales.R;


/**
 * Created by @yzzzd on 1/28/18 11:01 AM.
 */

public class BasicTitleDialog implements View.OnClickListener {

    private Dialog dialog;
    private View dialogView;

    private AppCompatTextView titleView;
    private AppCompatTextView messageView;
    private AppCompatButton btnPositive;
    private AppCompatButton btnNegative;
    private ImageView ivState;

    private ButtonPositiveClickListener buttonPositiveClickListener;
    private ButtonNegativeClickListener buttonNegativeClickListener;

    public BasicTitleDialog(Context context) {
        init(new AlertDialog.Builder(context));
    }

    public BasicTitleDialog(Context context, int theme) {
        init(new AlertDialog.Builder(context, theme));
    }

    private void init(AlertDialog.Builder dialogBuilder) {
        dialogView = LayoutInflater.from(dialogBuilder.getContext()).inflate(R.layout.dialog_basic_title, null);
        dialog = dialogBuilder.setView(dialogView).create();

        titleView = dialogView.findViewById(R.id.tv_title);
        messageView = dialogView.findViewById(R.id.tv_message);
        btnPositive = dialogView.findViewById(R.id.btn_positive);
        btnNegative = dialogView.findViewById(R.id.btn_negative);
        ivState = dialogView.findViewById(R.id.iv_state);

        btnPositive.setOnClickListener(this);
        btnNegative.setOnClickListener(this);
        dialog.setCancelable(false);
    }

    public BasicTitleDialog setAlert(@StringRes int message, @StringRes int btnText, @StringRes int btnText2) {
        return setAlert(string(message), string(btnText), string(btnText2));
    }

    public BasicTitleDialog setAlert(CharSequence message, CharSequence btnText, CharSequence btnText2) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);

        btnPositive.setVisibility(View.VISIBLE);
        btnPositive.setText(btnText);

        btnNegative.setVisibility(View.VISIBLE);
        btnNegative.setText(btnText2);

        return this;
    }

    public BasicTitleDialog setButtonPositiveClickListener(ButtonPositiveClickListener listener) {
        this.buttonPositiveClickListener = listener;
        return this;
    }

    public BasicTitleDialog setButtonNegativeClickListener(ButtonNegativeClickListener listener) {
        this.buttonNegativeClickListener = listener;
        return this;
    }

    public BasicTitleDialog setMessage(@StringRes int message) {
        return setMessage(string(message));
    }

    public BasicTitleDialog setMessage(CharSequence message) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);
        return this;
    }

    public BasicTitleDialog setTitle(@StringRes int message) {
        return setTitle(string(message));
    }

    public BasicTitleDialog setTitle(CharSequence message) {
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(message);
        return this;
    }

    public BasicTitleDialog setTitleVisibility(boolean isVisible) {
        titleView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BasicTitleDialog setSingleButton() {
        btnNegative.setVisibility(View.GONE);
        return this;
    }

    public BasicTitleDialog setOnlyText() {
        ivState.setVisibility(View.GONE);
        return this;
    }

    public BasicTitleDialog setIvState(Integer resourceId) {
        ivState.setImageResource(resourceId);
        return this;
    }

    public BasicTitleDialog setPositiveButton(@StringRes int message) {
        return setPositiveButton(string(message));
    }

    public BasicTitleDialog setPositiveButton(CharSequence message) {
        btnPositive.setVisibility(View.VISIBLE);
        btnPositive.setText(message);
        return this;
    }

    public BasicTitleDialog setPositiveButtonBackground(Drawable drawable) {
        btnPositive.setBackground(drawable);
        return this;
    }

    public BasicTitleDialog setPositiveButtonColor(@ColorInt int color) {
        btnPositive.setTextColor(color);
        return this;
    }

    public BasicTitleDialog setNegativeButton(@StringRes int message) {
        return setNegativeButton(string(message));
    }

    public BasicTitleDialog setNegativeButton(CharSequence message) {
        btnNegative.setVisibility(View.VISIBLE);
        btnNegative.setText(message);
        return this;
    }

    public BasicTitleDialog setNegativeButtonBackground(Drawable drawable) {
        btnNegative.setBackground(drawable);
        return this;
    }

    public BasicTitleDialog setNegativeButtonColor(@ColorInt int color) {
        btnNegative.setTextColor(color);
        return this;
    }

    public BasicTitleDialog setMessageGravity(int gravity) {
        messageView.setGravity(gravity);
        return this;
    }

    public BasicTitleDialog setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
        return this;
    }

    boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    protected String string(@StringRes int res) {
        return dialogView.getContext().getString(res);
    }

    protected <ViewClass extends View> ViewClass findView(int id) {
        return (ViewClass) dialogView.findViewById(id);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public Dialog show() {
        dialog.show();
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_positive:
                buttonPositiveClickListener.clicked(this);
                break;
            case R.id.btn_negative:
                buttonNegativeClickListener.clicked(this);
                break;
        }
    }

    public interface ButtonPositiveClickListener {
        public void clicked(BasicTitleDialog dialog);
    }

    public interface ButtonNegativeClickListener {
        public void clicked(BasicTitleDialog dialog);
    }
}
