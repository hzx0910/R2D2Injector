# R2D2Injector
Bind Android views and callbacks to fields and methods like **ButterKnife**

Eliminate findViewById calls by using @InjectSameId on fields.
Eliminate anonymous inner-classes for listeners by annotating methods with @OnClickSameId and others.

**Step 1. Add the JitPack repository to your root build.gradle:**
```groovy

allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```

**Step 2. Add the dependency**
```groovy

dependencies {
    implementation 'com.github.hzx0910.R2D2Injector:inject-annotation:1.0.1'
    annotationProcessor 'com.github.hzx0910.R2D2Injector:inject-compiler:1.0.1'
}
```

**Better than ButterKnife : Bind same view type together !**
```java
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
```

**AnnotationProcessor generate injector file like this :**
```java
public class LoginActivityInjector implements Injector<LoginActivity>, View.OnClickListener {
     
  private LoginActivity target;
 
  @Override
  public void inject(LoginActivity target) {
    inject(target,target);
  }
 
  @Override
  public void inject(LoginActivity target, Object source) {
    this.target=target;
    target.edit_email=find(source,R.id.edit_email);
    target.edit_password=find(source,R.id.edit_password);
    target.text_notice=find(source,R.id.text_notice);
    find(source,R.id.button_sign_in).setOnClickListener(this);
    find(source,R.id.button_help).setOnClickListener(this);
  }
 
  @Override
  public void onClick(View view) {
    switch(view.getId()) {
      case R.id.button_sign_in:
        target.button_sign_in();
        break;
      case R.id.button_help:
        target.button_help((Button)view);
        break;
    }
  }
 
  public <T extends View> T find(Object source, int id) {
    if (source instanceof View) {
      return (T) ((View) source).findViewById(id);
    } else if (source instanceof Activity)  {
      return (T) ((Activity) source).findViewById(id);
    } else if (source instanceof Dialog)  {
      return (T) ((Dialog) source).findViewById(id);
    }
    return null;
  }
}
```