package com.example.tom.androidgraph;

import com.androidplot.xy.XYSeries;

import java.util.ArrayList;

/**
 * Created by tom on 7-9-16.
 */
class SensorValueSeries implements XYSeries {
    private final int _capacity;
    private final ArrayList<SensorSample> _data;

    private Number domainMin;
    private Number domainMax;
    private Number rangeMin;
    private Number rangeMax;
    public float RangeStep;

    public void setRangeMin(Number value) {
        rangeMin = value;
    }

    public void setRangeMax(Number value) {
        rangeMax = value;
    }

    public Number getDomainMin() {
        return domainMin;
    }

    public void setDomainMin(Number value) {
        domainMin = value;
    }

    public Number getDomainMax() {
        return domainMax;
    }

    public void setDomainMax(Number value) {
        domainMax = value;
    }

    public int capacity() {
        return _capacity;
    }

    public SensorValueSeries(int capacity) {
        _capacity = capacity;
        _data = new ArrayList<>();
    }

    public void add(SensorSample sample) {

        if (size() >= capacity() )
            _data.remove(0);
        _data.add(sample);

        if( size() >= capacity() )
            domainMin = _data.get(0).getTime();

        SensorSample last = _data.get(_data.size() - 1 );
        domainMax = Math.max( domainMax.longValue(), last.getTime() );

        rangeMin = Math.min( rangeMin.floatValue(), sample.Density );
        rangeMax = Math.max( rangeMax.floatValue(), sample.Density );


    }

    @Override
    public int size() {
        return _data.size();
    }

    @Override
    public Number getX(int index) {

        SensorSample s = _data.get(index);

        return s.getTime();
    }

    @Override
    public Number getY(int index) {

        SensorSample s = _data.get(index);

        return s.Density;
    }

    @Override
    public String getTitle() {
        return null;
    }

    public Number getRangeMin(){
        return Math.floor( rangeMin.floatValue() / RangeStep ) * RangeStep;
    }

    public Number getRangeMax(){
        return Math.ceil( rangeMax.floatValue() / RangeStep ) * RangeStep;
    }
}
