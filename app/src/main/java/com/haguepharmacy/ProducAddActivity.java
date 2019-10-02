package com.haguepharmacy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.haguepharmacy.model.ProductInfo;
import com.haguepharmacy.model.ProductSpinnerModel;

import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

public class ProducAddActivity extends AppCompatActivity {

    Context context;
    Realm realm;
    private EditText etProductName,etPrice;
    private AppCompatButton btnAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add);
        context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Realm.init(context);
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        initUi();

    }

    private void initUi() {
        etProductName = (EditText)findViewById(R.id.etProductName);
        etPrice = (EditText)findViewById(R.id.etPrice);
        btnAdd = (AppCompatButton) findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(etProductName.getText().toString())){
                    Toast.makeText(context, "Enter Prduct Name.", Toast.LENGTH_SHORT).show();
                }else {
                    if(TextUtils.isEmpty(etPrice.getText().toString())){
                        etPrice.setText("0");
                    }


                    ProductSpinnerModel user = realm.createObject(ProductSpinnerModel.class); //todo  Create a new object
                    user.setName(etProductName.getText().toString()); //todo get user name from Edittext and store in user object
                    user.setPrice(etPrice.getText().toString()); //todo get user age from Edittext and store in user object
                    realm.commitTransaction();
                    // commit transaction
                    // Toast.makeText(context, "User added", Toast.LENGTH_LONG).show();
                    // clearText();


                    RealmResults<ProductInfo> result = realm.where(ProductInfo.class)
                            //.lessThan("age", 45)//find all users with age less than 45
                            .findAll();//ret

                    for (int i = 0; i <result.size() ; i++) {

                        Log.e("name",""+result.get(i).getName());
                        Log.e("price",""+result.get(i).getPrice());

                    }


                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < result.size(); i++) {
                        stringBuilder.append(result.get(i).getName() + "  ");
                    }

                    startActivity(new Intent(context,VoucharMakeActivity.class));
                }


            }
        });

    }

}
