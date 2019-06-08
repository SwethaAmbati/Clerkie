package app.com.clerkie.ui.chart;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.com.clerkie.R;
import app.com.clerkie.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChartActivity extends AppCompatActivity implements OnChartValueSelectedListener, OnChartGestureListener {


    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart horizontalBarChart;
    @BindView(R.id.pie_chart)
    PieChart pieChart;
    @BindView(R.id.double_line_chart)
    LineChart doubleLineChart;
    @BindView(R.id.single_line_chart)
    LineChart singleLineChart;
    @BindView(R.id.vertical_bar_chart)
    BarChart verticalBarChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_screen);
        ButterKnife.bind(this);

        pieChart();
        doubleLineChart();
        horizontalBarChart();
        singleLineChart();
        verticalBarChart();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void horizontalBarChart(){

        horizontalBarChart.setDrawBarShadow(false);
        horizontalBarChart.setDrawValueAboveBar(true);

        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.setPinchZoom(true);
        horizontalBarChart.setDrawGridBackground(false);

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(258f, 175f));
        barEntries.add(new BarEntry(191f, 104f));
        barEntries.add(new BarEntry(194f, 96f));
        barEntries.add(new BarEntry(315f, 87f));
        barEntries.add(new BarEntry(172f, 70f));
        barEntries.add(new BarEntry(267f, 65f));
        barEntries.add(new BarEntry(189f, 62f));
        barEntries.add(new BarEntry(109f, 53f));

        BarDataSet set = new BarDataSet(barEntries, "BarDataSet");
        set.setColors(ViewUtils.getColorInts());
        set.setDrawValues(true);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        data.setBarWidth(8f);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        horizontalBarChart.setData(data);

        horizontalBarChart.setFitBars(true);
        horizontalBarChart.animateXY(1000, 1000);
        horizontalBarChart.invalidate();

        horizontalBarChart.getLegend().setEnabled(false);
    }

    private void pieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterText(generateCenterSpannableText());
        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        List<PieEntry> yvalues = new ArrayList<>();
        yvalues.add(new PieEntry(30, "Housing"));
        yvalues.add(new PieEntry(10, "Insurance"));
        yvalues.add(new PieEntry(10, "Retirement"));
        yvalues.add(new PieEntry(10, "Education"));
        yvalues.add(new PieEntry(10, "Taxes"));
        yvalues.add(new PieEntry(20, "Spending"));
        yvalues.add(new PieEntry(10, "Savings"));

        PieDataSet dataSet = new PieDataSet(yvalues, "Budget");

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(13f);
        pieData.setValueTextColor(Color.DKGRAY);

        dataSet.setColors(ViewUtils.getColorInts());

        pieChart.setOnChartValueSelectedListener(this);
        pieChart.animateXY(1400, 1400);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        pieChart.setData(pieData);
    }

    private void doubleLineChart() {
        doubleLineChart.setOnChartGestureListener(this);
        doubleLineChart.setDrawGridBackground(false);

        doubleLineChart.getDescription().setEnabled(false);
        doubleLineChart.setDragDecelerationFrictionCoef(0.9f);

        List<Entry> values1 = new ArrayList<>();
        values1.add(new Entry(258f, 175f));
        values1.add(new Entry(191f, 104f));
        values1.add(new Entry(194f, 96f));
        values1.add(new Entry(315f, 87f));
        values1.add(new Entry(189f, 62f));

        List<Entry> values2 = new ArrayList<>();
        values2.add(new Entry(109f, 53f));
        values2.add(new Entry(172f, 70f));
        values2.add(new Entry(267f, 65f));
        Collections.sort(values1, new EntryXComparator());
        Collections.sort(values2, new EntryXComparator());

        LineDataSet lineDataSet1 = new LineDataSet(values1, "Budget - Old");
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineDataSet lineDataSet2 = new LineDataSet(values2, "Budget - New");
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT);


        // Animating Chart Line1
        lineDataSet1.setColor(Color.RED);
        lineDataSet1.setCircleColor(Color.BLACK);
        lineDataSet1.setLineWidth(2f);
        lineDataSet1.setCircleRadius(3f);
        lineDataSet1.setDrawCircleHole(false);
        lineDataSet1.setValueTextSize(11f);
        lineDataSet1.setDrawFilled(true);
        lineDataSet1.setFormLineWidth(1f);
        lineDataSet1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        lineDataSet1.setFormSize(15.f);
        lineDataSet1.setFillColor(Color.WHITE);

        // Animating Chart Line2
        lineDataSet2.setColor(Color.BLUE);
        lineDataSet2.setCircleColor(Color.BLACK);
        lineDataSet2.setLineWidth(2f);
        lineDataSet2.setCircleRadius(3f);
        lineDataSet2.setDrawCircleHole(false);
        lineDataSet2.setValueTextSize(11f);
        lineDataSet2.setDrawFilled(true);
        lineDataSet2.setFormLineWidth(1f);
        lineDataSet2.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        lineDataSet2.setFormSize(15.f);
        lineDataSet2.setFillColor(Color.WHITE);

        List<ILineDataSet> lineDataSet = new ArrayList<ILineDataSet>();
        lineDataSet.add(lineDataSet1);
        lineDataSet.add(lineDataSet2);

        LineData lineData = new LineData(lineDataSet);
        doubleLineChart.setData(lineData);
        doubleLineChart.invalidate();
        doubleLineChart.animateX(2500);

        // legend
        Legend legend = doubleLineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextSize(11f);
        legend.setTextColor(Color.DKGRAY);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(true);
    }

    private void verticalBarChart() {
        verticalBarChart.setDrawBarShadow(false);
        verticalBarChart.setDrawValueAboveBar(true);

        verticalBarChart.getDescription().setEnabled(false);
        verticalBarChart.setPinchZoom(false);
        verticalBarChart.setDrawGridBackground(false);

        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(258f, 175f));
        barEntries.add(new BarEntry(191f, 104f));
        barEntries.add(new BarEntry(194f, 96f));
        barEntries.add(new BarEntry(315f, 87f));
        barEntries.add(new BarEntry(172f, 70f));
        barEntries.add(new BarEntry(267f, 65f));
        barEntries.add(new BarEntry(189f, 62f));
        barEntries.add(new BarEntry(109f, 53f));

        BarDataSet set = new BarDataSet(barEntries, "BarDataSet");
        set.setColors(ViewUtils.getColorInts());

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);

        BarData data = new BarData(dataSets);
        data.setBarWidth(8f);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);

        verticalBarChart.setData(data);
        verticalBarChart.setFitBars(true);
        verticalBarChart.invalidate();
        verticalBarChart.getLegend().setEnabled(false);
    }

    private void singleLineChart() {
        singleLineChart.setOnChartGestureListener(this);
        singleLineChart.setDrawGridBackground(false);

        singleLineChart.getDescription().setEnabled(false);
        singleLineChart.setDragDecelerationFrictionCoef(0.9f);

        List<Entry> values = new ArrayList<>();
        values.add(new Entry(258f, 175f));
        values.add(new Entry(191f, 104f));
        values.add(new Entry(194f, 96f));
        values.add(new Entry(315f, 87f));
        values.add(new Entry(172f, 70f));
        values.add(new Entry(267f, 65f));
        values.add(new Entry(189f, 62f));
        values.add(new Entry(109f, 53f));
        Collections.sort(values, new EntryXComparator());

        LineDataSet lineDataSet = new LineDataSet(values, "Budget");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);


        lineDataSet.setDrawIcons(true);

        // Animating Chart Line
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setValueTextSize(11f);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
        lineDataSet.setFormSize(15.f);
        lineDataSet.setFillColor(Color.WHITE);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);

        LineData data = new LineData(dataSets);
        singleLineChart.setData(data);
        singleLineChart.animateX(2500);

        // legend
        Legend l = singleLineChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private SpannableString generateCenterSpannableText() {
        return new SpannableString("Pie Chart\n Budget");
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null) {
            return;
        }
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            doubleLineChart.highlightValues(null);
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }



}
