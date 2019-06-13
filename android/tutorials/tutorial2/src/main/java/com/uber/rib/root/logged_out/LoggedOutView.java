package com.uber.rib.root.logged_out;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.uber.rib.tutorial1.R;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Top level view for {@link LoggedOutBuilder.LoggedOutScope}.
 */
public class LoggedOutView extends LinearLayout implements LoggedOutInteractor.LoggedOutPresenter {

    public LoggedOutView(Context context) {
        this(context, null);
    }

    public LoggedOutView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoggedOutView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public Observable<String> loginName() {
        return RxView.clicks(findViewById(R.id.login_button))
            .map(new Function<Object, String>() {
                @Override
                public String apply(Object o) throws Exception {
                    TextView textView = (TextView) findViewById(R.id.edit_text);
                    return textView.getText().toString();
                }
            });
    }
}
