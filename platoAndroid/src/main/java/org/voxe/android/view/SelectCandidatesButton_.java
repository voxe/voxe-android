//
// DO NOT EDIT THIS FILE, IT HAS BEEN GENERATED USING AndroidAnnotations.
//


package org.voxe.android.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import org.voxe.android.R.id;
import org.voxe.android.R.layout;


/**
 * We use @SuppressWarning here because our java code
 * generator doesn't know that there is no need
 * to import OnXXXListeners from View as we already
 * are in a View.
 * 
 */
@SuppressWarnings("unused")
public final class SelectCandidatesButton_
    extends SelectCandidatesButton
{

    private Context context_;
    private boolean mAlreadyInflated_ = false;

    public SelectCandidatesButton_(Context context) {
        super(context);
        init_();
    }

    private void init_() {
        context_ = getContext();
        if (context_ instanceof Activity) {
            Activity activity = ((Activity) context_);
        }
    }

    private void afterSetContentView_() {
        candidate2ImageView = ((ImageView) findViewById(id.candidate2ImageView));
        candidate1ImageView = ((ImageView) findViewById(id.candidate1ImageView));
        {
            View view = findViewById(id.selectCandidatesButton);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        SelectCandidatesButton_.this.selectCandidatesButtonClicked();
                    }

                }
                );
            }
        }
    }

    /**
     * The mAlreadyInflated_ hack is needed because of an Android bug
     * which leads to infinite calls of onFinishInflate()
     * when inflating a layout with a parent and using
     * the <merge /> tag.
     * 
     */
    @Override
    public void onFinishInflate() {
        if (!mAlreadyInflated_) {
            mAlreadyInflated_ = true;
            inflate(getContext(), layout.select_candidates_button, this);
            afterSetContentView_();
        }
        super.onFinishInflate();
    }

    public static SelectCandidatesButton build(Context context) {
        SelectCandidatesButton_ instance = new SelectCandidatesButton_(context);
        instance.onFinishInflate();
        return instance;
    }

}
