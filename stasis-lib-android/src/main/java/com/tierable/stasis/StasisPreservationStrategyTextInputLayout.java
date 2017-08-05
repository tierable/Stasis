package com.tierable.stasis;


import android.support.design.widget.TextInputLayout;


/**
 * @author Aniruddh Fichadia
 * @date 2017-08-05
 */
public class StasisPreservationStrategyTextInputLayout
        extends StasisPreservationStrategyView<TextInputLayout> {
    private       CharSequence                       hint;
    private       CharSequence                       error;
    private final StasisPreservationStrategyTextView preservationStrategyEditText;


    public StasisPreservationStrategyTextInputLayout() {
        preservationStrategyEditText = new StasisPreservationStrategyTextView();
    }


    @Override
    public void freeze(TextInputLayout preserved) {
        super.freeze(preserved);

        hint = preserved.getHint();
        error = preserved.getError();
        preservationStrategyEditText.freeze(preserved.getEditText());
    }

    @Override
    public void unFreeze(TextInputLayout preserved) {
        super.unFreeze(preserved);

        preserved.setHint(hint);
        preserved.setError(error);
        preservationStrategyEditText.unFreeze(preserved.getEditText());
    }


    @Override
    public String toString() {
        return "StasisPreservationStrategyTextInputLayout{" +
                "preservationStrategyEditText=" + preservationStrategyEditText +
                ", hint=" + hint +
                ", error=" + error +
                "} " + super.toString();
    }
}
