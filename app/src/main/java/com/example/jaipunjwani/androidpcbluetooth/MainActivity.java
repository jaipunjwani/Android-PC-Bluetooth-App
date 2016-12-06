package com.example.jaipunjwani.androidpcbluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.w3c.dom.Text;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    Button subscribeButton;
    Button publishButton;
    Button launchBluetoothButton;
    TextView channelTextView;
    EditText messagesEditText;
    EditText statusEditText;

    private static final String CHANNEL = "my_channel";
    private static final String SUBSCRIBE_KEY = "SubscribeKey";
    private static final String PUBLISH_KEY = "PublishKey";
    public static final String PUBNUB = "Pubnub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        channelTextView = (TextView) findViewById(R.id.channelTextView);
        messagesEditText = (EditText) findViewById(R.id.messagesEditText);
        statusEditText = (EditText) findViewById(R.id.statusEditText);
        subscribeButton = (Button) findViewById(R.id.subscribeButton);
        publishButton = (Button) findViewById(R.id.publishButton);
        launchBluetoothButton = (Button) findViewById(R.id.launchBluetoothButton);

        channelTextView.setText("Channel: " + CHANNEL);

        subscribeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribe();
            }
        });

        publishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                publish();
            }
        });

        launchBluetoothButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // launch bluetooth activity to connect with nearby computer
            }
        });

    }

    public void subscribe() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        // pnConfiguration.setPublishKey(PUBLISH_KEY);
        pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
        pnConfiguration.setSecure(false);

        PubNub pubNub = new PubNub(pnConfiguration);
        pubNub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {


                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                    Log.i(PUBNUB, "connection disconnected");

                }

                else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    Log.i(PUBNUB, "subscribed");
                    statusEditText.setText("Status: subscribed");
                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc



                }
                else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                }
                else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
                else {
                    statusEditText.setText("Status: " + "Subscription Error " + status.getCategory().name());
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // Handle new message stored in message.message
                if (message.getChannel() != null) {
                    Log.i("received", message.getMessage().asText());
                    messagesEditText.setText("Message: " + message.getMessage().asText());
                    // Message has been received on channel group stored in
                    // message.getChannel()
                }
                else {
                    Log.i("error", "message.getChannel == null");
                    statusEditText.setText("Error: message channel null");
                    // Message has been received on channel stored in
                    // message.getSubscription()
                }

            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscription()
                    - message.getTimetoken()
            */
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubNub.subscribe().channels(Arrays.asList(CHANNEL)).execute();
    }

    public void publish() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setPublishKey(PUBLISH_KEY);
        //pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
        pnConfiguration.setSecure(false);

        PubNub pubnub = new PubNub(pnConfiguration);




        pubnub.publish().channel(CHANNEL)
                .usePOST(true)
                .shouldStore(true)
                .message(Arrays.asList("hello", "there"))
                .async(new PNCallback<PNPublishResult>() {
            @Override
            public void onResponse(PNPublishResult result, PNStatus status) {
                // Check whether request successfully completed or not.
                if (!status.isError()) {

                    Log.i("Pubnub", "message published");
                    statusEditText.setText("Status: Message published");
                }
                // Request processing failed.
                else {
                    Log.i("Pubnub", "message not published " + status.getCategory().name());
                    statusEditText.setText("Status: Message Failed to publish (" + status.getCategory().name() + ")");
                    //status.retry();
                }
            }
        });
    }

}
