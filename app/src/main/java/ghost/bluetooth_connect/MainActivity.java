// 有值後斷開藍芽
//重置按鈕
//98:D3:32:30:95:1E
package ghost.bluetooth_connect;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import static org.json.JSONObject.NULL;

public class MainActivity extends Activity {
    String BTdata;
    BluetoothAdapter mBluetoothAdapter = null;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
   // int seconds;
    //private EditText editTextId;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    int flag=0;

    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition ;
    volatile boolean stopWorker = false;
    TextView txview;
    TextView text;
    ProgressDialog loading;
    TextView textViewResult;

    //final String data_url = "https://carindex.000webhostapp.com/confirm.php";

    ArrayList<String> parameter = new ArrayList<String>();  //指定是String的型態

    Button blebtn,serbtn;
    //int count = 1;
   // String msg = "r";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);





        BluetoothAdapter adaptr = BluetoothAdapter.getDefaultAdapter();

        txview = (TextView)findViewById(R.id.txview);
        blebtn = (Button)findViewById(R.id.blebtn);
        serbtn = (Button)findViewById(R.id.serbtn);
        //ledbtn = (Button)findViewById(R.id.ledbtn);
        //text  = (TextView)findViewById(R.id.textView1);



        if (adaptr != null) {
            System.out.println("本機擁有藍芽設備");
            if (!adaptr.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
            Set<BluetoothDevice> devices = adaptr.getBondedDevices();
            if (devices.size() > 0) {
                for (Iterator iterator = devices.iterator(); iterator.hasNext(); ) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) iterator.next();
                    System.out.println(bluetoothDevice.getAddress());
                    System.out.println("This is my phone's Address");
                }
            } else {
                System.out.println("沒有藍芽設備");
            }


        }


        blebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag=0;
                findBT();

                try {
                    openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });

        serbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getData();
/*
                try {
                    openBT();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

            }

        });


    }

    private void getData() {
        /*
        String id = txview.getText().toString().trim();
        if (id.equals("")) {
            Toast.makeText(this, "Please enter an id", Toast.LENGTH_LONG).show();
            return;
        }
        */
        loading = ProgressDialog.show(this,"Please wait...","Fetching...",false,false);


        //final String url = Config.DATA_URL+"?R_value="+parameter.get(0)+"&G_value="+parameter.get(1)+"&B_value="+parameter.get(2)+"&CSP="+parameter.get(3)+"&LCA="+parameter.get(4);
        final String url = Config.DATA_URL+"?R_value="+parameter.get(0)+"&G_value="+parameter.get(1)+"&B_value="+parameter.get(2)+"&CSP="+parameter.get(3)+"&LCA="+parameter.get(4);
        //final String url = Config.DATA_URL+"?R_value="+12+"&G_value="+12+"&B_value="+12+"&CSP="+12+"&LCA="+24;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override

            public void onResponse(String response) {

                System.out.println(url);
                loading.dismiss();
                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                if(response!=NULL) {
                    showJSON(response);
                }
                else {
                    Toast.makeText(MainActivity.this,response,Toast.LENGTH_LONG).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        //String R_value="";
        //String G_value="";
        //String B_value = "";
        String Risk_value = "";
        Risk_value = response;
        /*

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            JSONObject collegeData = result.getJSONObject(0);
            System.out.println(collegeData);
            //Toast.makeText(MainActivity.this,collegeData.getString(),Toast.LENGTH_LONG).show();
            Risk_value = collegeData.getString(Config.JSON_RISK);
            //R_value = collegeData.getString(Config.JSON_R);
            //G_value = collegeData.getString(Config.JSON_G);
            //B_value = collegeData.getString(Config.JSON_B);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //textViewResult.setText("Name:\t"+R_value+"\nAddress:\t" +G_value+ "\nVice Chancellor:\t"+ B_value);
        */
        Risk_value +="%";
        //Toast.makeText(MainActivity.this,Risk_value,Toast.LENGTH_LONG).show();

        txview.setText(Risk_value);
        //Toast.makeText(MainActivity.this,"2222222",Toast.LENGTH_LONG).show();
    }





    void findBT() {
        System.out.println("findBT");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)//取得手機是否有藍芽裝置
        {
            //myLable.setText("NO bluetooth adapter available");
        }
        if (!mBluetoothAdapter.isEnabled())//檢查手機是否有開啟藍芽，若無則跳到Android藍芽設定的畫面
        {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth,0);//找到幾個藍芽周邊
        }
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

//"20:16:06:29:36:16"/98:D3:32:30:95:1E
        if ((!pairedDevices.toString().equals("98:D3:35:71:04:F4")))//如找到裝置處理
        {
            for (BluetoothDevice device : pairedDevices) {
                // myLable.setText(device.getName());//顯示該藍芽名稱
                if (device.getAddress().equals("98:D3:35:71:04:F4")) {
                    mmDevice = device;
                    //Toast.makeText(this, mmDevice.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    //"20:15:03:16:08:82"
    @TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        if (mmDevice.toString().equals("98:D3:35:71:04:F4"))//如果有找到設備
        {
            //Toast.makeText(MainActivity.this, "openin", Toast.LENGTH_SHORT).show();

            mmSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);//建立連線
            mmSocket.connect();//連線藍芽

            mmOutputStream = mmSocket.getOutputStream();//傳送指令
            mmInputStream = mmSocket.getInputStream();//接收指令
            System.out.println("傳送指令");
            beginListenForData();//開始接收資料函數
            //txw.setText("Bluetooth Opened:" + mmDevice.getName() + "" + mmDevice.getAddress());//同時顯示藍芽名稱和位址
            //myLable.setText("");
        }
    }

    /*
    void sendData() throws IOException
    {
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
    }
    */

    void beginListenForData() {
        //System.out.println("beginListenForData");
        final Handler handler = new Handler();
        final byte delimiter = 10;  //將十進制轉換成ASCII Code LF(line feed-換行)
        stopWorker = false;       //監控多執行緒的運作（false為開啟）
        readBufferPosition = 0;  //readBuffer陣列位置，預設為0
        //roadBufferPosition = 0;  //roadDetection陣列位置，預設為0
        //roadDetection = new int[100];//宣告一個int陣列型別的陣列
        readBuffer = new byte[1024];//宣告一個byte陣列型別的陣列

        workerThread = new Thread(new Runnable() //建立Thread物件時，Runnable定義Thread要做的事情
        {
            public void run() //Thread物件會調用Runnable物件的run()方法，來控制。
            {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {//監控Thread物件是否運作，是的話就進入迴圈，不是就跳出迴圈
                    try {
                        int bytesAvailable = mmInputStream.available();//宣告接受資料變數
                        //System.out.println(bytesAvailable);
                        if (bytesAvailable > 0)//如果有資料進來
                        {

                            byte[] packetBytes = new byte[bytesAvailable];//宣告byte陣列，數量由bytesAvailable決定
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];//將資料一個個從packetBytes取出值到b變數
                                if (b == delimiter) //若b是換行指令
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];//宣告encodeBytes陣列
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    BTdata = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {

                                        public void run()//利用handler服務窗口顯示從encodedBytes轉換的文字
                                        {
                                            //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                            String value = BTdata;
                                            String[] arrays = value.split(" ");
                                            int j=0;
                                            for(String s : arrays)
                                            {
                                                parameter.add(s);
                                                j=j+1;
                                            }


                                            //Toast.makeText(MainActivity.this, parameter.get(0), Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, parameter.get(1), Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, parameter.get(2), Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, parameter.get(3), Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(MainActivity.this, parameter.get(4), Toast.LENGTH_SHORT).show();

                                            if(flag==0) {
                                                    Toast.makeText(MainActivity.this, "連接成功", Toast.LENGTH_SHORT).show();
                                                flag=flag+1;

                                            }

    //while(parameter.get(0)!=NULL&&parameter.get(1)!=NULL&&parameter.get(2)!=NULL&&parameter.get(3)!=NULL&&parameter.get(4)!=NULL) {
                                                //getData();
                                                //break;
                                            /*
                                                Toast.makeText(MainActivity.this, parameter.get(0), Toast.LENGTH_SHORT).show();
                                                Toast.makeText(MainActivity.this, parameter.get(1), Toast.LENGTH_SHORT).show();
                                                Toast.makeText(MainActivity.this, parameter.get(2), Toast.LENGTH_SHORT).show();
                                                Toast.makeText(MainActivity.this, parameter.get(3), Toast.LENGTH_SHORT).show();
                                                Toast.makeText(MainActivity.this, parameter.get(4), Toast.LENGTH_SHORT).show();
                                            */

                                            //}
                                            //stopWorker = true;
                                           // txview.setText(String.valueOf(seconds));

                                            //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();


                                        }
                                    });
                                }


                                else //若沒換行 一直存進來
                                {
                                    readBuffer[readBufferPosition++] = b;//將接受到資料放入陣列裡面
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;//將多執行緒中止
                    }
                }
            }
        });
        workerThread.start();//開始多執行緒的工作
    }
//----------------------以上為藍芽各個function------------------------


}