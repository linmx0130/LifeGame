package com.sweetdum.lifegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by sweetdum on 2015/4/29.
 */
public class BoardView extends View {
    private static final int PADDING=5;
    private static final int VERTICAL_BLOCKS=20;
    private static final int HORIZONTAL_BLOCKS=16;
    private int BLOCK_SIZE=25;
    private boolean beLife[][];
    private int startX,endX,startY,endY;
    private int waitTime=1000;
    private AutoGen autoGen;
    public BoardView(Context context) {
        this(context,null);

    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        beLife=new boolean[HORIZONTAL_BLOCKS][VERTICAL_BLOCKS];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height=canvas.getHeight();
        int width=canvas.getWidth();
        startX=PADDING;endX=width-PADDING;
        startY=PADDING;endY=height-PADDING;
        BLOCK_SIZE= Math.min((endX - startX) / HORIZONTAL_BLOCKS, (endY - startY) / VERTICAL_BLOCKS);
        Log.d("BroadView", "Size="+BLOCK_SIZE);
        Paint[] blockColor=new Paint[2];
        blockColor[0]=new Paint();
        blockColor[0].setColor(0xff2222f0);
        blockColor[1]=new Paint();
        blockColor[1].setColor(0xffffff11);
        Paint lineColor=new Paint();
        lineColor.setColor(0xff111111);
        for (int i=0;i<HORIZONTAL_BLOCKS;++i){
            for (int j=0;j<VERTICAL_BLOCKS;++j){
                int c=(beLife[i][j]?1:0);
                canvas.drawRect(startX+i*BLOCK_SIZE,startY+j*BLOCK_SIZE,
                        startX+(i+1)*BLOCK_SIZE,
                        startY+(j+1)*BLOCK_SIZE,
                        blockColor[c]);
            }
        }
        for (int i=0;i<HORIZONTAL_BLOCKS;++i) {
            for (int j=0;j<VERTICAL_BLOCKS;++j) {
                int x=startX+i*BLOCK_SIZE;
                int y=startY+j*BLOCK_SIZE;
                canvas.drawLine(x, y, x + BLOCK_SIZE, y, lineColor);
                canvas.drawLine(x, y, x, y + BLOCK_SIZE, lineColor);
            }
        }
    }
    public boolean isLife(int x,int y){
        return beLife[x][y];
    }
    public void setLife(int x,int y, boolean v){
        beLife[x][y]=v;
    }
    private long lastTouchTime=0;
    private int lastX,lastY;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        long delta=event.getEventTime()-lastTouchTime;
        lastTouchTime=event.getEventTime();
        for (int i=0;i<HORIZONTAL_BLOCKS;++i){
            for (int j=0;j<VERTICAL_BLOCKS;++j){
                int xx=startX+i*BLOCK_SIZE;
                int yy=startY+j*BLOCK_SIZE;
                if (x>xx && x<xx+BLOCK_SIZE && y>yy && y<yy+BLOCK_SIZE){
                    if (lastX==i && lastY==j && delta<100){
                        break;
                    }
                    beLife[i][j]=!beLife[i][j];
                    lastX=i;lastY=j;
                }
            }
        }
        invalidate();
        return true;
    }
    public void nextGeneration(){
        boolean [][] nextArray=new boolean[HORIZONTAL_BLOCKS][VERTICAL_BLOCKS];
        final int[][] dxy={{-1,0},{1,0},{0,-1},{0,1},{-1,-1},{1,-1},{-1,1},{1,1}};
        for (int i=0;i<HORIZONTAL_BLOCKS;++i){
            for (int j=0; j<VERTICAL_BLOCKS; ++j){
                int count=0;
                for (int k=0;k<8;++k){
                    int nx=i+dxy[k][0];
                    int ny=j+dxy[k][1];
                    if (nx<0 || nx>=HORIZONTAL_BLOCKS || ny<0 || ny>=VERTICAL_BLOCKS) continue;
                    count += beLife[nx][ny]?1:0;
                }
                if (count==3) {
                    nextArray[i][j]=true;
                } else if (count==2) {
                    nextArray[i][j]=beLife[i][j];
                } else {
                    nextArray[i][j]=false;
                }
            }
        }
        beLife=nextArray;
        invalidate();
    }

    private class AutoGen extends AsyncTask<Integer, Void, Void> {
        private boolean isWorking;
        @Override
        protected Void doInBackground(Integer... params) {
            int sleepTime=params[0];
            isWorking=true;
            while (isWorking){
                try {
                    Thread.sleep((long) sleepTime);
                }catch(Exception e){
                }
                publishProgress();
            }
            return null;
        }

        public boolean isWorking() {
            return isWorking;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            nextGeneration();
        }
        public void stop(){
            isWorking=false;
        }
    }
    public void startAutoGen(){
        if (autoGen!=null){
            autoGen.stop();
        }
        autoGen=new AutoGen();
        autoGen.execute(waitTime);
    }
    public void stopAutoGen(){
        if (autoGen!=null){
            if (autoGen.isWorking()){
                autoGen.stop();
            }
        }
    }
    public boolean isAutoGen(){
        if (autoGen!=null){
            if (autoGen.isWorking()) return true;
        }
        return false;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }
}
