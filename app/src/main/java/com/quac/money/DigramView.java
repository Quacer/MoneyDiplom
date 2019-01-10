package com.quac.money;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class DigramView extends View {

    private int income;
    private int expense;

    private Paint expensePaint=new Paint();
    private Paint incomePaint=new Paint();

    public DigramView(Context context) {
        this(context,null);
    }

    public DigramView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DigramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        incomePaint.setColor(getResources().getColor(R.color.balance_income_color));
        expensePaint.setColor(getResources().getColor(R.color.balance_expense_color));

        //По умолчанию на макете
        /*if(isInEditMode()){
            income=19000;
            expense=4500;
        }*/



    }



    public void update(int income, int expense){
        this.income=income;
        this.expense=expense;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawPieDiagram(canvas);
        } else {
            drawRectDiagram(canvas);
        }
    }

    private void drawPieDiagram(Canvas canvas){
        if (expense + income == 0)
            return;
        float expenseAngle = 360.f * expense / (expense + income);
        float incomeAngle = 360.f * income / (expense + income);
        int space = 10; // space between income and expense
        int size = Math.min(getWidth(), getHeight()) - space * 2;
        final int xMargin = (getWidth() - size) / 2, yMargin = (getHeight() - size) / 2;
        canvas.drawArc(xMargin - space, yMargin, getWidth() - xMargin - space, getHeight() - yMargin, 180 - expenseAngle / 2, expenseAngle, true, expensePaint);
        canvas.drawArc(xMargin + space, yMargin, getWidth() - xMargin + space, getHeight() - yMargin, 360 - incomeAngle / 2, incomeAngle, true, incomePaint);


    }

    private void drawRectDiagram(Canvas canvas){

        if(expense+income==0)
            return;

        long max=Math.max(expense,income);
        long expenseHeight=getHeight()*expense/max;
        long incomeHeight=getHeight()*income/max;

        int w=getWidth()/4;

        canvas.drawRect(w/2,getHeight()-expenseHeight,w*3/2,getHeight(),expensePaint);
        canvas.drawRect(5*w/2,getHeight()-incomeHeight,w*7/2,getHeight(),incomePaint);

    }
}
