package make.more.r2d2.r2d2injector;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ServiceLoader;

import make.more.r2d2.annotation.InjectSameId;
import make.more.r2d2.annotation.Injector;
import make.more.r2d2.annotation.OnClickSameId;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    @InjectSameId(R.class)
    EditText edit_email, edit_password;
    @InjectSameId(R.class)
    TextView text_notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new LoginActivityInjector().inject(this);

        ServiceLoader<Injector> loader = ServiceLoader.load(Injector.class);
        text_notice.setText("");
        for (Injector injector : loader) {
            text_notice.append(injector.getClass().getName());
        }
    }

    @OnClickSameId(R.class)
    void button_sign_in() {
        String text = "button_sign_in click:\n" + edit_email.getText() + "\n" + edit_password.getText();
        text_notice.setText(text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }

    @OnClickSameId(R.class)
    void button_help(Button button) {
        String text = "button_help click:\n" + button.getText();
        text_notice.setText(text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }
}

