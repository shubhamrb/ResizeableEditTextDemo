# ResizeableEditTextDemo

Resizeable Edit Text like Instagram and WhatsApp Story....


# First add below code in activity_main

<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    tools:showIn="@layout/activity_main"
    android:background="@color/colorPrimary">

    <FrameLayout
        android:id="@+id/container"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:layout_centerInParent="true"
        android:background="@color/colorPrimary" >

        <example.com.totalityassesment.AutoFitEditText
            android:id="@+id/rET"
            android:layout_height="match_parent"
            android:layout_width="match_parent"
            android:background="@color/colorPrimary"
            android:focusable="true"
            android:focusableInTouchMode="true"
            android:gravity="center_vertical|center_horizontal"
            android:hint="TAP"
            android:isScrollContainer="false"
            android:inputType="textMultiLine|textNoSuggestions"
            android:maxLength="240"
            android:textColor="@android:color/white"
            android:textColorHint="@android:color/white"
            android:textSize="90sp"
            android:textCursorDrawable="@null"/>
    </FrameLayout>
</RelativeLayout>


# And below code in MainActivity.java

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


# Create custom class AutoFitEditText.java for resizing the text
