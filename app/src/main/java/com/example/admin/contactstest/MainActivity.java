package com.example.admin.contactstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 读取联系人姓名和电话
 */

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<String> adapter;
    List<String> contactsList = new ArrayList<>();
    ListView contactView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contactView = (ListView) findViewById(R.id.contacts_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList);
        contactView.setAdapter(adapter);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //如果没有授权，调用ActivityCompat.requestPermissions()申请授权
            /**
             * 第一个参数是activity的实例
             * 第二个参数是String数组，我们把要申请的权限名放在数组中即可
             * 第三个参数是请求码，只要是唯一值就可以
             */
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            readContacts(); //如果已经授权，直接读取联系人
        }
    }

    private void readContacts() {
        Cursor cursor = null;
        try {
            //查询系统的联系人数据
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    //获取联系人姓名
                    String diaplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //获取联系人手机号
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactsList.add(diaplayName + "\n" + number);

                }
                adapter.notifyDataSetChanged();  //刷新ListView
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }

    //不管是同意或者拒绝我们的权限申请，最终都会回调到onRequestPermissionsResult（）方法中，而授权的结果会封装在grantResults参数中
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                //判断下最后的授权结果，如果用户同意就调用call（）方法，如果用户拒绝只能放弃操作，并提示用户
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
}
