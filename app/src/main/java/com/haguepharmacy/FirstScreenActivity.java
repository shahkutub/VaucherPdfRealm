package com.haguepharmacy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.haguepharmacy.utils.AppConstant;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.itextpdf.text.html.HtmlTags.FONT;

public class FirstScreenActivity extends AppCompatActivity {

    Context context;
    private final static int IMAGE_RESULT = 200;
    private static final int PERMISSION_REQUEST_CODE = 300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        context = this;

        LinearLayout linFullScreen = (LinearLayout)findViewById(R.id.linFullScreen);
        linFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(context,MainActivity.class));
            }
        });

//        TimerTask task = new TimerTask() {
//
//            @Override
//            public void run() {
//
//                // go to the main activity
//                Intent nextActivity = new Intent(context,
//                        MainActivity.class);
//                startActivity(nextActivity);
//
//                // make sure splash screen activity is gone
//               //finish();
//
//            }
//
//        };
//
//        // Schedule a task for single execution after a specified delay.
//        // Show splash screen for 4 seconds
//        new Timer().schedule(task, 4000);

        if(!checkPermission()){
            requestPermission();
        }

    }

    public void onClickAdd(View view){
        //createPdfTable();
        startActivity(new Intent(context,ProducAddActivity.class));
    }
    public void onClickSale(View view){
        startActivity(new Intent(context,VoucharMakeActivity.class));
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

    private void createPdfTable(){
        //Font f = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont("solaimanlipi.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font f = new Font(bf,14);
        Paragraph paragraph = new Paragraph("বিসমিল্লাহির রাহমানির রাহিম\nAmena Traders\nপ্রতিষ্ঠাতাঃ মরহুম হাজি মুহাম্মদ বাচ্চু মিয়া\n" +
                "্রোপাইটর : মোঃ শাহ পরান\nমোবাইল:০১৭২৫৫৭৬১৫৬,০১৭৫৩৭৪৫০২০"+"\n"+ AppConstant.customerName+"\t"+AppConstant.customerMobile,
                f);
        paragraph.setAlignment(Element.ALIGN_CENTER);
        //document.add(paragraph);

        Document document = new Document();



        PdfPTable table = new PdfPTable(new float[] { 2, 1, 2 });
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell("পণ্যের নাম");
        table.addCell("Age");
        table.addCell("Location");
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j=0;j<cells.length;j++){
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        for (int i=1;i<5;i++){
            table.addCell("Name:"+i);
            table.addCell("Age:"+i);
            table.addCell("Location:"+i);
        }
        try {
            String targetPdf = "/sdcard/productTable.pdf";
            File filePath;
            filePath = new File(targetPdf);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        document.open();
        try {
            document.add(paragraph);
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();
        System.out.println("Done");

        openGeneratedPDF();
    }


    private void openGeneratedPDF(){
        File file = new File("/sdcard/productTable.pdf");
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

}
