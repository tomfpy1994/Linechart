package com.example.bunnyfung.linechart;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String jsonString = "";
    Button btn1h, btn24h;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1h = (Button)findViewById(R.id.btn1h);
        btn24h = (Button)findViewById(R.id.btn24h);
        chart = (LineChart) findViewById(R.id.chart);

        ArrayList<HashMap<String, String>> list = formalJSONString("24hours");
        loadChart(list);

        btn1h.setOnClickListener(this);
        btn24h.setOnClickListener(this);
    }

    public void loadChart(ArrayList<HashMap<String, String>> list){
        LineData mLineData = makeLineData(list);       // 制作7個數據點（沿x坐標軸）
        setChartStyle(chart, mLineData, Color.WHITE);
    }

    public String loadJSONFromAsset() {
        String json = "";
        try {
            InputStream is = getResources().openRawResource(R.raw.seatingdate);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public ArrayList<HashMap<String, String>> formalJSONString(String gp){
        ArrayList<HashMap<String, String>> formList = new ArrayList<HashMap<String, String>>();
        try{
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jArry = obj.getJSONArray(gp);
            formList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> m_li;

            for(int i=0; i<jArry.length(); i++){
                JSONObject jo_inside = jArry.getJSONObject(i);
                String time = jo_inside.getString("Time");
                String value = jo_inside.getString("value");

                m_li = new HashMap<String, String>();
                m_li.put("time", time);
                m_li.put("value", value);

                formList.add(m_li);
            }
            Toast.makeText(MainActivity.this, "formList"+formList.size() , Toast.LENGTH_LONG).show();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return formList;
    }

    private void setChartStyle(LineChart mLineChart, LineData lineData, int color) {
        // 是否在折線圖上添加邊框
        mLineChart.setDrawBorders(false);
        mLineChart.setDescription(null);
        mLineChart.setNoDataTextDescription("Date Empty!");

        // 是否繪制背景顔色。
        mLineChart.setDrawGridBackground(false);
        mLineChart.setGridBackgroundColor(Color.CYAN);
        // 觸摸
        mLineChart.setTouchEnabled(true);
        // 拖拽
        mLineChart.setDragEnabled(true);
        // 縮放
        mLineChart.setScaleEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setBackgroundColor(color);
        mLineChart.setData(lineData);
        // 設置比例圖標示，就是那個一組y的value的
        Legend mLegend = mLineChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 樣式
        mLegend.setFormSize(15.0f);// 字體
        mLegend.setTextColor(Color.BLUE);// 顔色
        // 沿x軸動畫，時間2000毫秒。
        //mLineChart.animateX(1000);
    }

    private LineData makeLineData(ArrayList<HashMap<String, String>> list) {
        ArrayList<HashMap<String, String>> t_List = list;
        Toast.makeText(MainActivity.this, "tList"+t_List.size() , Toast.LENGTH_LONG).show();
        int count = t_List.size();
        ArrayList<String> x = new ArrayList<String>();
        // y軸的數據
        ArrayList<Entry> y = new ArrayList<Entry>();

        for (int i=0; i< count;i++){
            HashMap<String, String> m_li = list.get(i);
            x.add(m_li.get("time"));

            float val = Float.parseFloat(m_li.get("value"));
            Entry entry = new Entry(val,i);
            y.add(entry);
        }
        // y軸數據集
        LineDataSet mLineDataSet = new LineDataSet(y, "123");
        // 用y軸的集合來設置參數

        mLineDataSet.setLineWidth(3.0f);             // 線寬
        mLineDataSet.setCircleSize(5.0f);           // 顯示的圓形大小
        mLineDataSet.setColor(Color.GREEN);        // 折線的顔色
        mLineDataSet.setCircleColor(Color.GREEN);   // 圓球的顔色

        // 設置mLineDataSet.setDrawHighlightIndicators(false)後，
        // Highlight的十字交叉的縱橫線將不會顯示，
        // 同時，mLineDataSet.setHighLightColor(Color.CYAN)失效。
        mLineDataSet.setDrawHighlightIndicators(false);


        mLineDataSet.setHighLightColor(Color.CYAN); // 按擊後，十字交叉線的顔色
        mLineDataSet.setValueTextSize(10.0f);       // 設置這項上顯示的數據點的字體大小。

        mLineDataSet.setDrawCircleHole(true);

        // 改變折線樣式，用曲線。
        mLineDataSet.setDrawCubic(true);
        // 默認是直線
        // 曲線的平滑度，值越大越平滑。
        mLineDataSet.setCubicIntensity(0.2f);

        // 填充曲線下方的區域，紅色，半透明。
        mLineDataSet.setDrawFilled(true);
        mLineDataSet.setFillAlpha(30);
        mLineDataSet.setFillColor(Color.GREEN);

        // 填充折線上數據點、圓球裏面包裹的中心空白處的顔色。
        mLineDataSet.setCircleColorHole(Color.WHITE);

        // 設置折線上顯示數據的格式。如果不設置，將默認顯示float數據格式。
        mLineDataSet.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                int n = (int) v;
                String s = "";
                return s;
            }
        });

        ArrayList<LineDataSet> mLineDataSets = new ArrayList<LineDataSet>();
        mLineDataSets.add(mLineDataSet);

        LineData mLineData = new LineData(x, mLineDataSets);

        return mLineData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        ArrayList<HashMap<String, String>> list = null;
        chart.removeAllViews();
        switch(v.getId()){
            case R.id.btn1h: Toast.makeText(MainActivity.this, "Btn1", Toast.LENGTH_LONG).show();
                list = formalJSONString("1hours");
                loadChart(list);
                break;
            case  R.id.btn24h: Toast.makeText(MainActivity.this, "Btn2", Toast.LENGTH_LONG).show();
                list = formalJSONString("24hours");
                loadChart(list);
                break;
        }
    }
}
