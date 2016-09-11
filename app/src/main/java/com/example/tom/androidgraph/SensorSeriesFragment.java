package com.example.tom.androidgraph;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SensorSeriesFragment extends Fragment {

    private final String STATE_SAMPLES = "Samples";
    private final int SAMPLES_MAX = 500;
    private final int PLOT_RANGE_STEP = 25;

    private SensorValueSeries series;

    private Gson gson;
    private XYPlot plot;
    private Handler handler;

    public SensorSeriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();
        gson = new Gson();

        if (savedInstanceState == null) {
            series = new SensorValueSeries(SAMPLES_MAX);

            Date start = new Date();
            Date end = new Date(start.getTime() + 1000 * 5 * SAMPLES_MAX);
            series.setDomainMin(start.getTime());
            series.setDomainMax(end.getTime());


            series.setRangeMin(0);
            series.setRangeMax( 50);
            series.RangeStep = 25;
        } else {
            String samples = savedInstanceState.getString(STATE_SAMPLES);

            series = gson.fromJson(samples, SensorValueSeries.class);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sensor_series, container, false);

        plot = (XYPlot) view.findViewById(R.id.plot);

        createPlot();

        createGraph();

        plot.redraw();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        String samples = gson.toJson(series, SensorValueSeries.class);

        savedInstanceState.putString(STATE_SAMPLES, samples);

        super.onSaveInstanceState(savedInstanceState);
    }

    private void createPlot() {

        //plot.setMarkupEnabled(true);
        plot.getLegend().setVisible(false);
        plot.getDomainTitle().setVisible(true);
        plot.getRangeTitle().setVisible(true);
        plot.getTitle().setVisible(false);

        //plot.setPlotMargins(0, 0, 0, 0);
        //plot.setPlotPadding(0, 0, 0, 0);
        plot.setRangeBoundaries(series.getRangeMin(), series.getRangeMax(), BoundaryMode.FIXED);
        plot.setRangeStep(StepMode.INCREMENT_BY_VAL, PLOT_RANGE_STEP);
        plot.setRangeLabel("Density [ug/m3]");

        plot.setDomainBoundaries(series.getDomainMin(), series.getDomainMax(), BoundaryMode.FIXED);
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 60 * 2 * 1000);
        plot.setDomainLabel("Time [min]");
    }

    private void createGraph() {

        Context context = getActivity().getApplicationContext();

        LineAndPointFormatter fmt = new LineAndPointFormatter();
        fmt.configure(context, R.xml.line_point_formatter_with_labels);
        fmt.setPointLabelFormatter(null);
        /*
        fmt.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
*/

        XYGraphWidget graph = plot.getGraph();

        graph.getLineLabelStyle(
                XYGraphWidget.Edge.LEFT).setFormat(new DecimalFormat("###.#"));

        graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).
                setFormat(new Format() {

                    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                        Date d = new Date(((Number) obj).longValue());

                        return dateFormat.format(d, toAppendTo, pos);
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;
                    }
                });

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[]{PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        graph.getDomainGridLinePaint().setPathEffect(dashFx);
        graph.getRangeGridLinePaint().setPathEffect(dashFx);

        graph.setMargins(25, 10, 0, 50);
        //graph.setPadding( 0, 0, 0, 0 );

        plot.addSeries(series, fmt);
    }

    public void add(final SensorSample sample) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                series.add(sample);

                if (series.size() == series.capacity() - 1)
                    plot.setDomainBoundaries(null, null, BoundaryMode.AUTO);

                plot.setRangeBoundaries(series.getRangeMin(), series.getRangeMax(), BoundaryMode.FIXED);

                plot.redraw();
            }
        });
    }

    public SensorSample parseSample(String source) {
        SensorSample sample = gson.fromJson(source, SensorSample.class);
        if (sample != null) {
            sample.Date = new Date();
        }
        return sample;
    }
}
