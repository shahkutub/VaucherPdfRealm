package com.haguepharmacy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.haguepharmacy.model.DrugInfo;
import com.haguepharmacy.model.ProductInfo;
import com.haguepharmacy.utils.AlertMessage;
import com.haguepharmacy.utils.AppConstant;
import com.haguepharmacy.utils.NetInfo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import cz.msebera.android.httpclient.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfActivity extends AppCompatActivity {

    Context context;
    private ScrollView scroll;
    private TextView tvName,tvDate,tvAddress,tvMobile,tvSubTotal,tvDiscount,tvServiceCharge,tvNetTotal;
    private AppCompatButton btnAdd,btnSubmit;
    private RecyclerView recyclerList;
    LinearLayout my_linear_layout;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);
        context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        intUi();
    }

    private void intUi() {

        scroll = (ScrollView) findViewById(R.id.scroll);
        my_linear_layout = (LinearLayout) findViewById(R.id.my_linear_layout);
        tvName = (TextView)findViewById(R.id.tvName);

        tvName.setText("নাম: "+AppConstant.customerName);

        tvDate = (TextView)findViewById(R.id.tvDate);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        tvAddress.setText("ঠিকানা: "+AppConstant.customerAddress);

        tvMobile = (TextView)findViewById(R.id.tvMobile);
        tvMobile.setText("মোবাইল: "+AppConstant.customerMobile);

        tvSubTotal = (TextView)findViewById(R.id.tvSubTotal);
        tvDiscount = (TextView)findViewById(R.id.tvDiscount);
        tvServiceCharge = (TextView)findViewById(R.id.tvServiceCharge);
        tvNetTotal = (TextView)findViewById(R.id.tvNetTotal);

        recyclerList = (RecyclerView)findViewById(R.id.recyclerList);
        btnSubmit = (AppCompatButton) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                btnSubmit.setVisibility(View.GONE);
//                Log.d("size"," "+llScroll.getWidth() +"  "+llScroll.getHeight());
//                Log.d("size"," "+llScroll.getChildAt(0).getWidth()+"  "+llScroll.getChildAt(0).getHeight());
//                bitmap = loadBitmapFromView(llScroll, llScroll.getWidth(), llScroll.getHeight());
                //bitmap = loadBitmapFromView(llScroll, 350, 900);
                //takeScreenShot();
               // createPdf();

                loadBitmapFromView(my_linear_layout);
                createPdf();
            }
        });

        ListAdapter listAdapter = new ListAdapter(AppConstant.pdfproductList, context);
        recyclerList.setHasFixedSize(true);
        recyclerList.setLayoutManager(new LinearLayoutManager(context));
        recyclerList.setNestedScrollingEnabled(false);
        recyclerList.setAdapter(listAdapter);

        getFinalCalculation(AppConstant.pdfproductList);
    }

    private void getFinalCalculation(List<ProductInfo> list) {

        float total = 0;
        for (ProductInfo info: list) {
            total+= Float.parseFloat(info.getTotal_price());
        }
        tvSubTotal.setText(""+total);

        tvDiscount.setText(String.valueOf(total*10/100));
        tvServiceCharge.setText(String.valueOf(total*15/100));
        tvNetTotal.setText(String.valueOf(total+(total*15/100)-(total*10/100)));

    }

    private class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

        Context context;
        List<ProductInfo> drugInfoList = new ArrayList<>();

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageButton  minus;
            // ImageButton  plus;
            EditText etItemDrugName,etItemUnitPrice,etItemQuantity,etItemTotal;

            public ViewHolder(View itemView) {
                super(itemView);
                // plus = (ImageButton) itemView.findViewById(R.id.plus);
                minus = (ImageButton) itemView.findViewById(R.id.minus);
                minus.setVisibility(View.GONE);
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
                            getFinalCalculation(AppConstant.pdfproductList);
                            notifyItemRemoved(position);

                            if(drugInfoList.size()==0){
                                //linProductView.setVisibility(View.GONE);
                            }
                        }catch (ArrayIndexOutOfBoundsException e){
                            e.printStackTrace();
                        }
                    }
                });


            }
        }


        public ListAdapter(List<ProductInfo> drugInfoList, Context context){
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

            final ProductInfo data = drugInfoList.get(position);
            holder.etItemDrugName.setText(data.getName());
            holder.etItemUnitPrice.setText(data.getPrice());
            holder.etItemQuantity.setText(data.getQuantity());
            holder.etItemTotal.setText(data.getTotal_price());



        }


        public List<ProductInfo> getStepList(){
            return drugInfoList;
        }
    }

    public  Bitmap loadBitmapFromView(View view) {

        // width measure spec
        int widthSpec = View.MeasureSpec.makeMeasureSpec(
                view.getMeasuredWidth(), View.MeasureSpec.AT_MOST);
        // height measure spec
        int heightSpec = View.MeasureSpec.makeMeasureSpec(
                view.getMeasuredHeight(), View.MeasureSpec.AT_MOST);

        // measure the view
        view.measure(widthSpec, heightSpec);

        // set the layout sizes
        int left = view.getLeft();
        int top = view.getTop();
        int bottom = view.getBottom();
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        int scrollX = view.getScrollX();
        int scrollY = view.getScrollY();

        view.layout(left, height +top, width + left, height + bottom);

        // create the bitmap

        bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // create a canvas used to get the view's image and draw it on the
        // bitmap

        Canvas c = new Canvas(bitmap);
        // position the image inside the canvas
        c.translate(-view.getScrollX(), -view.getScrollY());
        // get the canvas
        view.draw(c);

        return bitmap;

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

        //bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);
        bitmap = Bitmap.createScaledBitmap(bitmap, scroll.getWidth(), scroll.getHeight(), true);


        paint.setColor(Color.WHITE);
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

    private void takeScreenShot()
    {
        View u = ((Activity) context).findViewById(R.id.scrollView);

        LinearLayout z = (LinearLayout) ((Activity) context).findViewById(R.id.llScroll);
        int totalHeight = z.getChildAt(0).getHeight();
        int totalWidth = z.getChildAt(0).getWidth();

        Bitmap b = getBitmapFromView(u,totalHeight,totalWidth);

        //Save bitmap
        String extr = Environment.getExternalStorageDirectory()+"/Folder/";
        String fileName = "report.jpg";
        File myPath = new File(extr, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "Screen", "screen");
        }catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth,totalHeight , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    protected void updateProfile() {
        String url = "http://css-bd.com/attendance-system/api/uploadPhoto";

        if (!NetInfo.isOnline(context)) {
            AlertMessage.showMessage(context, "Alert",
                    "Check Internet");
            return;
        }

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setCancelable(false);
        pd.setMessage("loading..");
        pd.show();

        final AsyncHttpClient client = new AsyncHttpClient();

        // String credentials = Username + ":" + Password;
        // String base64EncodedCredentials =
        // Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        // client.addHeader("Authorization", "Basic " +
        // base64EncodedCredentials);

        final RequestParams param = new RequestParams();

        try {

            //String path = PersistData.getStringData(con, AppConstant.path);
//            param.put("user_id",AppConstant.getUserdata(con).getUser_id());
//            param.put("images",new File(picture));


        } catch (final Exception e1) {
            e1.printStackTrace();
        }

        client.post(url, param, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] response) {
                // called when response HTTP status is "200 OK"

                pd.dismiss();

//                Log.e("resposne ", ">>" + new String(response));
//
//                Gson g = new Gson();
//                LoinResponse logInResponse = g.fromJson(new String(response),
//                        LoinResponse.class);
//
//                Log.e("status", "" + logInResponse.getStatus());
//
//                if (logInResponse.getStatus()==1) {
//
//                    Toast.makeText(con, logInResponse.getMessage() + "",
//                            Toast.LENGTH_LONG).show();
//
//                    finish();
//
//                } else {
//
//                    AlertMessage.showMessage(con, "Status",
//                            logInResponse.getMessage() + "");
//                    return;
//                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers,
                                  byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)

                // Log.e("errorResponse", new String(errorResponse));

                pd.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried

            }
        });

    }

}
