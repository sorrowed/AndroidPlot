package com.example.tom.androidgraph;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    private final String topic = "/sensors/78C40E03AA71/dust";
    private final String host = "tcp://gost.geodan.nl:1883";

    private MqttClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window w = getWindow();
        w.requestFeature(Window.FEATURE_ACTION_BAR);

        ActionBar b = getSupportActionBar();
        if (b != null)
            b.hide();

        setContentView(R.layout.activity_main);

        try {
            connect(host);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void connect(String uri) throws MqttException {

        if (client != null)
            return;

        MemoryPersistence persistence = new MemoryPersistence();

        client = new MqttClient(uri, "test", persistence);
        client.setCallback(this);

        client.connect();

        client.subscribe(topic);
    }

    private void disconnect() throws MqttException {

        if (client == null)
            return;

        client.unsubscribe(topic);
        client.disconnect();
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, final MqttMessage message) throws Exception {

        FragmentManager mgr = getSupportFragmentManager();

        SensorSeriesFragment fragment = (SensorSeriesFragment)mgr.findFragmentById( R.id.plotFragment);

        SensorSample sample = fragment.parseSample( message.toString());

        fragment.add( sample );
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
