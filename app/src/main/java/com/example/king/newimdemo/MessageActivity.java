package com.example.king.newimdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private ImageView jieshouimg;
    private Button text;
    private Button img;
    private TextView jieshoutext;
    private LinearLayout msgLinear;
    private EditText username;
    private EditText content;
    private Button btnpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);

        jieshouimg = ((ImageView) findViewById(R.id.jieshouimg));
        jieshoutext = ((TextView) findViewById(R.id.jieshoutext));
        img = ((Button) findViewById(R.id.img));
        text = ((Button) findViewById(R.id.text));
        msgLinear = ((LinearLayout) findViewById(R.id.meglinear));
        username = ((EditText) findViewById(R.id.username));
        content = ((EditText) findViewById(R.id.content));
        btnpp = ((Button) findViewById(R.id.btnp2p));
        btnpp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //启动单聊界面
                //NimUIKit.init(MessageActivity.this);
                NimUIKit.startP2PSession(MessageActivity.this, username.getText().toString().toLowerCase());

            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendText();
                View inflate = getLayoutInflater().inflate(R.layout.msg_textme, null);
                TextView metext = (TextView) inflate.findViewById(R.id.zjtext);
                metext.setText(content.getText().toString());

                final IMMessage message = MessageBuilder.createTextMessage(username.getText().toString(), SessionTypeEnum.P2P, metext.getText().toString());
                inflate.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        NIMClient.getService(MsgService.class)
                                .revokeMessage(message).setCallback(new RequestCallback<Void>() {
                            @Override
                            public void onSuccess(Void param) {
                                Log.i("结果", "撤销成功");
                            }

                            @Override
                            public void onFailed(int code) {
                                Log.i("结果", "撤销失败==" + code);
                            }

                            @Override
                            public void onException(Throwable exception) {
                                Log.i("结果", "撤销异常");
                            }

                        });
                        return false;
                    }
                });
                msgLinear.addView(inflate);
            }
        });
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendImg();
            }
        });
    }

    private void sendText() {
        IMMessage message = MessageBuilder.createTextMessage(username.getText().toString().toLowerCase(), SessionTypeEnum.P2P, content.getText().toString());

        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
            @Override
            public void onSuccess(Void param) {
                Log.i("结果", "发送成功");
            }

            @Override
            public void onFailed(int code) {
                Log.i("结果", "发送失败:==代码==" + code);
            }

            @Override
            public void onException(Throwable exception) {
                Log.i("结果", "发送异常：代码==" + exception.getMessage());
            }
        });
    }

    private void sendImg() {

    }

    Observer<List<IMMessage>> incomingMessageObserver =
            new Observer<List<IMMessage>>() {
                @Override
                public void onEvent(List<IMMessage> messages) {
                    View inflate = getLayoutInflater().inflate(R.layout.msg_text, null);
                    TextView pytext = ((TextView) inflate.findViewById(R.id.pytext));

                    msgLinear.addView(inflate);
                    // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                    for (int i = 0; i < messages.size(); i++) {
                        Log.i("接收到的消息", messages.get(i).getContent());
                        pytext.setText(messages.get(i).getContent());
                    }
                }
            };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, false);
    }
}
