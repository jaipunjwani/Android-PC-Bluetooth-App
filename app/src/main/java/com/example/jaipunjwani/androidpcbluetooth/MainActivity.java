package com.example.jaipunjwani.androidpcbluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.JsonToken;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonToken;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    Button subscribeButton;
    Button publishButton;
    Button launchBluetoothButton;
    TextView channelTextView;
    EditText messagesEditText;
    EditText statusEditText;

    PubNub pubNub;

    private static final String CHANNEL = "my_channel_JAI_2";
    private static final String SUBSCRIBE_KEY = "sub-c-c2b7158a-bcd2-11e6-b737-0619f8945a4f";
    private static final String PUBLISH_KEY = "pub-c-d84f1266-0118-4890-967b-40b3372b4a6a";
    public static  final String PUBNUB = "Pubnub";

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

        initiatePubNubInstance();



    }

    public void initiatePubNubInstance() {
        PNConfiguration pnConfiguration = new PNConfiguration();
        pnConfiguration.setLogVerbosity(PNLogVerbosity.BODY);
        pnConfiguration.setPublishKey(PUBLISH_KEY);
        pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
        pnConfiguration.setSecure(false);

        pubNub = new PubNub(pnConfiguration);
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
                    JsonNode node = message.getMessage();

                    String type = node.getNodeType().name();

                    final String msg = message.getMessage().asText();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleUIThread(msg);
                        }
                    });


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

    }

    public void handleUIThread(String messageText) {
        messagesEditText.setText("Message: " + messageText);
    }

    public void subscribe() {

        pubNub.subscribe().channels(Arrays.asList(CHANNEL)).execute();
    }

    public void publish() {

        pubNub.publish().channel(CHANNEL)
                .usePOST(true)
                //.shouldStore(true)
                .message("CODE@MIT")
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
