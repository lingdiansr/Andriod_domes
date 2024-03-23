package com.ldsr.calculator;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private StringBuilder expression = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        textView = findViewById(R.id.textView);

        // 设置数字和运算符的点击事件
        findViewById(R.id.tv0).setOnClickListener(v -> updateExpression("0"));
        findViewById(R.id.tv1).setOnClickListener(v -> updateExpression("1"));
        findViewById(R.id.tv2).setOnClickListener(v -> updateExpression("2"));
        findViewById(R.id.tv3).setOnClickListener(v -> updateExpression("3"));
        findViewById(R.id.tv4).setOnClickListener(v -> updateExpression("4"));
        findViewById(R.id.tv5).setOnClickListener(v -> updateExpression("5"));
        findViewById(R.id.tv6).setOnClickListener(v -> updateExpression("6"));
        findViewById(R.id.tv7).setOnClickListener(v -> updateExpression("7"));
        findViewById(R.id.tv8).setOnClickListener(v -> updateExpression("8"));
        findViewById(R.id.tv9).setOnClickListener(v -> updateExpression("9"));
        findViewById(R.id.tvPlus).setOnClickListener(v -> updateExpression("+"));
        findViewById(R.id.tvMinus).setOnClickListener(v -> updateExpression("-"));
        findViewById(R.id.tvMultiply).setOnClickListener(v -> updateExpression("×"));
        findViewById(R.id.tvDivide).setOnClickListener(v -> updateExpression("÷"));
        findViewById(R.id.tvPercent).setOnClickListener(v -> updateExpression("%"));
        findViewById(R.id.tvPoint).setOnClickListener(v -> updateExpression("."));

        // 清除按钮的点击事件
        findViewById(R.id.tvClear).setOnClickListener(v -> {
            expression.setLength(0);
            textView.setText("0");
        });

        // 退格按钮的点击事件
        findViewById(R.id.tvBackslash).setOnClickListener(v -> {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                textView.setText(expression.toString());
            }
        });

        // 计算按钮的点击事件
        findViewById(R.id.tvEquals).setOnClickListener(v -> {
            try {
                String result = evaluateExpression(expression.toString());
                textView.setText(result);
                expression.delete(0,expression.length());
            } catch (Exception e) {
                textView.setText("Error");
            }
        });
    }
    private void updateExpression(String value) {
        expression.append(value);
        textView.setText(expression.toString());
    }
    private String evaluateExpression(String expression) {
        String result = "";
        try {
            // 使用Android自带的计算方法来计算表达式的结果
            double doubleResult = eval(expression);

            // 判断是否为整数
            if (doubleResult % 1 == 0) {
                // 将双精度浮点数转换为整数形式
                result = String.valueOf((int) doubleResult);
            } else {
                result = String.valueOf(doubleResult);
            }
        } catch (NumberFormatException e) {
            result = "似乎不是一个可以计算的算式呢";
        } catch (ArithmeticException e) {
            result = "除数不可以为零哦";
        } catch (RuntimeException e) {
            result = "似乎不是一个可以计算的算式呢";
        }
        return result;
    }

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
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('×')) x *= parseFactor();
                    else if (eat('÷')) {
                        double divisor = parseFactor();
                        if (divisor == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        x /= divisor;
                    } else return x;
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
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}