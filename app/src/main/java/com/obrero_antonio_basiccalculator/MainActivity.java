package com.obrero_antonio_basiccalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;
    private StringBuilder inputStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.textview_display);
        inputStringBuilder = new StringBuilder();

        // Set click listeners for number buttons
        Button[] numberButtons = new Button[10];
        for (int i = 0; i < numberButtons.length; i++) {
            String buttonID = "button_" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            numberButtons[i] = findViewById(resID);
            numberButtons[i].setOnClickListener(numberButtonClickListener);
        }

        // Set click listeners for operator buttons
        Button buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(operatorButtonClickListener);

        Button buttonSubtract = findViewById(R.id.button_subtract);
        buttonSubtract.setOnClickListener(operatorButtonClickListener);

        Button buttonMultiply = findViewById(R.id.button_multiply);
        buttonMultiply.setOnClickListener(operatorButtonClickListener);

        Button buttonDivide = findViewById(R.id.button_divide);
        buttonDivide.setOnClickListener(operatorButtonClickListener);

        // Set click listener for equal button
        Button buttonEqual = findViewById(R.id.button_equal);
        buttonEqual.setOnClickListener(equalButtonClickListener);

        // Set click listener for decimal button
        Button buttonDecimal = findViewById(R.id.button_decimal);
        buttonDecimal.setOnClickListener(decimalButtonClickListener);
    }

    private View.OnClickListener numberButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String buttonText = button.getText().toString();
            inputStringBuilder.append(buttonText);
            displayTextView.setText(inputStringBuilder.toString());
        }
    };

    private View.OnClickListener operatorButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            String buttonText = button.getText().toString();
            inputStringBuilder.append(buttonText);
            displayTextView.setText(inputStringBuilder.toString());
        }
    };

    private View.OnClickListener equalButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                // Evaluate the expression
                String expression = inputStringBuilder.toString();
                double result = eval(expression);
                displayTextView.setText(String.valueOf(result));
            } catch (Exception e) {
                displayTextView.setText("Error");
            }
        }
    };

    private View.OnClickListener decimalButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String currentInput = inputStringBuilder.toString();
            if (!currentInput.contains(".")) {
                inputStringBuilder.append(".");
                displayTextView.setText(inputStringBuilder.toString());
            }
        }
    };

    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }
}
