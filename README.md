# R2D2Injector
Bind Android views and callbacks to fields and methods like **ButterKnife**

Eliminate findViewById calls by using @InjectSameId on fields.
Eliminate anonymous inner-classes for listeners by annotating methods with @OnClickSameId and others.

Better than butterknife：**Bind same view type together !**

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
