package pt.fraunhofer.pulse.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PulseView extends View {

    public PulseView(Context context) {
        super(context);
        init();
    }

    public PulseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PulseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private double[] pulse;
    
    private Paint pulsePaint;
    private Paint zeroPaint;
    private Paint gridPaint;
    
    private void init() {
        setNoPulse();
        
        setBackgroundColor(Color.DKGRAY);
        pulsePaint = initPulsePaint();
        zeroPaint = initZeroPaint();
        gridPaint = initGridPaint();
    }
    
    private Paint initPulsePaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);
        p.setStrokeWidth(2);
        return p;
    }

    private Paint initZeroPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.LTGRAY);
        p.setStrokeWidth(2);
        return p;
    }
    
    private Paint initGridPaint() {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.LTGRAY);
        p.setStrokeWidth(1);
        return p;
    }
    
    public double[] getPulse() {
        return pulse;
    }
    
    public void setPulse(final double[] pulse) {
        this.pulse = pulse;
        invalidate();
    }
    
    public void setNoPulse() {
        setPulse(new double[0]);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0, y(0), getWidth(), y(0), zeroPaint);
        canvas.drawLine(0, 0, 0, getHeight(), gridPaint);
        for (int i = 1; i < pulse.length; i++) {
            canvas.drawLine(x(i), 0, x(i), getHeight(), gridPaint);
            canvas.drawLine(x(i-1), y(pulse[i-1]), x(i), y(pulse[i]), pulsePaint);
        }
    }
    
    private float x(int x) {
        return x * getWidth() / (pulse.length - 1);
    }
    
    private float y(double y) {
        return (float)(getHeight() / 2. - y * getHeight() / 3.);
    }
    
}
