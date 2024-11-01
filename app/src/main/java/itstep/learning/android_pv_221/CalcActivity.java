package itstep.learning.android_pv_221;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class CalcActivity extends AppCompatActivity {

    private TextView tvResult;
    private TextView tvHistory;

    private static final int maxDigits = 9;
    private String zeroSign;
    private double firstOperand = 0;
    private double secondOperand = 0;
    private String currentOperation = "";
    private boolean resetOnNextInput = false;
    private boolean isRepeatEquals = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);


        zeroSign = getString(R.string.calc_btn_digit_0);
        tvResult = findViewById(R.id.calc_tv_result);
        tvHistory = findViewById(R.id.calc_tv_history);

        findViewById(R.id.calc_btn_c).setOnClickListener(this::btnClickC);
        findViewById(R.id.calc_btn_backspace).setOnClickListener(this::btnClickBackspace);
        findViewById(R.id.calc_btn_coma).setOnClickListener(this::btnClickDot);
        findViewById(R.id.calc_btn_pm).setOnClickListener(this::btnClickPlusMinus);

        findViewById(R.id.calc_btn_plus).setOnClickListener(this::btnClickAdd);
        findViewById(R.id.calc_btn_equal).setOnClickListener(this::btnClickEquals);

        findViewById(R.id.calc_btn_minus).setOnClickListener(this::btnClickSubtract);
        findViewById(R.id.calc_btn_multiply).setOnClickListener(this::btnClickMultiply);
        findViewById(R.id.calc_btn_divide).setOnClickListener(this::btnClickDivide);

        findViewById(R.id.calc_btn_square).setOnClickListener(this::btnClickSquare);


        for (int i = 0; i < 10; i++)
        {
            String btnIdName = "calc_btn_digit_" + i;
            @SuppressLint("DiscouragedApi") int btnId = getResources().getIdentifier(btnIdName, "id", getPackageName());
            findViewById(btnId).setOnClickListener(this::btnClickDigit);
        }
        btnClickC(null);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("tv_result", tvResult.getText());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("tv_result"));
    }

    private void btnClickBackspace(View view)
    {
        String resText = tvResult.getText().toString();
        resText = resText.substring(0, resText.length()-1);
        if(resText.isEmpty())
        {
            resText = zeroSign;
        }
        tvResult.setText(resText);
        isRepeatEquals = false;
    }

    private void btnClickDigit(View view) {
        String resText = tvResult.getText().toString();

        if (resetOnNextInput) {
            resText = "";
            resetOnNextInput = false;
        }

        if (resText.length() < maxDigits) {
            if (resText.equals(zeroSign)) {
                resText = "";
            }

            resText += ((Button) view).getText();
            tvResult.setText(resText);
        }
        isRepeatEquals = false;
    }
    private void btnClickC(View view)
    {
        tvResult.setText(zeroSign);
        isRepeatEquals = false;
    }

    private void btnClickDot(View view) {
        String resText = tvResult.getText().toString();

        if (!resText.contains(".")) {
            if (resText.equals(zeroSign)) {
                resText = "0.";
            } else {
                resText += ".";
            }
            tvResult.setText(resText);
        }
        isRepeatEquals = false;
    }

    private void btnClickPlusMinus(View view) {
        String resText = tvResult.getText().toString();

        if (!resText.equals(zeroSign)) {
            if (resText.startsWith("-")) {
                resText = resText.substring(1);
            } else {
                resText = "-" + resText;
            }
            tvResult.setText(resText);
        }
        isRepeatEquals = false;
    }

    private void btnClickAdd(View view) {
        setOperation("+");
    }

    private void btnClickSubtract(View view) {
        setOperation("-");
    }

    private void btnClickMultiply(View view) {
        setOperation("*");
    }

    private void btnClickDivide(View view) {
        setOperation("/");
    }

    private void btnClickSquare(View view) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.ROOT);
            firstOperand = format.parse(tvResult.getText().toString()).doubleValue();
            currentOperation = "^2";
            double result = firstOperand * firstOperand;

            String formattedResult = (result % 1 == 0)
                    ? String.format(Locale.ROOT, "%.0f", result)
                    : String.format(Locale.ROOT, "%.8f", result).replaceAll("0+$", "").replaceAll("\\.$", "");

            tvResult.setText(formattedResult.length() > maxDigits
                    ? String.format(Locale.ROOT, "%.6g", result)
                    : formattedResult);

            tvHistory.setText(firstOperand + " ^2 = " + formattedResult);
            firstOperand = result;
            resetOnNextInput = true;
            isRepeatEquals = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setOperation(String operation) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.ROOT);
            firstOperand = format.parse(tvResult.getText().toString()).doubleValue();
            currentOperation = operation;
            resetOnNextInput = true;
            tvHistory.setText(firstOperand + " " + operation + " ");
            isRepeatEquals = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void btnClickEquals(View view) {
        if (!currentOperation.isEmpty()) {
            try {

                NumberFormat format = NumberFormat.getInstance(Locale.ROOT);

                if (!isRepeatEquals || currentOperation.equals("^2")) {
                    secondOperand = currentOperation.equals("^2") ? firstOperand : format.parse(tvResult.getText().toString()).doubleValue();
                    isRepeatEquals = true;
                }

                double result = 0;

                switch (currentOperation) {
                    case "+":
                        result = firstOperand + secondOperand;
                        break;
                    case "-":
                        result = firstOperand - secondOperand;
                        break;
                    case "*":
                        result = firstOperand * secondOperand;
                        break;
                    case "/":
                        if (secondOperand == 0) {
                            tvResult.setText(R.string.error_divide_by_zero);
                            tvHistory.setText("");
                            return;
                        } else {
                            result = firstOperand / secondOperand;
                        }
                        break;
                    case "^2":
                        result = firstOperand * firstOperand;
                        break;
                }

                String formattedResult = (result % 1 == 0)
                        ? String.format(Locale.ROOT, "%.0f", result)
                        : String.format(Locale.ROOT, "%.8f", result).replaceAll("0+$", "").replaceAll("\\.$", "");

                tvResult.setText(formattedResult.length() > maxDigits
                        ? String.format(Locale.ROOT, "%.6g", result)
                        : formattedResult);

                tvHistory.setText(firstOperand + " " + currentOperation + " " + secondOperand + " = " + formattedResult);

                firstOperand = result;

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private String formatResult(double result) {
        String formattedResult = (result % 1 == 0)
                ? String.format(Locale.ROOT, "%.0f", result)
                : String.format(Locale.ROOT, "%.8f", result).replaceAll("0+$", "").replaceAll("\\.$", "");

        return formattedResult.length() > maxDigits
                ? String.format(Locale.ROOT, "%.6g", result)
                : formattedResult;
    }
}