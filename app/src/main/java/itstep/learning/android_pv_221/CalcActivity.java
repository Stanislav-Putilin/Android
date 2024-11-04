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

        findViewById(R.id.calc_btn_sqrt).setOnClickListener(this::btnClickSquareRoot);
        findViewById(R.id.calc_btn_inverse).setOnClickListener(this::btnClickReciprocal);
        findViewById(R.id.calc_btn_ce).setOnClickListener(this::btnClickCE);
        findViewById(R.id.calc_btn_percent).setOnClickListener(this::btnClickPercent);

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
        outState.putCharSequence("tv_history", tvHistory.getText());
        outState.putDouble("first_operand", firstOperand);
        outState.putDouble("second_operand", secondOperand);
        outState.putString("current_operation", currentOperation);
        outState.putBoolean("reset_on_next_input", resetOnNextInput);
        outState.putBoolean("is_repeat_equals", isRepeatEquals);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        tvResult.setText(savedInstanceState.getCharSequence("tv_result"));
        tvHistory.setText(savedInstanceState.getCharSequence("tv_history"));
        firstOperand = savedInstanceState.getDouble("first_operand");
        secondOperand = savedInstanceState.getDouble("second_operand");
        currentOperation = savedInstanceState.getString("current_operation", "");
        resetOnNextInput = savedInstanceState.getBoolean("reset_on_next_input", false);
        isRepeatEquals = savedInstanceState.getBoolean("is_repeat_equals", false);
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
        tvHistory.setText("");
        firstOperand = 0;
        secondOperand = 0;
        currentOperation = "";
        resetOnNextInput = false;
        isRepeatEquals = false;
    }

    private void btnClickCE(View view) {
        tvResult.setText(zeroSign);
        resetOnNextInput = true;
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

            String formattedResult = formatResult(result);

            tvResult.setText(formattedResult);

            tvHistory.setText(firstOperand + " ^2 = " + formattedResult);
            firstOperand = result;
            resetOnNextInput = true;
            isRepeatEquals = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void btnClickSquareRoot(View view) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.ROOT);
            currentOperation = "√";
            firstOperand = format.parse(tvResult.getText().toString()).doubleValue();

            if (firstOperand < 0)
            {
                tvResult.setText(R.string.error_invalid_input);
                tvHistory.setText("");
                return;
            }

            double result = Math.sqrt(firstOperand);

            String formattedResult = formatResult(result);

            tvResult.setText(formattedResult);
            tvHistory.setText("√" + firstOperand + " = " + formattedResult);

            firstOperand = result;
            resetOnNextInput = true;
            isRepeatEquals = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void btnClickReciprocal(View view) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.ROOT);
            firstOperand = format.parse(tvResult.getText().toString()).doubleValue();
            currentOperation = "1/x";

            if (firstOperand == 0) {
                tvResult.setText(R.string.error_divide_by_zero);
                tvHistory.setText("");
                return;
            }

            double result = 1 / firstOperand;

            String formattedResult = formatResult(result);
            tvResult.setText(formattedResult);
            tvHistory.setText("1 / " + firstOperand + " = " + formattedResult);

            firstOperand = result;
            resetOnNextInput = true;
            isRepeatEquals = true;

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void btnClickPercent(View view) {
        try {
            NumberFormat format = NumberFormat.getInstance(Locale.ROOT);
            double currentValue = format.parse(tvResult.getText().toString()).doubleValue();

            if (!currentOperation.isEmpty() && !isRepeatEquals) {

                secondOperand = firstOperand * currentValue / 100;
                String formattedResult = formatResult(secondOperand);

                tvResult.setText(formattedResult);
                tvHistory.setText(firstOperand + " " + currentOperation + " " + formattedResult);

            } else {
                double result = currentValue / 100;
                String formattedResult = formatResult(result);
                tvResult.setText(formattedResult);
                tvHistory.setText(currentValue + "% = " + formattedResult);
            }

            resetOnNextInput = true;
            isRepeatEquals = false;
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

                if (!isRepeatEquals) {
                    secondOperand = format.parse(tvResult.getText().toString()).doubleValue();
                    isRepeatEquals = true;
                } else if (!currentOperation.equals("√") && !currentOperation.equals("1/x")) {

                    firstOperand = format.parse(tvResult.getText().toString()).doubleValue();
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

                    case "√":
                        if (firstOperand < 0) {
                            tvResult.setText(R.string.error_invalid_input);
                            tvHistory.setText("");
                            return;
                        }
                        result = Math.sqrt(firstOperand);
                        break;
                    case "1/x":
                        if (firstOperand == 0) {
                            tvResult.setText(R.string.error_divide_by_zero);
                            tvHistory.setText("");
                            return;
                        }
                        result = 1 / firstOperand;
                        break;
                }

                String formattedResult = formatResult(result);
                tvResult.setText(formattedResult);

                String operationText;

                if (currentOperation.equals("√")) {
                    operationText = "√" + firstOperand;
                } else if (currentOperation.equals("1/x")) {
                    operationText = "1 / " + firstOperand;
                } else {
                    operationText = firstOperand + " " + currentOperation + " " + secondOperand;
                }

                tvHistory.setText(operationText + " = " + formattedResult);

                firstOperand = result;
                resetOnNextInput = true;

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