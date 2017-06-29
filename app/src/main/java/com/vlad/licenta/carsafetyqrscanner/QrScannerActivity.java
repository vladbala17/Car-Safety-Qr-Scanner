package com.vlad.licenta.carsafetyqrscanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.google.firebase.auth.FirebaseAuth;


public class QrScannerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = QrScannerActivity.class.getSimpleName();
    private static final int ZBAR_SCANNER_REQUEST = 5;
    private static final String USER_EMAIL = "user_email";
    private static final String CAR_NAME = "car_name";
    private TextView userEmailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");

        setContentView(R.layout.activity_qr_scanner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra(USER_EMAIL);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        userEmailTextView = (TextView) headerView.findViewById(R.id.user_email_tv);
        userEmailTextView.setText(userEmail);
        final BarcodeFragment fragment = (BarcodeFragment) getSupportFragmentManager().findFragmentById(R.id.barCode);
        fragment.setScanResultHandler(new IScanResultHandler() {
            @Override
            public void scanResult(final ScanResult scanResult) {
                new AlertDialog.Builder(QrScannerActivity.this)
                        .setTitle(R.string.dialog_title)
                        .setMessage(Html.fromHtml("<b>" + scanResult.getRawResult().getText() + "</b>" + getString(R.string.action_needed)))
                        .setCancelable(true)

                        .setPositiveButton(R.string.car_instructions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent pdfIntent = new Intent(QrScannerActivity.this, PDFViewerActivity.class);
                                pdfIntent.putExtra(CAR_NAME,scanResult.getRawResult().getText());
                                startActivity(pdfIntent);
                            }
                        })
                        .setNegativeButton(R.string.scan_again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                fragment.restart();
                            }
                        })
                        .create()
                        .show();

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {

        } else if (id == R.id.nav_log_out) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
