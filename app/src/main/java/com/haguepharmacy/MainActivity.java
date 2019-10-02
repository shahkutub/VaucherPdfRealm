package com.haguepharmacy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haguepharmacy.model.DrugInfo;
import com.haguepharmacy.model.DrugResponse;
import com.haguepharmacy.model.SpinnerModel;
import com.haguepharmacy.search.Contact;
import com.haguepharmacy.search.ContactsAdapter;
import com.haguepharmacy.search.MyDividerItemDecoration;
import com.haguepharmacy.utils.AlertMessage;
import com.haguepharmacy.utils.Api;
import com.haguepharmacy.utils.AppConstant;
import com.haguepharmacy.utils.FileUtils;
import com.haguepharmacy.utils.MyBounceInterpolator;
import com.haguepharmacy.utils.NetInfo;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements ContactsAdapter.ContactsAdapterListener{

    Context context;
    private Button btnChooseFile;
    private final static int IMAGE_RESULT = 200;
    private static final int PERMISSION_REQUEST_CODE = 300;
    private String userPhotoPath;
    private TextView tvFileName,tvSubTotal,tvDiscount,tvServiceCharge,tvNetTotal;
    List<DrugInfo> drugList = new ArrayList<>();
    private AppCompatButton btnAdd,btnSubmit;
    private  RecyclerView recyclerList;
    private List<SpinnerModel> drugNameSpinnerList = new ArrayList<>();
    private String drugid,drugName;
    private ImageView imagePrescription;
    private LinearLayout linProductView;

    List<String> drug_idList = new ArrayList<>();
    List<String> drug_nameList = new ArrayList<>();
    List<String> quantityList = new ArrayList<>();
    List<String> unit_priceList = new ArrayList<>();
    List<String> total_priceList = new ArrayList<>();

    private EditText etMobile,etAddress;
    private LinearLayout llScroll;
    private Bitmap bitmap;


    private RecyclerView recycler_view_search_list;
    private ContactsAdapter mAdapter;
    private SearchView searchView;
    private String listDrug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_page);
        context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getQuestion();
        initUi();

        tollBarSearch();
    }

    private void tollBarSearch() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
       // getSupportActionBar().setTitle(R.string.toolbar_title);



    }

    private void initUi() {
        llScroll = (LinearLayout) findViewById(R.id.llScroll);
        linProductView = (LinearLayout)findViewById(R.id.linProductView);

        imagePrescription = (ImageView)findViewById(R.id.imagePrescription);

        etMobile = (EditText) findViewById(R.id.etMobile);
        etAddress = (EditText) findViewById(R.id.etAddress);

        tvFileName = (TextView)findViewById(R.id.tvFileName);
        tvSubTotal = (TextView)findViewById(R.id.tvSubTotal);
        tvDiscount = (TextView)findViewById(R.id.tvDiscount);
        tvServiceCharge = (TextView)findViewById(R.id.tvServiceCharge);
        tvNetTotal = (TextView)findViewById(R.id.tvNetTotal);
        btnChooseFile = (Button)findViewById(R.id.btnChooseFile);

        btnChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
            }
        });

        if(!checkPermission()){
            requestPermission();
        }



        recyclerList = (RecyclerView)findViewById(R.id.recyclerList);



        btnAdd = (AppCompatButton) findViewById(R.id.btnAdd);
        btnSubmit = (AppCompatButton) findViewById(R.id.btnSubmit);

//        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
//        // Use bounce interpolator with amplitude 0.2 and frequency 20
//        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
//        myAnim.setInterpolator(interpolator);
//        btnAdd.startAnimation(myAnim);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productAddDialogue();
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                Gson gson = new Gson();
                listDrug = gson.toJson(drugList);
                Log.e("drug_List", listDrug);

                if(drugList.size()>0){

                    if(TextUtils.isEmpty(etMobile.getText().toString())){
                        Toast.makeText(context, "Please input mobile number", Toast.LENGTH_SHORT).show();
                        etMobile.requestFocus();
                    }
                    else if(TextUtils.isEmpty(etAddress.getText().toString())){
                        Toast.makeText(context, "Please input address", Toast.LENGTH_SHORT).show();
                        etAddress.requestFocus();
                    }
                    else {
                        submitDrug();
                       // submitDrugMaltipart();
                    }
                }else {

                    if(TextUtils.isEmpty(etMobile.getText().toString())){
                        Toast.makeText(context, "Please input mobile number", Toast.LENGTH_SHORT).show();
                        etMobile.requestFocus();
                    }
                    else if(TextUtils.isEmpty(etAddress.getText().toString())){
                        Toast.makeText(context, "Please input address", Toast.LENGTH_SHORT).show();
                        etAddress.requestFocus();
                    }
                    else if(TextUtils.isEmpty(userPhotoPath)){
                        Toast.makeText(context, "Please Take Prescription photo", Toast.LENGTH_SHORT).show();
                        etAddress.requestFocus();
                    }

                    else {
                        submitPrescription();
                    }
                }





            }
        });
    }

    // Function to convert ArrayList<String> to String[]
//    public static String[] GetStringArray(ArrayList<DrugInfo> arr)
//    {
//
//        // declaration and initialise String Array
//        String str[] = new String[arr.size()];
//
//        // Convert ArrayList to object array
//        Object[] objArr = arr.toArray();
//
//        // Iterating and converting to String
//        int i = 0;
//        for (Object obj : objArr) {
//            str[i++] = (String)obj;
//        }
//
//        return str;
//    }

    private void productAddDialogue() {
        //Dialog dialog = new Dialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogue_order);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        final ImageView imgCancel = (ImageView) dialog.findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final EditText etUnitPrice = (EditText) dialog.findViewById(R.id.etUnitPrice);
        final EditText etQuantity = (EditText) dialog.findViewById(R.id.etQuantity);
        final EditText etTotalPrice = (EditText) dialog.findViewById(R.id.etTotalPrice);
        final SearchableSpinner spnDrugName = (SearchableSpinner)dialog.findViewById(R.id.spnDrugName);





        CustomSpinnerAdapter spinneradapter = new CustomSpinnerAdapter(MainActivity.this,
                R.layout.spinner_item, R.id.title, drugNameSpinnerList);
        spnDrugName.setAdapter(spinneradapter);
        spnDrugName.setTitle("Select Item");
        spnDrugName.setPositiveButton("OK");

        spnDrugName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etTotalPrice.setText("");
                etUnitPrice.setText("");
                etQuantity.setText("");

                if(position>0){
                    drugName = spnDrugName.getSelectedItem().toString();
                    drugid = drugNameSpinnerList.get(position).getDrug_id();
                    etUnitPrice.setText(drugNameSpinnerList.get(position).getMrp());
                    etQuantity.requestFocus();
                }else {
                    etTotalPrice.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        etUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float unitPrice;
                if(!TextUtils.isEmpty(etUnitPrice.getText().toString())&& !TextUtils.isEmpty(etQuantity.getText().toString())){
                    unitPrice = Float.parseFloat(etUnitPrice.getText().toString());
                    float total = unitPrice*Float.parseFloat(etQuantity.getText().toString());
                    etTotalPrice.setText(String.valueOf(total));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                float unitPrice;
                if(!TextUtils.isEmpty(etUnitPrice.getText().toString())&& !TextUtils.isEmpty(etQuantity.getText().toString())){
                    unitPrice = Float.parseFloat(etUnitPrice.getText().toString());
                    float total = unitPrice*Float.parseFloat(etQuantity.getText().toString());
                    etTotalPrice.setText(String.valueOf(total));
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        AppCompatButton btnAddDrug = (AppCompatButton) dialog.findViewById(R.id.btnAddDrug);

        btnAddDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(TextUtils.isEmpty(drugName)){
                   Toast.makeText(context, "Select drug name", Toast.LENGTH_SHORT).show();
               }else if(TextUtils.isEmpty(etTotalPrice.getText().toString())) {
                   Toast.makeText(context, "input quantity", Toast.LENGTH_SHORT).show();
               }else {
                   DrugInfo drugInfo = new DrugInfo();
                   drugInfo.setDrug_id(drugid);
                   drugInfo.setDrug_name(drugName);
                   drugInfo.setUnit_price(etUnitPrice.getText().toString());
                   drugInfo.setQuantity(etQuantity.getText().toString());
                   drugInfo.setTotal_price(etTotalPrice.getText().toString());

                   drugList.add(drugInfo);

                   ListAdapter listAdapter = new ListAdapter(drugList, context);
                   recyclerList.setHasFixedSize(true);
                   recyclerList.setLayoutManager(new LinearLayoutManager(context));
                   recyclerList.setNestedScrollingEnabled(false);
                   recyclerList.setAdapter(listAdapter);

                   getFinalCalculation(drugList);

                   if(drugList.size()>0){
                       linProductView.setVisibility(View.VISIBLE);
                   }
                   dialog.dismiss();
               }


            }
        });

        dialog.show();

    }

    private void getFinalCalculation(List<DrugInfo> list) {

        float total = 0;
        for (DrugInfo info: list) {
            total+= Float.parseFloat(info.getTotal_price());
        }
        tvSubTotal.setText(""+total);

        tvDiscount.setText(String.valueOf(total*10/100));
        tvServiceCharge.setText(String.valueOf(total*15/100));
        tvNetTotal.setText(String.valueOf(total+(total*15/100)-(total*10/100)));

    }


    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("*/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
                                 final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == IMAGE_RESULT) {

                File path = new File(getImageFilePath(data));
                //userPhotoPath = FileUtils.getPath(context,data.getData());
                if (path != null) {
                    userPhotoPath = getImageFilePath(data);
                    String filename=userPhotoPath.substring(userPhotoPath.lastIndexOf("/")+1);
                    tvFileName.setText(filename);
                    Log.e("userPicPath",""+userPhotoPath);
                    Bitmap bitmap = BitmapFactory.decodeFile(userPhotoPath);
                    imagePrescription.setImageBitmap(bitmap);
                }
            }

        }

    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "prescription.png"));
        }
        return outputFileUri;
    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private boolean checkPermission() {
        //int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        //int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        //int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return
                //result == PackageManager.PERMISSION_GRANTED
                result1 == PackageManager.PERMISSION_GRANTED
                        //&& result2 == PackageManager.PERMISSION_GRANTED
                        && result2 == PackageManager.PERMISSION_GRANTED
                        && result3 == PackageManager.PERMISSION_GRANTED;
        //&& result5 == PackageManager.PERMISSION_GRANTED;

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                        //ACCESS_FINE_LOCATION,
                        CAMERA,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                },
                PERMISSION_REQUEST_CODE);

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhoneAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    //  Toast.makeText(con, ""+imei, Toast.LENGTH_SHORT).show();

                    if (locationAccepted && cameraAccepted && readPhoneAccepted) {
                        // Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show();
//                        if (mgr.mGoogleApiClient == null) {
//                            mgr.buildGoogleApiClient();
//                        }
                    } else {

                        //Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ CAMERA,
                                                                    READ_EXTERNAL_STORAGE,
                                                                    WRITE_EXTERNAL_STORAGE,},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onContactSelected(SpinnerModel contact) {

        //searchView.setQuery(contact.getDrug_name(), false);
        searchView.clearFocus();

        DrugInfo drugInfo = new DrugInfo();
        drugInfo.setDrug_id(contact.getDrug_id());
        drugInfo.setDrug_name(contact.getDrug_name());
        drugInfo.setUnit_price(contact.getMrp());
        drugInfo.setQuantity("10");
        drugInfo.setTotal_price(String.valueOf(Integer.parseInt(contact.getMrp())*10));

        drugList.add(drugInfo);

        ListAdapter listAdapter = new ListAdapter(drugList, context);
        recyclerList.setHasFixedSize(true);
        recyclerList.setLayoutManager(new LinearLayoutManager(context));
        recyclerList.setNestedScrollingEnabled(false);
        recyclerList.setAdapter(listAdapter);

        getFinalCalculation(drugList);

        recycler_view_search_list.setVisibility(View.GONE);
        if(drugList.size()>0){
            linProductView.setVisibility(View.VISIBLE);
        }
    }


    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

        Context context;
        List<DrugInfo> drugInfoList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageButton  minus;
           // ImageButton  plus;
            EditText etItemDrugName,etItemUnitPrice,etItemQuantity,etItemTotal;

            public ViewHolder(View itemView) {
                super(itemView);
               // plus = (ImageButton) itemView.findViewById(R.id.plus);
                minus = (ImageButton) itemView.findViewById(R.id.minus);
                etItemDrugName = (EditText) itemView.findViewById(R.id.etItemDrugName);
                etItemUnitPrice = (EditText) itemView.findViewById(R.id.etItemUnitPrice);
                etItemQuantity = (EditText) itemView.findViewById(R.id.etItemQuantity);
                etItemTotal = (EditText) itemView.findViewById(R.id.etItemTotal);

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        try {
                            drugInfoList.remove(position);
                            getFinalCalculation(drugInfoList);
                            notifyItemRemoved(position);

                            if(drugInfoList.size()==0){
                                linProductView.setVisibility(View.GONE);
                            }
                        }catch (ArrayIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                });

//                plus.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        int position = getAdapterPosition();
//
//                        DrugInfo drugInfo = new DrugInfo();
//
//                        try {
//                            drugInfoList.add(position + 1, drugInfo);
//                            notifyItemInserted(position + 1);
//                        }catch (ArrayIndexOutOfBoundsException e){e.printStackTrace();}
//                    }
//                });


            }
        }


        public ListAdapter(List<DrugInfo> drugInfoList, Context context){
            this.drugInfoList = drugInfoList;
            this.context = context;
        }


        @Override
        public int getItemCount() {
            return drugInfoList.size();
        }


        @Override
        public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order, viewGroup, false);
            return new ViewHolder(v);
        }


        @Override
        public void onBindViewHolder(ListAdapter.ViewHolder holder, final int position) {

            int x = holder.getLayoutPosition();

            final DrugInfo data = drugInfoList.get(position);
            holder.etItemDrugName.setText(data.getDrug_name());
            holder.etItemUnitPrice.setText(data.getUnit_price());
            holder.etItemQuantity.setText(data.getQuantity());
            holder.etItemTotal.setText(data.getTotal_price());



        }


        public List<DrugInfo> getStepList(){
            return drugInfoList;
        }
    }


    public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerModel> {

        LayoutInflater flater;
        List<SpinnerModel> list = new ArrayList<>();

        public CustomSpinnerAdapter(Activity context, int resouceId, int textviewId, List<SpinnerModel> list){

            super(context,resouceId,textviewId, list);
            flater = context.getLayoutInflater();
            this.list = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            SpinnerModel rowItem = getItem(position);

            View rowview = flater.inflate(R.layout.spinner_item,null,true);

            TextView txtTitle = (TextView) rowview.findViewById(R.id.title);

            if(rowItem.getDrug_name()!=null){
                txtTitle.setText(rowItem.getDrug_name().toString());
            }


            return rowview;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = flater.inflate(R.layout.spinner_item,parent, false);
            }
            SpinnerModel rowItem = getItem(position);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
            if(rowItem.getDrug_name()!=null){
                txtTitle.setText(rowItem.getDrug_name().toString());
            }

            return convertView;
        }
    }


    private void getQuestion() {

        if(!NetInfo.isOnline(context)){
            AlertMessage.showMessage(context,"Alert!","No internet connection!");
        }

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading....");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Api api = retrofit.create(Api.class);
        Call<DrugResponse> userCall = api.getDrugList();
        userCall.enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(Call<DrugResponse> call, Response<DrugResponse> response) {
                pd.dismiss();
                DrugResponse drugResponse =  new DrugResponse();
                drugResponse = response.body();

                if(drugResponse!=null){
                    if(drugResponse.getRecords().size()>0){
                        drugNameSpinnerList.clear();

                        SpinnerModel select = new SpinnerModel();
                        select.setDrug_name("Select Drug");
                        drugNameSpinnerList = drugResponse.getRecords();

                        drugNameSpinnerList.add(0,select);

                        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce);
                        // Use bounce interpolator with amplitude 0.2 and frequency 20
                        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
                        myAnim.setInterpolator(interpolator);
                        btnAdd.startAnimation(myAnim);

                        Toast.makeText(context, " data found"+drugResponse.getRecords().size(), Toast.LENGTH_SHORT).show();
                        //search list
                        recycler_view_search_list = findViewById(R.id.recycler_view_search_list);
                        mAdapter = new ContactsAdapter(context, drugNameSpinnerList, (ContactsAdapter.ContactsAdapterListener) context);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_view_search_list.setLayoutManager(mLayoutManager);
                        recycler_view_search_list.setItemAnimator(new DefaultItemAnimator());
                        recycler_view_search_list.addItemDecoration(new MyDividerItemDecoration(context, DividerItemDecoration.VERTICAL, 36));
                        recycler_view_search_list.setAdapter(mAdapter);

                    }

                }else {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<DrugResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void submitPrescription() {

        if(!NetInfo.isOnline(context)){
            AlertMessage.showMessage(context,"Alert!","No internet connection!");
        }

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading....");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        File file = new File(userPhotoPath);

        // Parsing any Media type file
        //RequestBody requestBody=RequestBody.create(MediaType.parse("application/pdf"), file);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        RequestBody customer_mobile = RequestBody.create(MediaType.parse("text/plain"), etMobile.getText().toString());
        RequestBody customer_address = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString());
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody sub_total = RequestBody.create(MediaType.parse("text/plain"), tvSubTotal.getText().toString());
        RequestBody service_charge = RequestBody.create(MediaType.parse("text/plain"), tvServiceCharge.getText().toString());
        RequestBody discount = RequestBody.create(MediaType.parse("text/plain"), tvDiscount.getText().toString());
        RequestBody net_total = RequestBody.create(MediaType.parse("text/plain"), tvNetTotal.getText().toString());
        RequestBody drugs = RequestBody.create(MediaType.parse("text/plain"), drugList.toString());
        RequestBody drug_id = RequestBody.create(MediaType.parse("text/plain"), drug_idList.toString());
        RequestBody drug_name = RequestBody.create(MediaType.parse("text/plain"), drug_nameList.toString());
        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), quantityList.toString());
        RequestBody unit_price = RequestBody.create(MediaType.parse("text/plain"), unit_priceList.toString());
        RequestBody total_price = RequestBody.create(MediaType.parse("text/plain"), total_priceList.toString());

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> userCall = api.submitPrescription(customer_mobile,customer_address,fileToUpload);
        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();


                if(response!=null){
                    Toast.makeText(context, "Data uploaded", Toast.LENGTH_SHORT).show();
                    drugList.clear();

                }else {
                    Toast.makeText(context, "Data not uploaded! Please try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(context, "Data upload failed", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void submitDrug() {

        if(!NetInfo.isOnline(context)){
            AlertMessage.showMessage(context,"Alert!","No internet connection!");
        }

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading....");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //File file = new File(userPhotoPath);

        // Parsing any Media type file
        //RequestBody requestBody=RequestBody.create(MediaType.parse("application/pdf"), file);

//        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
//        RequestBody customer_mobile = RequestBody.create(MediaType.parse("text/plain"), etMobile.getText().toString());
//        RequestBody customer_address = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString());
//        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
//        RequestBody sub_total = RequestBody.create(MediaType.parse("text/plain"), tvSubTotal.getText().toString());
//        RequestBody service_charge = RequestBody.create(MediaType.parse("text/plain"), tvServiceCharge.getText().toString());
//        RequestBody discount = RequestBody.create(MediaType.parse("text/plain"), tvDiscount.getText().toString());
//        RequestBody net_total = RequestBody.create(MediaType.parse("text/plain"), tvNetTotal.getText().toString());
//        RequestBody drugs = RequestBody.create(MediaType.parse("text/plain"), drugList.toString());
//        RequestBody drug_id = RequestBody.create(MediaType.parse("text/plain"), drug_idList.toString());
//        RequestBody drug_name = RequestBody.create(MediaType.parse("text/plain"), drug_nameList.toString());
//        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), quantityList.toString());
//        RequestBody unit_price = RequestBody.create(MediaType.parse("text/plain"), unit_priceList.toString());
//        RequestBody total_price = RequestBody.create(MediaType.parse("text/plain"), total_priceList.toString());

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> userCall = api.submitDrug(etMobile.getText().toString(),etAddress.getText().toString(),
                tvSubTotal.getText().toString(),tvServiceCharge.getText().toString(),tvDiscount.getText().toString(),
                tvNetTotal.getText().toString(),drugList.toString());
        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();


                if(response!=null){
                    Toast.makeText(context, "Data uploaded", Toast.LENGTH_SHORT).show();
                    drugList.clear();

                }else {
                    Toast.makeText(context, "Data not uploaded! Please try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(context, "Data upload failed", Toast.LENGTH_SHORT).show();

            }
        });


    }



    private void submitDrugMaltipart() {

        if(!NetInfo.isOnline(context)){
            AlertMessage.showMessage(context,"Alert!","No internet connection!");
        }

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading....");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        File file = new File("file");

        // Parsing any Media type file
        //RequestBody requestBody=RequestBody.create(MediaType.parse("application/pdf"), file);

        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        RequestBody customer_mobile = RequestBody.create(MediaType.parse("text/plain"), etMobile.getText().toString());
        RequestBody customer_address = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString());
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody sub_total = RequestBody.create(MediaType.parse("text/plain"), tvSubTotal.getText().toString());
        RequestBody service_charge = RequestBody.create(MediaType.parse("text/plain"), tvServiceCharge.getText().toString());
        RequestBody discount = RequestBody.create(MediaType.parse("text/plain"), tvDiscount.getText().toString());
        RequestBody net_total = RequestBody.create(MediaType.parse("text/plain"), tvNetTotal.getText().toString());
        RequestBody drugs = RequestBody.create(MediaType.parse("text/plain"), drugList.toString());
        RequestBody drug_id = RequestBody.create(MediaType.parse("text/plain"), drug_idList.toString());
        RequestBody drug_name = RequestBody.create(MediaType.parse("text/plain"), drug_nameList.toString());
        RequestBody quantity = RequestBody.create(MediaType.parse("text/plain"), quantityList.toString());
        RequestBody unit_price = RequestBody.create(MediaType.parse("text/plain"), unit_priceList.toString());
        RequestBody total_price = RequestBody.create(MediaType.parse("text/plain"), total_priceList.toString());

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> userCall = api.submitDrugMultipart(customer_mobile,customer_address,fileToUpload,sub_total,
                service_charge,discount,net_total,drugs);
        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pd.dismiss();


                if(response!=null){
                    Toast.makeText(context, "Data uploaded", Toast.LENGTH_SHORT).show();
                    drugList.clear();

                }else {
                    Toast.makeText(context, "Data not uploaded! Please try again", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(context, "Data upload failed", Toast.LENGTH_SHORT).show();

            }
        });


    }


    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createPdf(){
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //  Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels ;
        float width = displaymetrics.widthPixels ;

        int convertHighet = (int) hight, convertWidth = (int) width;

//        Resources mResources = getResources();
//        Bitmap bitmap = BitmapFactory.decodeResource(mResources, R.drawable.screenshot);

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        Paint paint = new Paint();
        canvas.drawPaint(paint);

        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);

        paint.setColor(Color.BLUE);
        canvas.drawBitmap(bitmap, 0, 0 , null);
        document.finishPage(page);

        // write the document content
        String targetPdf = "/sdcard/pdffromScroll.pdf";
        File filePath;
        filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        // close the document
        document.close();
        Toast.makeText(this, "PDF of Scroll is created!!!", Toast.LENGTH_SHORT).show();

        openGeneratedPDF();

    }

    private void openGeneratedPDF(){
        File file = new File("/sdcard/pdffromScroll.pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                Log.e("onQueryTextSubmit","onQueryTextSubmit");
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed

                Log.e("onQueryTextChange","onQueryTextChange");
                linProductView.setVisibility(View.GONE);
                recycler_view_search_list.setVisibility(View.VISIBLE);
                mAdapter.getFilter().filter(query);
                return false;
            }
        });

       searchView.setOnSearchClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.e("SearchClick","SearchClick");


               linProductView.setVisibility(View.GONE);
               recycler_view_search_list.setVisibility(View.VISIBLE);
           }
       });

       searchView.setOnCloseListener(new SearchView.OnCloseListener() {
           @Override
           public boolean onClose() {
               Log.e("CloseListener","CloseListener");

               if(drugList.size()>0){
                   linProductView.setVisibility(View.VISIBLE);
               }
               recycler_view_search_list.setVisibility(View.GONE);
               return false;
           }
       });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {

            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
