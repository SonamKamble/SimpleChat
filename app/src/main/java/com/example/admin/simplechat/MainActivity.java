package com.example.admin.simplechat;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.logging.Level;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listview;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listview = (ListView) findViewById(R.id.msgview);
        chatText = (EditText) findViewById(R.id.msg);
        buttonSend = (Button) findViewById(R.id.send);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listview.setAdapter(chatArrayAdapter);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listview.setAdapter(chatArrayAdapter);

        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listview.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }


    private boolean sendChatMessage() {
        chatArrayAdapter.add(new ChatMessage(side, chatText.getText().toString()));
        chatText.setText("");
        side = !side;
        return true;
    }

    public void onClick(View view) {
        String message=chatText.getText().toString();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.6/3000/message/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChatService chatService=retrofit.create(ChatService.class);
        Call<Message> call=chatService.createMessage(message);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                int statusCode=response.code();
                Message message=response.body();
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Logger.getLogger("message").log(Level.SEVERE,t.toString());
            }
        });
        sendChatMessage();
    }
    public interface ChatService{
        @POST("message")
        Call<Message> createMessage(@Body String message);
    }
}
