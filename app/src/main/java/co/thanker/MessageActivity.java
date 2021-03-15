package co.thanker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import co.thanker.data.Message;

public class MessageActivity extends AppCompatActivity {

    private final String MESSAGE_OBJECT = "message-object";

    private TextView mTextTitle;
    private TextView mTextBody;
    private Intent mIntent;
    private Message mMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mTextTitle = (TextView) findViewById(R.id.text_message_title);
        mTextBody = (TextView) findViewById(R.id.text_message_body);

        mIntent = getIntent();

        if(mIntent != null){
            mMessage = (Message) mIntent.getSerializableExtra(MESSAGE_OBJECT);
            mTextTitle.setText(Html.fromHtml(mMessage.getTitle()));
            mTextBody.setText(Html.fromHtml(mMessage.getText()));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }
}
