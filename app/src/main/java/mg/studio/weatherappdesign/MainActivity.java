package mg.studio.weatherappdesign;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public  void CheckNet(){
        Context context = this.getApplicationContext();
        ConnectivityManager connectivityManager =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = connectivityManager.getActiveNetworkInfo();
        if(network == null){
            Toast.makeText(MainActivity.this,"网络连接不可用",Toast.LENGTH_LONG).show();}}

    public void btnClick(View view) {
        CheckNet();
        new DownloadUpdate().execute();
    }

    public void btnClick_flush(View view) {
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWek = String.valueOf(c.get(Calendar.DAY_OF_WEEK));//获取当前星期几
        String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期
        TextView tv_time = (TextView)findViewById(R.id.tv_date);
        TextView tv_week = (TextView)findViewById(R.id.tv_week);

        if("1".equals(mWek)){
            mWek ="Sunday";
        }else if("2".equals(mWek)){
            mWek ="Monday";
        }else if("3".equals(mWek)){
            mWek ="Tuesday";
        }else if("4".equals(mWek)){
            mWek ="Wednesday    ";
        }else if("5".equals(mWek)){
            mWek ="Thursday";
        }else if("6".equals(mWek)){
            mWek ="Friday";
        }else if("7".equals(mWek)){
            mWek ="Saturday";
        }
        String temp = mDay+"/"+mMonth+"/"+mYear;
        tv_time.setText(temp);
        tv_week.setText(mWek);
    }

    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            String buffer = "";
            String low = "";
            String high = "";
            try {
                URL url=new URL("http://wthrcdn.etouch.cn/WeatherApi?citykey=101040100");
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                //设置请求方式
                connection.setRequestMethod("GET");
                //设置连接超时的时间（优化）
                connection.setConnectTimeout(5000);
                //结果码（状态）//成功200   失败 未修改304
                //获取结果码
                int code=connection.getResponseCode();
                if(code==200){
                    InputStream is=connection.getInputStream();
                    //使用PULL解析
                    XmlPullParser xmlPullParser= Xml.newPullParser();
                    xmlPullParser.setInput(is,"UTF-8");
                    //获取解析的标签的类型
                    int type=xmlPullParser.getEventType();
                    while(type!=XmlPullParser.END_DOCUMENT){
                        switch (type) {
                            case XmlPullParser.START_TAG:
                                //获取开始标签的名字
                                String starttgname = xmlPullParser.getName();
                                if ("date".equals(starttgname)) {
                                    String date = xmlPullParser.nextText();
                                }
                                if ("high".equals(starttgname)) {
                                    //获取high的值
                                    high = xmlPullParser.nextText();
                                    high = high.substring(3,5);
                                } else if ("low".equals(starttgname)) {
                                    //获取low的值
                                    low = xmlPullParser.nextText();
                                    low = low.substring(3,5);
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                break;
                        }
                        buffer = low + "-" + high;
                        type=xmlPullParser.next();
                    }
                    //返回温度
                    return buffer;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temperature);
        }
    }
}