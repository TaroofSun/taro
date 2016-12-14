package taro.com.circlebartest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private CircleBar circleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        circleBar = (CircleBar) findViewById(R.id.circleBar);

    }

    public void changeData(View view) {
        //改变数据
        circleBar.setmBarMax(150);
        circleBar.setmBarProgress(50);
    }

    public void changeColor(View view) {
        //改变颜色
        circleBar.setmBarColor(Color.RED);
        circleBar.setmHintColor(Color.GRAY);
    }
}
