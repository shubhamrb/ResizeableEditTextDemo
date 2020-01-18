package example.com.totalityassesment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AutoFitEditText mAutoFitEditText;
    private FrameLayout mRootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView=(FrameLayout)findViewById(R.id.container);
        mAutoFitEditText=(AutoFitEditText) findViewById(R.id.rET);

        mAutoFitEditText.setEnabled(true);
        mAutoFitEditText.setFocusableInTouchMode(true);
        mAutoFitEditText.setFocusable(true);
        mAutoFitEditText.setEnableSizeCache(false);
        mAutoFitEditText.setMovementMethod(null);

        mAutoFitEditText.setMaxHeight(100);
        mAutoFitEditText.setMinTextSize(100f);
       // mAutoFitEditText.setLines(1);
        mAutoFitEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.KEYCODE_BREAK ){

                    Toast.makeText(MainActivity.this, "PRESSED", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

    }
}
