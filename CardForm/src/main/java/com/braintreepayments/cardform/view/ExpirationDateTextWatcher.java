package com.braintreepayments.cardform.view;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class ExpirationDateTextWatcher implements TextWatcher {

    private static final Set<String> VALID_MONTHS = new HashSet<>(
            Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));

    private static final int STATE_IDLE = 0;
    private static final int STATE_FORMATTING = 1;

    /**
     * Date string is at most 7 characters XX/XXXX
     */
    private static final int BUFFER_CAPACITY = 8;

    static ExpirationDateTextWatcher newInstance() {
        return new ExpirationDateTextWatcher();
    }

    private int state;
    private StringBuilder buffer;

    private ExpirationDateTextWatcher() {
        state = STATE_IDLE;
        buffer = new StringBuilder(BUFFER_CAPACITY);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // TODO: unit test
        // clear string buffer
        // Ref: https://stackoverflow.com/a/2242475
        buffer.setLength(0);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { /* do nothing */ }

    @Override
    public void afterTextChanged(Editable e) {
        if (state == STATE_FORMATTING) {
            // prevent infinite recursion from calling s.replace() while formatting text
            return;
        }

        int originalTextLength = e.length();
        if (originalTextLength > 0) {

            for (int i = 0; i < originalTextLength; i++) {
                char c = e.charAt(i);

                boolean isEmpty = (buffer.length() == 0);
                boolean shouldAppendLeadingZero = (isEmpty && (c != '0' && c != '1'));
                if (shouldAppendLeadingZero) {
                    buffer.append('0');
                }
                buffer.append(c);

                boolean shouldInsertSlash = (buffer.length() == 2) && !VALID_MONTHS.contains(buffer.toString());
                if (shouldInsertSlash) {
                    buffer.insert(1, '/');
                    if (buffer.charAt(0) == '1') {
                        buffer.insert(0, '0');
                    }
                }
            }

            state = STATE_FORMATTING;
            e.replace(0, originalTextLength, buffer, 0, buffer.length());
            state = STATE_IDLE;
        }
    }
}
