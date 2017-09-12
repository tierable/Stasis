package com.tierable.stasis;


import android.support.design.widget.TextInputLayout;
import android.widget.EditText;


/**
 * Preserves and restores the
 * <ul>
 * <li>{@link EditText} using {@link PreservationStrategyTextView}
 * <li>Hint
 * <li>Error
 * <li>State saved by {@link PreservationStrategyView}
 * </ul>
 *
 * @author Aniruddh Fichadia
 * @date 2017-08-05
 */
public class PreservationStrategyTextInputLayout
        extends PreservationStrategyView<TextInputLayout> {
    private       CharSequence                 hint;
    private       CharSequence                 error;
    private final PreservationStrategyTextView preservationStrategyEditText;


    public PreservationStrategyTextInputLayout() {
        preservationStrategyEditText = new PreservationStrategyTextView();
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
        return "PreservationStrategyTextInputLayout{" +
                "hint=" + hint +
                ", error=" + error +
                ", preservationStrategyEditText=" + preservationStrategyEditText +
                "} " + super.toString();
    }
}
